package erizo.by.smsapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import erizo.by.smsapp.R;
import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.model.MessageWrapper;
import erizo.by.smsapp.model.User;
import erizo.by.smsapp.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BASE_HOST = "https://con24.ru/testapi/";
    private static final String GET_ALL_MESSAGES_TASK = "getAllMessages";
    private static final String DEVICE_ID = "1";
    private static final String SIM_ID = "1";
    private static final String SECRET_KEY = "T687G798UHO7867H";
    private int counter = 0;

    private Button startButton;
    private Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_HOST)
            .build();
    private APIService service = retrofit.create(APIService.class);
    private List<Message> mes = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                service.getMessages(GET_ALL_MESSAGES_TASK, DEVICE_ID, SIM_ID, SECRET_KEY).enqueue(new Callback<MessageWrapper>() {
                    @Override
                    public void onResponse(Call<MessageWrapper> call, Response<MessageWrapper> response) {
                        try {
                            if (response.body() != null) {
                                Log.d(TAG, response.body().toString());
                                if (!response.body().getMessages().isEmpty()) {
                                    for (Message list : response.body().getMessages()) {
                                        mes.add(list);
                                    }
                                    counter = 0;
                                }else {
                                    Log.e(TAG, "No new messages");
                                }
                                Log.e(TAG, String.valueOf(counter));
                            }else {
                                Log.e(TAG, "Response body = NULL");
                                Log.e(TAG, String.valueOf(counter));
                                counter = 0;
                            }
                        }catch (Exception e){
                            counter++;
                            Log.e(TAG, e.getMessage());
                            Log.e(TAG, String.valueOf(counter));
                        }

                    }

                    @Override
                    public void onFailure(Call<MessageWrapper> call, Throwable t) {
                        Log.e(TAG, "Something went wrong " + t.getMessage());
                        final TextView textView = (TextView) findViewById(R.id.textView);
                        textView.setText(t.getMessage());
                    }
                });
            }
        });
    }
}
