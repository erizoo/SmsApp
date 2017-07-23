package erizo.by.smsapp.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

import erizo.by.smsapp.DeliverReceiver;
import erizo.by.smsapp.GetSmsFromServerTimerTask;
import erizo.by.smsapp.R;
import erizo.by.smsapp.SendSmsFromPhoneTimerTask;
import erizo.by.smsapp.SentReceiver;
import erizo.by.smsapp.model.Message;

import static erizo.by.smsapp.App.firstSimSettings;
import static erizo.by.smsapp.App.secondSimSettings;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;

    private String SENT_SMS = "SENT_SMS";
    private String DELIVER_SMS = "DELIVER_SMS";
    private Intent sentIntent = new Intent(SENT_SMS);
    private Intent deliverIntent = new Intent(DELIVER_SMS);
    private PendingIntent sentPi, deliverPi;
    private Button startButton, stopButton, settingsButton;
    private Queue<Message> firstSimMessageList = new ConcurrentLinkedQueue<>();
    private Queue<Message> secondSimMessageList = new ConcurrentLinkedQueue<>();
    private BroadcastReceiver firstSimSentReceiver = new SentReceiver(firstSimMessageList, firstSimSettings);
    private BroadcastReceiver firstSimDeliverReceiver = new DeliverReceiver(firstSimMessageList, firstSimSettings);
    private BroadcastReceiver secondSimSentReceiver = new SentReceiver(secondSimMessageList, secondSimSettings);
    private BroadcastReceiver secondSimDeliverReceiver = new DeliverReceiver(secondSimMessageList, secondSimSettings);

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
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Timer timer : timers) {
                    timer.cancel();
                }
                timers.clear();
                Log.d(TAG, "App stopped ");
                Toast.makeText(getApplicationContext(), "App stopped ", Toast.LENGTH_SHORT).show();
                settingsButton.setClickable(true);
                startButton.setClickable(true);
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer getSmsFromServer_firstSim = new Timer();
                Timer sendSmsFromPhone_firstSim = new Timer();
                Timer getSmsFromServer_secondSim = new Timer();
                Timer sendSmsFromPhone_secondSim = new Timer();
                timers.add(getSmsFromServer_firstSim);
                timers.add(sendSmsFromPhone_firstSim);
                timers.add(getSmsFromServer_secondSim);
                timers.add(sendSmsFromPhone_secondSim);
                if (firstSimSettings.get("status").equals("false")) {
                    Toast.makeText(getApplicationContext(), "Активируйте SIM ", Toast.LENGTH_SHORT).show();
                } else {
                    settingsButton.setClickable(false);
                    startButton.setClickable(false);
                    Log.d(TAG, "App started ");
                    if (firstSimSettings.get("status").equals("true")) {
                        Toast.makeText(getApplicationContext(), "App started ", Toast.LENGTH_SHORT).show();
                        getSmsFromServer_firstSim.schedule(
                                new GetSmsFromServerTimerTask(
                                        firstSimSettings,
                                        firstSimMessageList),
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
                                        deliverIntent),
                                0L,
                                Long.parseLong(firstSimSettings.get("frequencyOfSmsSending"),
                                        10) * 1000);
                    }
                    if (secondSimSettings.get("status").equals("true")) {
                        getSmsFromServer_secondSim.schedule(
                                new GetSmsFromServerTimerTask(
                                        secondSimSettings,
                                        secondSimMessageList),
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
                                        deliverIntent),
                                0L,
                                Long.parseLong(secondSimSettings.get("frequencyOfSmsSending"),
                                        10) * 1000);
                    }
                }

            }
        });
    }

    public String getMessageIdForSms(String phone, String message) {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        String[] item = formattedDate.split(" ");
        String[] itemOne = item[0].split("-");
        String[] itemTwo = item[1].split(":");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemOne[0]).append(itemOne[1]).append(itemOne[2]).append(itemTwo[0]).append(itemTwo[1]).append(itemTwo[2])
                .append("00001").append("00001").append(MD5_Hash(phone + message));
        Log.d(TAG, String.valueOf(stringBuilder).toUpperCase());
        return String.valueOf(stringBuilder).toUpperCase();
    }

    public static String MD5_Hash(String s) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(s.getBytes(), 0, s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }
}
