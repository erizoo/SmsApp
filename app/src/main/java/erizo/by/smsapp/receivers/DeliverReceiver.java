package erizo.by.smsapp.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Map;
import java.util.Queue;

import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.model.Status;
import erizo.by.smsapp.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static erizo.by.smsapp.SmsStatus.SET_MESSAGES_STATUS;
import static erizo.by.smsapp.SmsStatus.STATUS_DELIVERED;
import static erizo.by.smsapp.SmsStatus.STATUS_UNDELIVERED;
import static erizo.by.smsapp.activity.MainActivity.logService;

public class DeliverReceiver extends BroadcastReceiver {

    private static final String TAG = DeliverReceiver.class.getSimpleName();

    private Queue<Message> messages;
    private Map<String, String> simSettings;

    private APIService service;
    private Integer systemErrorCounter;
    private Integer unsentMessageCounter;


    public DeliverReceiver(Queue<Message> messages, Map<String, String> simSettings, Integer systemErrorCounter, Integer unsentMessageCounter) {
        this.messages = messages;
        this.simSettings = simSettings;
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(simSettings.get("url"))
                .build();
        service = retrofit.create(APIService.class);
        this.systemErrorCounter = systemErrorCounter;
        this.unsentMessageCounter = unsentMessageCounter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Delivered sms broadcasting start");
        if (!messages.isEmpty()) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    if (messages.peek().getStatus() != null) {
                        Message message = messages.poll();
                        service.sendStatus(
                                SET_MESSAGES_STATUS,
                                simSettings.get("deviceId"),
                                simSettings.get("simId"),
                                simSettings.get("secretKey"),
                                message.getMessageID(),
                                STATUS_DELIVERED)
                                .enqueue(new Callback<Status>() {
                                    @Override
                                    public void onResponse(Call<Status> call, Response<Status> response) {
                                        if (response.body() != null) {
                                            Log.d(TAG, "Message status: " + response.body().getStatus());
                                            logService.appendLog("Message status: " + response.body().getStatus() + TAG);
                                        }
                                        systemErrorCounter = 0;
                                    }

                                    @Override
                                    public void onFailure(Call<Status> call, Throwable t) {
                                        systemErrorCounter++;
                                        Log.d(TAG, "Error get status sent " + t.getMessage());
                                        logService.appendLog(t.getMessage());
                                    }
                                });
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    unsentMessageCounter++;
                    if (messages.peek().getStatus() != null) {
                        logService.appendLog("message didn't delivered");
                        Message message = messages.poll();
                        service.sendStatus(
                                SET_MESSAGES_STATUS,
                                simSettings.get("deviceId"),
                                simSettings.get("simId"),
                                simSettings.get("secretKey"),
                                message.getMessageID(),
                                STATUS_UNDELIVERED)
                                .enqueue(new Callback<Status>() {
                                    @Override
                                    public void onResponse(Call<Status> call, Response<Status> response) {
                                        if (response.body() != null) {
                                            Log.d(TAG, "Message status: " + response.body().getStatus());
                                            logService.appendLog("Message status: " + response.body().getStatus() + TAG);
                                        }
                                        systemErrorCounter++;
                                    }

                                    @Override
                                    public void onFailure(Call<Status> call, Throwable t) {
                                        systemErrorCounter++;
                                        Log.d(TAG, "Error get status sent " + t.getMessage());
                                        logService.appendLog(t.getMessage());
                                    }
                                });
                    }
                    break;
            }
        }
    }
}
