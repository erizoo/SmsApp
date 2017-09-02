package erizo.by.smsapp.receivers;

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

import static erizo.by.smsapp.SmsStatus.SET_MESSAGES_STATUS;
import static erizo.by.smsapp.SmsStatus.SMS_PENDING;
import static erizo.by.smsapp.SmsStatus.SMS_SENT;
import static erizo.by.smsapp.SmsStatus.STATUS_SENT;
import static erizo.by.smsapp.SmsStatus.STATUS_UNSENT;
import static erizo.by.smsapp.activity.MainActivity.logService;

public class SentReceiver extends BroadcastReceiver {

    private static final String TAG = SentReceiver.class.getSimpleName();

    private Queue<Message> messages;
    private Map<String, String> simSettings;

    private APIService service;
    private Integer systemErrorCounter;
    private Integer unsentMessageCounter;


    public SentReceiver(Queue<Message> messages, Map<String, String> simSettings, Integer systemErrorCounter, Integer unsentMessageCounter) {
        this.messages = messages;
        this.simSettings = simSettings;
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(simSettings.get("url"))
                .build();
        this.service = retrofit.create(APIService.class);
        this.systemErrorCounter = systemErrorCounter;
        this.unsentMessageCounter = unsentMessageCounter;
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
                                            systemErrorCounter = 0;
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Status> call, Throwable t) {
                                        systemErrorCounter++;
                                        Log.d(TAG, "Error get status sent " + t.getMessage());
                                        logService.appendLog(TAG + " " + t.getMessage());
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
                    unsentMessageCounter++;
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
                                    systemErrorCounter++;
                                    logService.appendLog("Message status: " + response.body().getStatus() + TAG);
                                    Log.d(TAG, "Message status: " + response.body().getStatus());
                                }
                            }

                            @Override
                            public void onFailure(Call<Status> call, Throwable t) {
                                systemErrorCounter++;
                                Log.d(TAG, "Error get status sent " + t.getMessage());
                                logService.appendLog(TAG + " " + TAG + " " + t.getMessage());
                            }
                        });
                    }
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Log.d(TAG, "No service ");
                    logService.appendLog("RESULT_ERROR_NO_SERVICE");
                    unsentMessageCounter++;
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
                                    systemErrorCounter++;
                                }
                            }

                            @Override
                            public void onFailure(Call<Status> call, Throwable t) {
                                systemErrorCounter++;
                                Log.d(TAG, "Error get status sent " + t.getMessage());
                                logService.appendLog(TAG + " " + t.getMessage());
                            }
                        });
                    }
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Log.d(TAG, "Null PDU ");
                    logService.appendLog("RESULT_ERROR_NULL_PDU");
                    unsentMessageCounter++;
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
                                    systemErrorCounter++;
                                }
                            }

                            @Override
                            public void onFailure(Call<Status> call, Throwable t) {
                                systemErrorCounter++;
                                Log.d(TAG, "Error get status sent " + t.getMessage());
                                logService.appendLog(TAG + " " + t.getMessage());
                            }

                        });
                    }
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Log.d(TAG, "Radio off ");
                    logService.appendLog("RESULT_ERROR_RADIO_OFF");
                    unsentMessageCounter++;
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
                                    systemErrorCounter++;
                                }
                            }

                            @Override
                            public void onFailure(Call<Status> call, Throwable t) {
                                systemErrorCounter++;
                                Log.d(TAG, "Error get status sent " + t.getMessage());
                                logService.appendLog(TAG + " " + t.getMessage());
                            }
                        });
                    }
                    break;
            }
    }
}
