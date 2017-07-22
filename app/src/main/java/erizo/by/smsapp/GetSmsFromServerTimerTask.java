package erizo.by.smsapp;

import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    private Map<String,String> simSettings;
    private Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_HOST)
            .build();
    private APIService service = retrofit.create(APIService.class);
    private Queue<Message> serverMessageList;

    private static final String BASE_HOST = "https://con24.ru/testapi/";
    private static final String GET_ALL_MESSAGES_TASK = "getAllMessages";
    private static final String DEVICE_ID = "1";
    private static final String SECRET_KEY = "T687G798UHO7867H";

    private static final String TAG = "TASK"; // TODO: 22.7.17 change to right

    public GetSmsFromServerTimerTask(Map<String, String> simSettings, Queue<Message> serverMessageList) {
        super();
        this.simSettings = simSettings;
        this.serverMessageList = serverMessageList;
    }

    public void run() {
        service.getMessages(GET_ALL_MESSAGES_TASK, DEVICE_ID, simSettings.get("simId"),
                SECRET_KEY).enqueue(new Callback<MessageWrapper>() {
            @Override
            public void onResponse(Call<MessageWrapper> call, Response<MessageWrapper> response) {
                try {
                    if (response.body() != null) {
                        Log.d(TAG, response.body().toString());
                        if (!response.body().getMessages().isEmpty()) {
                            for (Message list : response.body().getMessages()) {
                                serverMessageList.add(list);
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
