package erizo.by.smsapp;

import android.util.Log;

import java.util.Map;
import java.util.Queue;
import java.util.TimerTask;

import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.model.MessageWrapper;
import erizo.by.smsapp.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static erizo.by.smsapp.activity.MainActivity.logService;

public class GetSmsFromServerTimerTask extends TimerTask {

    private static final String TAG = GetSmsFromServerTimerTask.class.getSimpleName();
    private static final String GET_ALL_MESSAGES_TASK = "getAllMessages";

    private Queue<Message> messages;
    private Map<String,String> simSettings;

    private APIService service;
    private Integer systemErrorCounter;


    public GetSmsFromServerTimerTask(Map<String, String> simSettings, Queue<Message> serverMessageList, Integer systemErrorCounter) {
        super();
        this.simSettings = simSettings;
        this.messages = serverMessageList;
        this.systemErrorCounter = systemErrorCounter;
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(simSettings.get("url"))
                .build();
        service = retrofit.create(APIService.class);
    }

    public void run() {
        service.getMessages(
                GET_ALL_MESSAGES_TASK,
                simSettings.get("deviceId"),
                simSettings.get("simId"),
                simSettings.get("secretKey"))
                .enqueue(new Callback<MessageWrapper>() {
            @Override
            public void onResponse(Call<MessageWrapper> call, Response<MessageWrapper> response) {
                try {
                    if (response.body() != null) {
                        Log.d(TAG, response.body().toString());
                        logService.appendLog(response.body().toString() + TAG);
                        if (!response.body().getMessages().isEmpty()) {
                            for (Message message : response.body().getMessages()) {
                                messages.add(message);
                            }
                        } else {
                            Log.d(TAG, "No new messages");
                            logService.appendLog("No new messages :" + TAG);
                        }
                    } else {
                        Log.d(TAG, "Response body = NULL");
                        logService.appendLog("Empty response body in :" + TAG);
                        systemErrorCounter = 0;
                    }
                } catch (Exception e) {
                    systemErrorCounter++;
                    logService.appendLog(e.getMessage() + TAG);
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<MessageWrapper> call, Throwable t) {
                systemErrorCounter++;
                Log.e(TAG, "Something went wrong " + t.getMessage());
                logService.appendLog(t.getMessage());
            }
        });
    }
}
