package erizo.by.smsapp.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import erizo.by.smsapp.DeliverReceiver;
import erizo.by.smsapp.GetSmsFromServerTimerTask;
import erizo.by.smsapp.R;
import erizo.by.smsapp.SendSmsFromPhoneTimerTask;
import erizo.by.smsapp.SentReceiver;
import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.model.Status;
import erizo.by.smsapp.service.APIService;
import erizo.by.smsapp.service.FileLogService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static erizo.by.smsapp.App.firstSimSettings;
import static erizo.by.smsapp.App.secondSimSettings;
import static erizo.by.smsapp.SmsStatus.NEW_INCOME_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SENT_SMS = "SENT_SMS";
    private static final String DELIVER_SMS = "DELIVER_SMS";

    private Integer systemErrorCounter;
    public static FileLogService logService = new FileLogService();
    private Intent sentIntent = new Intent(SENT_SMS);
    private Intent deliverIntent = new Intent(DELIVER_SMS);
    private PendingIntent sentPi, deliverPi;
    private Button startButton, stopButton, settingsButton;
    private Queue<Message> firstSimMessageList = new ConcurrentLinkedQueue<>();
    private Queue<Message> secondSimMessageList = new ConcurrentLinkedQueue<>();
    private Queue<Message> incomeMessages = new ConcurrentLinkedQueue<>();
    private BroadcastReceiver firstSimSentReceiver = new SentReceiver(firstSimMessageList, firstSimSettings, systemErrorCounter);
    private BroadcastReceiver firstSimDeliverReceiver = new DeliverReceiver(firstSimMessageList, firstSimSettings, systemErrorCounter);
    private BroadcastReceiver secondSimSentReceiver = new SentReceiver(secondSimMessageList, secondSimSettings, systemErrorCounter);
    private BroadcastReceiver secondSimDeliverReceiver = new DeliverReceiver(secondSimMessageList, secondSimSettings, systemErrorCounter);
    private SmsListener smsListener = new SmsListener();

    private class SmsListener extends BroadcastReceiver {

        private final String TAG = SmsListener.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "got sms");
            logService.appendLog(TAG + " " + "got sms");
            if(intent.getAction().equals(SMS_RECEIVED)){
                Bundle bundle = intent.getExtras();
                SmsMessage[] msgs;
                if (bundle != null) {
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for(int i=0; i<msgs.length; i++){
                            msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                            String msgAddress = msgs[i].getOriginatingAddress();
                            String msgBody = msgs[i].getMessageBody();
                            String[] smsData = new String[]{msgAddress, msgBody};
                            new IncomeSmsChecker().execute(smsData);
                        }
                    } catch(Exception e){
//                            Log.d("Exception caught",e.getMessage());
                    }

                }
            }
        }
    }

    private  class IncomeSmsChecker extends AsyncTask<String, Void, Void> {

        private final String TAG = IncomeSmsChecker.class.getSimpleName();

        @Override
        protected Void doInBackground(String... messageData) {
            Log.d(TAG, "start checking income sms");
            logService.appendLog(TAG + " " + "start checking income sms");
            Uri uriSMSURI = Uri.parse("content://sms/inbox");
            Cursor cur = getContext().getContentResolver().query(uriSMSURI, null, null, null, null);
            if (cur != null) {
                while (cur.moveToNext()) {
                    String address = cur.getString(cur.getColumnIndex("address"));
                    String body = cur.getString(cur.getColumnIndexOrThrow("body"));
                    String simId = cur.getString(cur.getColumnIndexOrThrow("sub_id"));
                    Log.d(TAG, "Body from cursor => " + cur.getString(cur.getColumnIndex("body")));
                    Log.d(TAG, "SimId from cursor => " + cur.getString(cur.getColumnIndex("sub_id")));
                    Log.d(TAG, "found sms : " + address + " " + body + " " + simId);
                    logService.appendLog(TAG + " : " + address + " " + body + " " + simId);
                    Log.d(TAG, "Address from cursor => " + cur.getString(cur.getColumnIndex("address")));
                    if (isMessagesMatch(address, body, messageData[0], messageData[1])) {
                        incomeMessages.add(new Message(address, body, simId));
                        break;
                        // TODO: 23.8.17 add income sms removing
                    }
                }
                cur.close();
            }
            return null;
        }

        private boolean isMessagesMatch(String currentMessageAddress, String currentMessageBody, String incomeMessageAddress, String incomeMessageBody) {
            return (currentMessageAddress.equals(incomeMessageAddress) && currentMessageBody.equals(incomeMessageBody));
        }
    }

    private Context getContext() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(firstSimSentReceiver, new IntentFilter(SENT_SMS));
        registerReceiver(firstSimDeliverReceiver, new IntentFilter(DELIVER_SMS));
        registerReceiver(secondSimSentReceiver, new IntentFilter(SENT_SMS));
        registerReceiver(secondSimDeliverReceiver, new IntentFilter(DELIVER_SMS));
        registerReceiver(smsListener, new IntentFilter(SMS_RECEIVED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(firstSimSentReceiver);
        unregisterReceiver(firstSimDeliverReceiver);
        unregisterReceiver(secondSimSentReceiver);
        unregisterReceiver(secondSimDeliverReceiver);
        unregisterReceiver(smsListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String permission = Manifest.permission.READ_PHONE_STATE;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.RECEIVE_SMS},
//                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        final List<Timer> timers = new ArrayList<>();
        sentPi = PendingIntent.getBroadcast(this, 0, sentIntent, 0);
        deliverPi = PendingIntent.getBroadcast(this, 0, deliverIntent, 0);
        startButton = (Button) findViewById(R.id.start_button);
        settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setClickable(true);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingsButton.isEnabled()) {
                    Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "You must app stop ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setEnabled(false);
        stopButton.setBackgroundColor(Color.GRAY);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Timer timer : timers) {
                    timer.cancel();
                }
                timers.clear();
                logService.appendLog("App stopped  :" + TAG);
                Log.d(TAG, "App stopped ");
                Toast.makeText(getApplicationContext(), "App stopped ", Toast.LENGTH_SHORT).show();
                settingsButton.setClickable(true);
                settingsButton.setBackgroundColor(Color.parseColor("#ff33b5e5"));
                startButton.setClickable(true);
                startButton.setBackgroundColor(Color.parseColor("#ff33b5e5"));
                stopButton.setEnabled(false);
                stopButton.setBackgroundColor(Color.GRAY);
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (firstSimSettings.get("status").equals("false") && secondSimSettings.get("status").equals("false")) {
                        Toast.makeText(getApplicationContext(), "Активируйте SIM ", Toast.LENGTH_SHORT).show();
                    } else {
                        systemErrorCounter = 0;
                        stopButton.setEnabled(true);
                        stopButton.setBackgroundColor(Color.parseColor("#ff33b5e5"));
                        settingsButton.setClickable(false);
                        settingsButton.setBackgroundColor(Color.GRAY);
                        startButton.setClickable(false);
                        startButton.setBackgroundColor(Color.GRAY);
                        Log.d(TAG, "App started ");
                        logService.appendLog("App started  :" + TAG);
                        Timer sendIncomeSms = new Timer();
                        timers.add(sendIncomeSms);
                        sendIncomeSms.schedule(new TimerTask() {

                            private Retrofit retrofit = new Retrofit.Builder()
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .baseUrl(firstSimSettings.get("url"))
                                    .build();
                            private APIService service = retrofit.create(APIService.class);

                            @Override
                            public void run() {
                                while (!incomeMessages.isEmpty()) {
                                    Message message = incomeMessages.poll();
                                    service.sendSms(
                                            NEW_INCOME_MESSAGE,
                                            firstSimSettings.get("deviceId"),
                                            message.getInternalSimIds(),
                                            firstSimSettings.get("secretKey"),
                                            message.getPhone(),
                                            message.getMessage(),
                                            "test message id").enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.body() != null) {
                                                logService.appendLog("Message status: " + response.body().getStatus() + TAG);
                                                Log.d(TAG, "Message status: " + response.body().getStatus());
                                                systemErrorCounter = 0;
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                            systemErrorCounter++;
                                            Log.e(TAG, t.getMessage());
                                            logService.appendLog(t.getMessage());
                                            Log.e(TAG, "Error get status pending " + t.getMessage());
                                        }
                                    });
                                }
                            }
                        }, 0L, 500L);
                        if (firstSimSettings.get("status").equals("true")) {
                            Timer getSmsFromServer_firstSim = new Timer();
                            Timer sendSmsFromPhone_firstSim = new Timer();
                            Timer sendFirstSimInboxSms = new Timer();
                            timers.add(sendFirstSimInboxSms);
                            timers.add(getSmsFromServer_firstSim);
                            timers.add(sendSmsFromPhone_firstSim);

//                            sendFirstSimInboxSms.schedule(new IncomeSmsSendTimerTask(MainActivity.this, firstSimSettings, secondSimSettings, systemErrorCounter), 0L, 30L * 1000);
                            Toast.makeText(getApplicationContext(), "App started ", Toast.LENGTH_SHORT).show();
                            getSmsFromServer_firstSim.schedule(
                                    new GetSmsFromServerTimerTask(
                                            firstSimSettings,
                                            firstSimMessageList,
                                            systemErrorCounter),
                                    0L,
                                    Long.parseLong(firstSimSettings.get("frequencyOfRequests"),
                                            10) * 1000);
                            sendSmsFromPhone_firstSim.schedule(
                                    new SendSmsFromPhoneTimerTask(
                                            firstSimMessageList,
                                            Integer.valueOf(firstSimSettings.get("simSlot")),
                                            sentPi,
                                            deliverPi,
                                            getBaseContext(),
                                            sentIntent,
                                            deliverIntent,
                                            systemErrorCounter),
                                    0L,
                                    Long.parseLong(firstSimSettings.get("frequencyOfSmsSending"),
                                            10) * 1000);
                        }
                        if (secondSimSettings.get("status").equals("true")) {
                            Timer getSmsFromServer_secondSim = new Timer();
                            Timer sendSmsFromPhone_secondSim = new Timer();
                            Timer sendSecondSimInboxSms = new Timer();
                            timers.add(sendSecondSimInboxSms);
                            timers.add(getSmsFromServer_secondSim);
                            timers.add(sendSmsFromPhone_secondSim);
                            getSmsFromServer_secondSim.schedule(
                                    new GetSmsFromServerTimerTask(
                                            secondSimSettings,
                                            secondSimMessageList,
                                            systemErrorCounter),
                                    0L,
                                    Long.parseLong(secondSimSettings.get("frequencyOfRequests"),
                                            10) * 1000);
                            sendSmsFromPhone_secondSim.schedule(
                                    new SendSmsFromPhoneTimerTask(
                                            secondSimMessageList,
                                            Integer.valueOf(secondSimSettings.get("simSlot")),
                                            sentPi,
                                            deliverPi,
                                            getBaseContext(),
                                            sentIntent,
                                            deliverIntent,
                                            systemErrorCounter),
                                    0L,
                                    Long.parseLong(secondSimSettings.get("frequencyOfSmsSending"),
                                            10) * 1000);
                        }
                    }
                } catch (Exception e) {
                    Log.e("START_BUTTON_CRASH", e.getMessage());
                    logService.appendLog("START_BUTTON_CRASH : " + e.getMessage());
                }
            }
        });
    }
}
