package erizo.by.smsapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
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

import static erizo.by.smsapp.activity.MainActivity.logService;

public class SentReceiver extends BroadcastReceiver implements SmsStatus {

    private static final String TAG = SentReceiver.class.getSimpleName();

    private Queue<Message> messages;
    private Map<String, String> simSettings;

    private APIService service;
    private Integer counter;


    public SentReceiver(Queue<Message> messages, Map<String, String> simSettings, Integer counter) {
        this.messages = messages;
        this.simSettings = simSettings;
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(simSettings.get("url"))
                .build();
        this.service = retrofit.create(APIService.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Sent sms broadcasting start");
        logService.appendLog("Sent sms broadcasting start" + TAG);
        if (!messages.isEmpty())
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    if (messages.peek().getStatus() != null) {
                        for (Message message : messages) {
                            if (message.getStatus().equals(SMS_PENDING)) {
                                Log.d(TAG, "sms broadcast starting");
                                logService.appendLog("sms broadcast starting" + TAG);
                                service.sendStatus(SET_MESSAGES_STATUS,
                                        simSettings.get("deviceId"),
                                        simSettings.get("simId"),
                                        simSettings.get("secretKey"),
                                        message.getMessageID(),
                                        STATUS_SENT).enqueue(new Callback<Status>() {
                                    @Override
                                    public void onResponse(Call<Status> call, Response<Status> response) {
                                        if (response.body() != null) {
                                            Log.d(TAG, "Message status: " + response.body().getStatus());
                                            logService.appendLog("Message status: " + response.body().getStatus() + TAG);
                                            counter = 0;
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Status> call, Throwable t) {
                                        counter++;
                                        Log.d(TAG, "Error get status sent " + t.getMessage());
                                        logService.appendLog(t.getMessage());
                                    }
                                });
                                message.setStatus(SMS_SENT);
                                return;
                            }
                        }
                    }
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Log.d(TAG, "Generic failure ");
                    logService.appendLog("RESULT_ERROR_GENERIC_FAILURE");
                    if (messages.peek().getStatus() != null) {
                        Message message = messages.poll();
                        service.sendStatus(SET_MESSAGES_STATUS,
                                simSettings.get("deviceId"),
                                simSettings.get("simId"),
                                simSettings.get("secretKey"),
                                message.getMessageID(),
                                STATUS_UNSENT).enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(Call<Status> call, Response<Status> response) {
                                if (response.body() != null) {
                                    counter++;
                                    logService.appendLog("Message status: " + response.body().getStatus() + TAG);
                                    Log.d(TAG, "Message status: " + response.body().getStatus());
                                }
                            }

                            @Override
                            public void onFailure(Call<Status> call, Throwable t) {
                                counter++;
                                Log.d(TAG, "Error get status sent " + t.getMessage());
                                logService.appendLog(t.getMessage());
                            }
                        });
                    }
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Log.d(TAG, "No service ");
                    logService.appendLog("RESULT_ERROR_NO_SERVICE");
                    if (messages.peek().getStatus() != null) {
                        Message message = messages.poll();
                        service.sendStatus(SET_MESSAGES_STATUS,
                                simSettings.get("deviceId"),
                                simSettings.get("simId"),
                                simSettings.get("secretKey"),
                                message.getMessageID(),
                                STATUS_UNSENT).enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(Call<Status> call, Response<Status> response) {
                                if (response.body() != null) {
                                    Log.d(TAG, "Message status: " + response.body().getStatus());
                                    counter++;
                                }
                            }

                            @Override
                            public void onFailure(Call<Status> call, Throwable t) {
                                counter++;
                                Log.d(TAG, "Error get status sent " + t.getMessage());
                                logService.appendLog(t.getMessage());
                            }
                        });
                    }
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Log.d(TAG, "Null PDU ");
                    logService.appendLog("RESULT_ERROR_NULL_PDU");
                    if (messages.peek().getStatus() != null) {
                        Message message = messages.poll();
                        service.sendStatus(SET_MESSAGES_STATUS,
                                simSettings.get("deviceId"),
                                simSettings.get("simId"),
                                simSettings.get("secretKey"),
                                message.getMessageID(),
                                STATUS_UNSENT).enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(Call<Status> call, Response<Status> response) {
                                if (response.body() != null) {
                                    Log.d(TAG, "Message status: " + response.body().getStatus());
                                    counter++;
                                }
                            }

                            @Override
                            public void onFailure(Call<Status> call, Throwable t) {
                                counter++;
                                Log.d(TAG, "Error get status sent " + t.getMessage());
                                logService.appendLog(t.getMessage());
                            }

                        });
                    }
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Log.d(TAG, "Radio off ");
                    logService.appendLog("RESULT_ERROR_RADIO_OFF");
                    if (messages.peek().getStatus() != null) {
                        Message message = messages.poll();
                        service.sendStatus(SET_MESSAGES_STATUS,
                                simSettings.get("deviceId"),
                                simSettings.get("simId"),
                                simSettings.get("secretKey"),
                                message.getMessageID(),
                                STATUS_UNSENT).enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(Call<Status> call, Response<Status> response) {
                                if (response.body() != null) {
                                    Log.d(TAG, "Message status: " + response.body().getStatus());
                                    counter++;
                                }
                            }

                            @Override
                            public void onFailure(Call<Status> call, Throwable t) {
                                counter++;
                                Log.d(TAG, "Error get status sent " + t.getMessage());
                                logService.appendLog(t.getMessage());
                            }
                        });
                    }
                    break;
            }
    }
}