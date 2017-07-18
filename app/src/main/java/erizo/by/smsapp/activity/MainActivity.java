package erizo.by.smsapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import erizo.by.smsapp.R;
import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.model.MessageWrapper;
import erizo.by.smsapp.model.Status;
import erizo.by.smsapp.service.APIService;
import erizo.by.smsapp.service.FileLogService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static erizo.by.smsapp.activity.SettingsFirstSim.settingsFirstSims;

public class MainActivity extends AppCompatActivity {

    String SENT_SMS = "SENT_SMS";
    String DELIVER_SMS = "DELIVER_SMS";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BASE_HOST = "https://con24.ru/testapi/";
    private static final String GET_ALL_MESSAGES_TASK = "getAllMessages";
    private static final String SET_MESSAGES_STATUS = "setMessageStatus";
    private static final String DEVICE_ID = "1";
    private static final String SIM_ID = "1";
    private static final String SECRET_KEY = "T687G798UHO7867H";
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_SENT = "sent";
    private static final String STATUS_UNSENT = "unsent";
    private static final String STATUS_INDELIVERED = "undelivered";
    private static final String STATUS_DELIVERED = "delivered";
    private int sentStatus;
    private int deliverStatus;
    private int counter = 0;
    private int i = 0;
    private Button startButton, stopButton, settings;
    private Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_HOST)
            .build();
    private APIService service = retrofit.create(APIService.class);
    private List<Message> mes = new LinkedList<>();

    Intent sentIntent = new Intent(SENT_SMS);
    Intent deliverIntent = new Intent(DELIVER_SMS);
    PendingIntent sentPi, deliverPi ;

    BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){
                case Activity.RESULT_OK:
                    sentStatus = 1;
                    Log.d(TAG, "SMS sent ");
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Log.d(TAG, "Generic failure ");
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Log.d(TAG, "No service ");
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Log.d(TAG, "Null PDU ");
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Log.d(TAG, "Radio off ");
                    break;
            }
        }
    };

    BroadcastReceiver deliverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){
                case Activity.RESULT_OK:
                    Log.d(TAG, "SMS delivered ");
                    deliverStatus = 1;
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d(TAG, "SMS not delivered ");
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
        settings = (Button) findViewById(R.id.settings_button);
        final Timer[] timer = {new Timer()};
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer[0].cancel();
                Log.d(TAG, "App stopped ");
                Toast.makeText(getApplicationContext(), "App stopped ", Toast.LENGTH_SHORT).show();

            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (settingsFirstSims.get("status") == null){
                    Toast.makeText(getApplicationContext(), "Активируйте SIM-карту ", Toast.LENGTH_SHORT).show();
                }else {
                    Log.d(TAG, "App started ");
                    if (settingsFirstSims.get("status").equals("true")){
                        Toast.makeText(getApplicationContext(), "App started ", Toast.LENGTH_SHORT).show();
                        try{
                            timer[0].schedule(new TimerTask() {
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
                                                        counter = 0;
                                                    }else {
                                                        Log.d(TAG, "No new messages");
                                                        new FileLogService().appendLog("Hello world");
                                                    }
                                                    Log.d(TAG, String.valueOf(counter));
                                                }else {
                                                    Log.d(TAG, "Response body = NULL");
                                                    Log.d(TAG, String.valueOf(counter));
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
                                            counter++;
                                            Log.e(TAG, "Something went wrong " + t.getMessage());
                                        }
                                    });
                                }
                            },0L, Long.parseLong(settingsFirstSims.get("frequencyOfRequests"), 10) * 1000 );
                        } catch (Exception e){
                            timer[0] = new Timer();
                            timer[0].schedule(new TimerTask() {
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
                                                        counter = 0;
                                                    }else {
                                                        Log.d(TAG, "No new messages");
                                                        new FileLogService().appendLog("Hello world");
                                                    }
                                                    Log.d(TAG, String.valueOf(counter));
                                                }else {
                                                    Log.d(TAG, "Response body = NULL");
                                                    Log.d(TAG, String.valueOf(counter));
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
                                            counter++;
                                            Log.e(TAG, "Something went wrong " + t.getMessage());
                                        }
                                    });
                                }
                            },0L, Long.parseLong(settingsFirstSims.get("frequencyOfRequests"), 10) * 1000 );
                        }
                        final SmsManager smsManager = SmsManager.getDefault();
                        Timer timerTwo = new Timer();
                        timerTwo.schedule(new TimerTask() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                            @Override
                            public void run() {
                                if (!mes.isEmpty()){
                                    if(i <= mes.size()-1){
                                        service.sendStatus(SET_MESSAGES_STATUS, DEVICE_ID, SIM_ID, SECRET_KEY, mes.get(i).getMessageID(), STATUS_PENDING).enqueue(new Callback<Status>() {
                                            @Override
                                            public void onResponse(Call<Status> call, Response<Status> response) {
                                                if (response.body() != null) {
                                                    Log.d(TAG, "Message status: " + response.body().getStatus());
                                                }
                                                counter = 0;
                                            }
                                            @Override
                                            public void onFailure(Call<Status> call, Throwable t) {
                                                counter++;
                                                Log.d(TAG, "Error get status pending " + t.getMessage());
                                            }
                                        });
                                        smsManager.sendTextMessage(mes.get(i).getPhone(), null,  mes.get(i).getMessage(), sentPi, deliverPi);
                                        i++;
                                    }else {
                                        mes.clear();
                                        i = 0;
                                    }
                                }
                                if(deliverStatus == 1){
                                    service.sendStatus(SET_MESSAGES_STATUS, DEVICE_ID, SIM_ID, SECRET_KEY, mes.get(i).getMessageID(), STATUS_DELIVERED).enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.body() != null) {
                                                Log.d(TAG, "Message status: " + response.body().getStatus());
                                            }
                                            counter = 0;
                                        }
                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                            counter++;
                                            Log.d(TAG, "Error get status pending " + t.getMessage());
                                        }
                                    });
                                }
                            }
                        },  0L, Long.parseLong(settingsFirstSims.get("frequencyOfSmsSending"), 10) * 1000);
                    }else {
                        Toast.makeText(getApplicationContext(), "Активируйте SIM-карту ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
