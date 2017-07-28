package erizo.by.smsapp.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
import erizo.by.smsapp.GetSmsFromServerTimerTask;
import erizo.by.smsapp.IncomeSmsSendTimerTask;
import erizo.by.smsapp.R;
import erizo.by.smsapp.SendSmsFromPhoneTimerTask;
import erizo.by.smsapp.SentReceiver;
import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.service.FileLogService;

import static erizo.by.smsapp.App.firstSimSettings;
import static erizo.by.smsapp.App.secondSimSettings;

public class MainActivity extends AppCompatActivity {

    private Integer counter;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    public static FileLogService logService = new FileLogService();
    private String SENT_SMS = "SENT_SMS";
    private String DELIVER_SMS = "DELIVER_SMS";
    private Intent sentIntent = new Intent(SENT_SMS);
    private Intent deliverIntent = new Intent(DELIVER_SMS);
    private PendingIntent sentPi, deliverPi;
    private Button startButton, stopButton, settingsButton;
    private Queue<Message> firstSimMessageList = new ConcurrentLinkedQueue<>();
    private Queue<Message> secondSimMessageList = new ConcurrentLinkedQueue<>();
    private BroadcastReceiver firstSimSentReceiver = new SentReceiver(firstSimMessageList, firstSimSettings, counter);
    private BroadcastReceiver firstSimDeliverReceiver = new DeliverReceiver(firstSimMessageList, firstSimSettings, counter);
    private BroadcastReceiver secondSimSentReceiver = new SentReceiver(secondSimMessageList, secondSimSettings, counter);
    private BroadcastReceiver secondSimDeliverReceiver = new DeliverReceiver(secondSimMessageList, secondSimSettings, counter);

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(firstSimSentReceiver, new IntentFilter(SENT_SMS));
        registerReceiver(firstSimDeliverReceiver, new IntentFilter(DELIVER_SMS));
        registerReceiver(secondSimSentReceiver, new IntentFilter(SENT_SMS));
        registerReceiver(secondSimDeliverReceiver, new IntentFilter(DELIVER_SMS));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(firstSimSentReceiver);
        unregisterReceiver(firstSimDeliverReceiver);
        unregisterReceiver(secondSimSentReceiver);
        unregisterReceiver(secondSimDeliverReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
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
                if (firstSimSettings.get("status").equals("false") && secondSimSettings.get("status").equals("false")) {
                    Toast.makeText(getApplicationContext(), "Активируйте SIM ", Toast.LENGTH_SHORT).show();
                } else {
                    stopButton.setEnabled(true);
                    stopButton.setBackgroundColor(Color.parseColor("#ff33b5e5"));
                    settingsButton.setClickable(false);
                    settingsButton.setBackgroundColor(Color.GRAY);
                    startButton.setClickable(false);
                    startButton.setBackgroundColor(Color.GRAY);
                    Log.d(TAG, "App started ");
                    logService.appendLog("App started  :" + TAG);
                    if (firstSimSettings.get("status").equals("true")) {
                        Timer getSmsFromServer_firstSim = new Timer();
                        Timer sendSmsFromPhone_firstSim = new Timer();
                        Timer sendFirstSimInboxSms = new Timer();
                        timers.add(sendFirstSimInboxSms);
                        timers.add(getSmsFromServer_firstSim);
                        timers.add(sendSmsFromPhone_firstSim);

                        sendFirstSimInboxSms.schedule(new IncomeSmsSendTimerTask(MainActivity.this, firstSimSettings), 0L, 30L * 1000);
                        Toast.makeText(getApplicationContext(), "App started ", Toast.LENGTH_SHORT).show();
                        getSmsFromServer_firstSim.schedule(
                                new GetSmsFromServerTimerTask(
                                        firstSimSettings,
                                        firstSimMessageList,
                                        counter),
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
                                        counter),
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

                        sendSecondSimInboxSms.schedule(new IncomeSmsSendTimerTask(MainActivity.this, secondSimSettings), 0L, 30L * 1000);
                        getSmsFromServer_secondSim.schedule(
                                new GetSmsFromServerTimerTask(
                                        secondSimSettings,
                                        secondSimMessageList,
                                        counter),
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
                                        counter),
                                0L,
                                Long.parseLong(secondSimSettings.get("frequencyOfSmsSending"),
                                        10) * 1000);
                    }
                }
            }
        });
    }
}
