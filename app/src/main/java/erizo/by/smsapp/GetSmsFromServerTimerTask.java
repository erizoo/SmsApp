package erizo.by.smsapp;

import android.util.Log;

import java.util.Map;
import java.util.Queue;
import java.util.TimerTask;

import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.model.MessageWrapper;
import erizo.by.smsapp.service.APIService;
import erizo.by.smsapp.service.FileLogService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetSmsFromServerTimerTask extends TimerTask {

    private static final String TAG = GetSmsFromServerTimerTask.class.getSimpleName();
    private static final String GET_ALL_MESSAGES_TASK = "getAllMessages";

    private Queue<Message> messages;
    private Map<String,String> simSettings;

    private APIService service;

    public GetSmsFromServerTimerTask(Map<String, String> simSettings, Queue<Message> serverMessageList) {
        super();
        this.simSettings = simSettings;
        this.messages = serverMessageList;
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
                        if (!response.body().getMessages().isEmpty()) {
                            for (Message message : response.body().getMessages()) {
                                messages.add(message);
                            }
                        } else {
                            Log.d(TAG, "No new messages");
                            new FileLogService().appendLog("Hello world");
                        }
//                        Log.d(TAG, String.valueOf(counter));
                    } else {
                        Log.d(TAG, "Response body = NULL");
//                        Log.d(TAG, String.valueOf(counter));
//                        counter = 0;
                    }
                } catch (Exception e) {
//                    counter++;
                    Log.e(TAG, e.getMessage());
//                    Log.e(TAG, String.valueOf(counter));
                }
            }

            @Override
            public void onFailure(Call<MessageWrapper> call, Throwable t) {
//                counter++;
                Log.e(TAG, "Something went wrong " + t.getMessage());
            }
        });
    }
}
