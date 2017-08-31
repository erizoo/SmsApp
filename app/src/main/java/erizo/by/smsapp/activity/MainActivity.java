package erizo.by.smsapp.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
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
import java.util.concurrent.ConcurrentLinkedQueue;

import erizo.by.smsapp.DeliverReceiver;
import erizo.by.smsapp.R;
import erizo.by.smsapp.SentReceiver;
import erizo.by.smsapp.asynctasks.IncomeSmsChecker;
import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.service.FileLogService;
import erizo.by.smsapp.timertasks.GetSmsFromServerTimerTask;
import erizo.by.smsapp.timertasks.IncomeSmsSendTimerTask;
import erizo.by.smsapp.timertasks.SendSmsFromPhoneTimerTask;

import static erizo.by.smsapp.App.firstSimSettings;
import static erizo.by.smsapp.App.secondSimSettings;
import static erizo.by.smsapp.SimSettings.SIM_IDENTIFIER;
import static erizo.by.smsapp.SimSettings.STATUS;

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
                            new IncomeSmsChecker(getContext(), incomeMessages).execute(smsData);
                        }
                    } catch(Exception e) {
                        Log.d("Exception caught", e.getMessage());
                        logService.appendLog(TAG + " : " + "failed read sms. Catch exception : " + e.getMessage());
                    }

                }
            }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_PHONE_STATE,
            }, 6);
        }
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
                if (firstSimSettings.get(STATUS).equals("false") && secondSimSettings.get(STATUS).equals("false")) {
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
                    sendIncomeSms.schedule(new IncomeSmsSendTimerTask(incomeMessages, systemErrorCounter), 0L, 1500L);
                    if (firstSimSettings.get(STATUS).equals("true") && firstSimSettings.containsKey(SIM_IDENTIFIER)) {
                        Timer getSmsFromServer_firstSim = new Timer();
                        Timer sendSmsFromPhone_firstSim = new Timer();
                        Timer sendFirstSimInboxSms = new Timer();
                        timers.add(sendFirstSimInboxSms);
                        timers.add(getSmsFromServer_firstSim);
                        timers.add(sendSmsFromPhone_firstSim);
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
                    } else {
                        Toast.makeText(getApplicationContext(), "Change first sim settings", Toast.LENGTH_SHORT).show();
                    }
                    if (secondSimSettings.get(STATUS).equals("true") && secondSimSettings.containsKey(SIM_IDENTIFIER)) {
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
                    } else {
                        Toast.makeText(getApplicationContext(), "Change second sim settings", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
