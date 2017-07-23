package erizo.by.smsapp;

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

public class DeliverReceiver extends BroadcastReceiver implements SmsStatus {

    private static final String TAG = DeliverReceiver.class.getSimpleName();

    private Queue<Message> messages;
    private Map<String, String> simSettings;


    private APIService service;

    public DeliverReceiver(Queue<Message> messages, Map<String, String> simSettings) {
        this.messages = messages;
        this.simSettings = simSettings;
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(simSettings.get("url"))
                .build();
        service = retrofit.create(APIService.class);
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
                                        }
//                                counter = 0;
                                    }

                                    @Override
                                    public void onFailure(Call<Status> call, Throwable t) {
//                                    counter++;
                                        Log.d(TAG, "Error get status sent " + t.getMessage());
                                    }
                                });
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    if (messages.peek().getStatus() != null) {
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
                                        }
//                                    counter = 0;
                                    }
                                    @Override
                                    public void onFailure(Call<Status> call, Throwable t) {
//                                    counter++;
                                        Log.d(TAG, "Error get status sent " + t.getMessage());
                                    }
                                });
                    }
                    break;
            }
        }
    }
}
