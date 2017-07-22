package erizo.by.smsapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import erizo.by.smsapp.GetSmsFromServerTimerTask;
import erizo.by.smsapp.R;
import erizo.by.smsapp.SendSmsFromPhoneTimerTask;
import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.model.MessageWrapper;
import erizo.by.smsapp.model.Status;
import erizo.by.smsapp.service.APIService;
import erizo.by.smsapp.service.FileLogService;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static erizo.by.smsapp.App.settingsFirstSims;
import static erizo.by.smsapp.App.settingsSecondSims;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private static final String MES = "MES";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BASE_HOST = "https://con24.ru/testapi/";
    private static final String GET_ALL_MESSAGES_TASK = "getAllMessages";
    private static final String NEW_INCOME_MESSAGE = "newIncomeMessage";
    private static final String SET_MESSAGES_STATUS = "setMessageStatus";
    private static final String DEVICE_ID = "1";
    private static final String SIM_ID = "1";
    private static final String SECRET_KEY = "T687G798UHO7867H";
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_SENT = "sent";
    private static final String STATUS_UNSENT = "unsent";
    private static final String STATUS_INDELIVERED = "undelivered";
    private static final String STATUS_DELIVERED = "delivered";
    String SENT_SMS = "SENT_SMS";
    String DELIVER_SMS = "DELIVER_SMS";
    Intent sentIntent = new Intent(SENT_SMS);
    Intent deliverIntent = new Intent(DELIVER_SMS);
    ArrayList<PendingIntent> sentIntents = new ArrayList<>();
    ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();
    PendingIntent sentPi, deliverPi;
    List<Integer> simList;
    private int counter = 0;
    private int i = 0;
    private int k = 0;
    private int j = 0;
    private int n = 0;
    private int status;
    private Button startButton, stopButton, settings;
    private Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_HOST)
            .build();
    private APIService service = retrofit.create(APIService.class);
    private Queue<Message> messageListFirstSim = new ConcurrentLinkedQueue<>();
    private Queue<Message> messageListSecondSim = new ConcurrentLinkedQueue<>();
    private List<Message> mesStatus = new LinkedList<>();
    private List<Message> mesStatusDelivered = new LinkedList<>();
