package erizo.by.smsapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import erizo.by.smsapp.R;
import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.model.MessageWrapper;
import erizo.by.smsapp.service.APIService;
import erizo.by.smsapp.service.FileLogService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private String SENT_SMS = "SENT_SMS";
    private String DELIVER_SMS = "DELIVER_SMS";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BASE_HOST = "https://con24.ru/testapi/";
    private static final String GET_ALL_MESSAGES_TASK = "getAllMessages";
    private static final String DEVICE_ID = "1";
    private static final String SIM_ID = "1";
    private static final String SECRET_KEY = "T687G798UHO7867H";
    private int counter = 0;
    private int i = 0;
    private long periodicity = 10L * 1000;

    private Button startButton;
    private AlarmManager am;
    private Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_HOST)
            .build();
    private APIService service = retrofit.create(APIService.class);
    private List<Message> mes = new LinkedList<>();

    Intent sentIntent = new Intent();
    Intent deliverIntent = new Intent();

    PendingIntent sentPi, deliverPi ;



    BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){
                case Activity.RESULT_OK:
                    Toast.makeText(context, "Sented", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(context, "Error sent", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    BroadcastReceiver deliverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){
                case Activity.RESULT_OK:
                    Toast.makeText(context, "Delivered", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(context, "Error deliver", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(sentReceiver, new IntentFilter(SENT_SMS));
        registerReceiver(deliverReceiver, new IntentFilter(DELIVER_SMS));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(sentReceiver);
        unregisterReceiver(deliverReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
        sentPi = PendingIntent.getBroadcast(this, 0, sentIntent, 0);
        deliverPi = PendingIntent.getBroadcast(this, 0, deliverIntent, 0);
        startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
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
                                            final SmsManager smsManager = SmsManager.getDefault();
                                            Timer timer = new Timer();
                                            timer.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    if (!mes.isEmpty()){
                                                        smsManager.sendTextMessage(mes.get(i).getPhone(), null,  mes.get(i).getMessageID(), sentPi, deliverPi);
                                                        i++;
                                                    }else {
                                                        i = 0;
                                                    }
                                                }
                                            },  0L, periodicity);
                                            i = 0;
                                            counter = 0;
                                        }else {
                                            Log.e(TAG, "No new messages");
                                            new FileLogService().appendLog("Hello world");
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
                },0L, 20L * 1000 );


            }
        });
    }
}