//    BroadcastReceiver sentReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (!messageListFirstSim.isEmpty()) {
//                for (Message) {
//                    switch (getResultCode()) {
//                        case Activity.RESULT_OK:
//                            service.sendStatus(SET_MESSAGES_STATUS, DEVICE_ID, settingsFirstSims.get("simId"), settingsFirstSims.get("secretKey"),
//                                    mesStatus.get(k).getMessageID(), STATUS_SENT).enqueue(new Callback<Status>() {
//                                @Override
//                                public void onResponse(Call<Status> call, Response<Status> response) {
//                                    if (response.body() != null) {
//                                        Log.d(TAG, "Message status: " + response.body().getStatus());
//                                    }
//                                    k++;
//                                    if (k == mesStatus.size()) {
//                                        k = 0;
//                                    }
//                                    counter = 0;
//                                }
//
//                                @Override
//                                public void onFailure(Call<Status> call, Throwable t) {
//                                    counter++;
//                                    Log.d(TAG, "Error get status sent " + t.getMessage());
//                                }
//                            });
//                            break;
//                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                            Log.d(TAG, "Generic failure ");
//                            service.sendStatus(SET_MESSAGES_STATUS, DEVICE_ID, settingsFirstSims.get("simId"), settingsFirstSims.get("secretKey"),
//                                    mesStatus.get(k).getMessageID(), STATUS_UNSENT).enqueue(new Callback<Status>() {
//                                @Override
//                                public void onResponse(Call<Status> call, Response<Status> response) {
//                                    if (response.body() != null) {
//                                        Log.d(TAG, "Message status: " + response.body().getStatus());
//                                    }
//                                    k++;
//                                    if (k == mesStatus.size()) {
//                                        k = 0;
//                                    }
//                                    counter = 0;
//                                }
//
//                                @Override
//                                public void onFailure(Call<Status> call, Throwable t) {
//                                    counter++;
//                                    Log.d(TAG, "Error get status sent " + t.getMessage());
//                                }
//                            });
//                            break;
//                        case SmsManager.RESULT_ERROR_NO_SERVICE:
//                            Log.d(TAG, "No service ");
//                            service.sendStatus(SET_MESSAGES_STATUS, DEVICE_ID, settingsFirstSims.get("simId"), settingsFirstSims.get("secretKey"),
//                                    mesStatus.get(k).getMessageID(), STATUS_UNSENT).enqueue(new Callback<Status>() {
//                                @Override
//                                public void onResponse(Call<Status> call, Response<Status> response) {
//                                    if (response.body() != null) {
//                                        Log.d(TAG, "Message status: " + response.body().getStatus());
//                                    }
//                                    k++;
//                                    if (k == mesStatus.size()) {
//                                        k = 0;
//                                    }
//                                    counter = 0;
//                                }
//
//                                @Override
//                                public void onFailure(Call<Status> call, Throwable t) {
//                                    counter++;
//                                    Log.d(TAG, "Error get status sent " + t.getMessage());
//                                }
//                            });
//                            break;
//                        case SmsManager.RESULT_ERROR_NULL_PDU:
//                            Log.d(TAG, "Null PDU ");
//                            service.sendStatus(SET_MESSAGES_STATUS, DEVICE_ID, settingsFirstSims.get("simId"), settingsFirstSims.get("secretKey"),
//                                    mesStatus.get(k).getMessageID(), STATUS_UNSENT).enqueue(new Callback<Status>() {
//                                @Override
//                                public void onResponse(Call<Status> call, Response<Status> response) {
//                                    if (response.body() != null) {
//                                        Log.d(TAG, "Message status: " + response.body().getStatus());
//                                    }
//                                    k++;
//                                    if (k == mesStatus.size()) {
//                                        k = 0;
//                                    }
//                                    counter = 0;
//                                }
//
//                                @Override
//                                public void onFailure(Call<Status> call, Throwable t) {
//                                    counter++;
//                                    Log.d(TAG, "Error get status sent " + t.getMessage());
//                                }
//                            });
//                            break;
//                        case SmsManager.RESULT_ERROR_RADIO_OFF:
//                            Log.d(TAG, "Radio off ");
//                            service.sendStatus(SET_MESSAGES_STATUS, DEVICE_ID, settingsFirstSims.get("simId"), settingsFirstSims.get("secretKey"),
//                                    mesStatus.get(k).getMessageID(), STATUS_UNSENT).enqueue(new Callback<Status>() {
//                                @Override
//                                public void onResponse(Call<Status> call, Response<Status> response) {
//                                    if (response.body() != null) {
//                                        Log.d(TAG, "Message status: " + response.body().getStatus());
//                                    }
//                                    k++;
//                                    if (k == mesStatus.size()) {
//                                        k = 0;
//                                    }
//                                    counter = 0;
//                                }
//
//                                @Override
//                                public void onFailure(Call<Status> call, Throwable t) {
//                                    counter++;
//                                    Log.d(TAG, "Error get status sent " + t.getMessage());
//                                }
//                            });
//                            break;
//                    }
//                    mesStatusDelivered = mesStatus;
//                } else {
//                    mesStatus.clear();
//                    k = 0;
//                }
//            }
//
//        }
//    };
//    BroadcastReceiver deliverReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (!mesStatusDelivered.isEmpty()) {
//                if (j <= mesStatusDelivered.size() - 1) {
//                    switch (getResultCode()) {
//                        case Activity.RESULT_OK:
//                            service.sendStatus(SET_MESSAGES_STATUS, DEVICE_ID, settingsFirstSims.get("simId"), settingsFirstSims.get("secretKey"),
//                                    mesStatusDelivered.get(j).getMessageID(), STATUS_DELIVERED).enqueue(new Callback<Status>() {
//                                @Override
//                                public void onResponse(Call<Status> call, Response<Status> response) {
//                                    if (response.body() != null) {
//                                        Log.d(TAG, "Message status: " + response.body().getStatus());
//                                    }
//                                    j++;
//                                    if (j == mesStatusDelivered.size()) {
//                                        j = 0;
//                                    }
//                                    counter = 0;
//                                }
//
//                                @Override
//                                public void onFailure(Call<Status> call, Throwable t) {
//                                    counter++;
//                                    Log.d(TAG, "Error get status sent " + t.getMessage());
//                                }
//                            });
//                            break;
//                        case Activity.RESULT_CANCELED:
//                            service.sendStatus(SET_MESSAGES_STATUS, DEVICE_ID, settingsFirstSims.get("simId"), settingsFirstSims.get("secretKey"),
//                                    mesStatusDelivered.get(j).getMessageID(), STATUS_INDELIVERED).enqueue(new Callback<Status>() {
//                                @Override
//                                public void onResponse(Call<Status> call, Response<Status> response) {
//                                    if (response.body() != null) {
//                                        Log.d(TAG, "Message status: " + response.body().getStatus());
//                                    }
//                                    j++;
//                                    if (j == mesStatusDelivered.size()) {
//                                        j = 0;
//                                    }
//                                    counter = 0;
//                                }
//
//                                @Override
//                                public void onFailure(Call<Status> call, Throwable t) {
//                                    counter++;
//                                    Log.d(TAG, "Error get status sent " + t.getMessage());
//                                }
//                            });
//                            break;
//                    }
//                } else {
//                    mesStatusDelivered.clear();
//                    j = 0;
//                }
//            }
//        }
//    };
//    private Timer timerGetSmsFromPhone = new Timer();

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(sentReceiver, new IntentFilter(SENT_SMS));
//        registerReceiver(deliverReceiver, new IntentFilter(DELIVER_SMS));
    }

    @Override
    protected void onStop() {
        super.onStop();
//        unregisterReceiver(sentReceiver);
//        unregisterReceiver(deliverReceiver);
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        sentPi = PendingIntent.getBroadcast(this, 0, sentIntent, 0);
        deliverPi = PendingIntent.getBroadcast(this, 0, deliverIntent, 0);
        startButton = (Button) findViewById(R.id.start_button);
        settings = (Button) findViewById(R.id.settings_button);
        final Timer[] timer = {new Timer()};
        settings.setClickable(true);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settings.isEnabled()) {
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
                timer[0].cancel();
                timerGetSmsFromPhone.cancel();
                Log.d(TAG, "App stopped ");
                Toast.makeText(getApplicationContext(), "App stopped ", Toast.LENGTH_SHORT).show();
                settings.setClickable(true);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Timer getSmsFromServer_firstSim = new Timer();
                Timer sendSmsFromPhone_firstSim = new Timer();
                if (settingsFirstSims.get("status").equals("false")) {
                    Toast.makeText(getApplicationContext(), "Активируйте SIM ", Toast.LENGTH_SHORT).show();
                } else {
                    settings.setClickable(false);
                    Log.d(TAG, "App started ");
                    if (settingsFirstSims.get("status").equals("true")) {
                        Toast.makeText(getApplicationContext(), "App started ", Toast.LENGTH_SHORT).show();
                        getSmsFromServer_firstSim.schedule(new GetSmsFromServerTimerTask(settingsFirstSims, messageListFirstSim), 0L, Long.parseLong(settingsFirstSims.get("frequencyOfRequests"), 10) * 1000);
                        sendSmsFromPhone_firstSim.schedule(new SendSmsFromPhoneTimerTask(messageListFirstSim, settingsFirstSims, sentPi, deliverPi, getBaseContext()), 0L, Long.parseLong(settingsFirstSims.get("frequencyOfSmsSending"), 10) * 1000);

                    }

//                    if (settingsSecondSims.get("status").equals("true")) {
//                        Toast.makeText(getApplicationContext(), "App started ", Toast.LENGTH_SHORT).show();
//                        getSmsFromServer_SecondSim.schedule(new GetSmsFromServerTimerTask(settingsSecondSims, messageListSecondSim), 0, Long.parseLong(settingsFirstSims.get("frequencyOfRequests"), 10) * 1000);
//                    }

                    Timer timerTwo = new Timer();
                    timerTwo.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!messageListFirstSim.isEmpty()) {
                                if (i <= messageListFirstSim.size() - 1) {
                                    service.sendStatus(SET_MESSAGES_STATUS, DEVICE_ID, settingsFirstSims.get("simId"),
                                            settingsFirstSims.get("secretKey"), messageListFirstSim.get(i).getMessageID(), STATUS_PENDING).enqueue(new Callback<Status>() {
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
                                    mesStatus = messageListFirstSim;
                                    SmsManager smsManager;
                                    if (Build.VERSION.SDK_INT <= LOLLIPOP_MR1) {
                                        smsManager = SmsManager.getDefault();
                                    } else {
                                        smsManager = getSmsManager(simList.get(Integer.valueOf(SIM_ID) - 1)); //todo change to real sim id
                                    }
                                    final ArrayList<Integer> simCardList = new ArrayList<>();
                                    SubscriptionManager subscriptionManager;
                                    subscriptionManager = SubscriptionManager.from(getBaseContext());
                                    final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager
                                            .getActiveSubscriptionInfoList();
                                    for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                                        int subscriptionId = subscriptionInfo.getSubscriptionId();
                                        simCardList.add(subscriptionId);
                                    }

                                    if (messageListFirstSim.get(i).getMessage().length() > 100) {
                                        ArrayList<String> parts = smsManager.divideMessage(messageListFirstSim.get(i).getMessage());
                                        int numParts = parts.size();
                                        for (int i = 0; i < numParts; i++) {
                                            sentIntents.add(PendingIntent.getBroadcast(getBaseContext(), 0, sentIntent, 0));
                                            deliveryIntents.add(PendingIntent.getBroadcast(getBaseContext(), 0, deliverIntent, 0));
                                        }
                                        smsManager.sendMultipartTextMessage(messageListFirstSim.get(i).getPhone(), null, parts, sentIntents, deliveryIntents);
                                    } else {
                                        SmsManager.getSmsManagerForSubscriptionId(simCardList.get(0)).sendTextMessage(messageListFirstSim.get(i).getPhone(), null, messageListFirstSim.get(i).getMessage(), sentPi, deliverPi);
//                                        SmsManager.getSmsManagerForSubscriptionId(simCardList.get(1)).sendTextMessage("+375336859996", null, "hello world", sentPi, sentPi);
//                                        smsManager.sendTextMessage(messageListFirstSim.get(i).getPhone(), null, messageListFirstSim.get(i).getMessage(), sentPi, deliverPi);
                                    }
                                    i++;
                                } else {
                                    mesStatus = messageListFirstSim;
                                    messageListFirstSim.clear();
                                    i = 0;
                                }
                            }
                        }
                    }, 0L, Long.parseLong(settingsFirstSims.get("frequencyOfSmsSending"), 10) * 1000);
                }
                timerGetSmsFromPhone.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        TelephonyProvider telephonyProvider = new TelephonyProvider(getBaseContext());
                        final List<Sms> smses = telephonyProvider.getSms(TelephonyProvider.Filter.INBOX).getList();
                        Log.d(TAG, "List size: " + smses.size());
                        if (!smses.isEmpty()) {
                            for (n = 0; n <= smses.size(); n++) {
                                if (n == smses.size()) {
                                    getContentResolver().delete(Uri.parse("content://sms"), null, null);
                                    smses.clear();
                                    n = 0;
                                }
                                try {
                                    service.sendSms(NEW_INCOME_MESSAGE, DEVICE_ID, SIM_ID, settingsFirstSims.get("secretKey"),
                                            smses.get(n).address, smses.get(n).body, getMessageIdForSms(smses.get(n).address, smses.get(n).body)).enqueue(new Callback<Status>() {
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
                                } catch (Exception e) {
                                    Log.d(TAG, "No new sms ");
                                }
                            }
                        } else {
                            Log.d(TAG, "No new sms ");
                        }

                    }
                }, 0L, 30L * 1000);
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


    @TargetApi(LOLLIPOP_MR1)
    private List<Integer> getSimList() {
        final ArrayList<Integer> simCardList = new ArrayList<>();
        SubscriptionManager subscriptionManager;
        subscriptionManager = SubscriptionManager.from(this);
        final List<SubscriptionInfo> subscriptionInfoList = subscriptionManager
                .getActiveSubscriptionInfoList();
        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            simCardList.add(subscriptionId);
        }
        return simCardList;
    }

    @TargetApi(LOLLIPOP_MR1)
    private SmsManager getSmsManager(int id) {
        return SmsManager.getSmsManagerForSubscriptionId(id);
    }
}
