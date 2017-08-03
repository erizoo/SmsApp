package erizo.by.smsapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import erizo.by.smsapp.model.IncomeSms;
import erizo.by.smsapp.model.Message;
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

import static erizo.by.smsapp.activity.MainActivity.logService;

public class IncomeSmsSendTimerTask extends TimerTask implements SmsStatus {

    private static final String TAG = IncomeSmsSendTimerTask.class.getSimpleName();
    private static final int SIM_SLOT_NUMBER = 27;
    private static final int PHONE = 2;
    private static final int MESSAGE = 12;
    private static final int P_ID = 1;

    private Context context;
    private Map<String, String> simSettings;

    private Retrofit retrofit;
    private APIService service;
    private Integer systemErrorCounter;

    public IncomeSmsSendTimerTask(Context context, Map<String, String> simSettings, Integer systemErrorCounter) {
        this.context = context;
        this.simSettings = simSettings;
        retrofit  = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(simSettings.get("url"))
                .build();
        service = retrofit.create(APIService.class);
        this.systemErrorCounter = systemErrorCounter;
    }

    @Override
    public void run() {
//        Queue<Message> messages = getCurrentSimIncomeMessageList();
        List<IncomeSms> messages = getSMS();
        Log.d(TAG, "List size: " + messages.size());
        if (!messages.isEmpty()) {
            for (IncomeSms message : messages) {
                try {
                    if (message.getSimId().equals("1")){
                        service.sendSms(
                                NEW_INCOME_MESSAGE,
                                simSettings.get("deviceId"),
                                "375336859996",
                                simSettings.get("secretKey"),
                                message.getAddress(),
                                message.getBody(),
                                getMessageIdForSms(
                                        message.getAddress(),
                                        message.getBody())).enqueue(new Callback<Status>() {
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
                    }else {
                        Log.d(TAG, "SIM ID is different ");
                    }

                } catch (Exception e) {
                    logService.appendLog( "No new sms "  + TAG);
                    Log.d(TAG, "No new sms ");
                }
            }
            messages.clear();
        } else {
            logService.appendLog( "No new sms "  + TAG);
            Log.d(TAG, "No new sms ");
        }
    }

    private String getMessageIdForSms(String phone, String message) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        String[] item = formattedDate.split(" ");
        String[] itemOne = item[0].split("-");
        String[] itemTwo = item[1].split(":");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemOne[0]).append(itemOne[1]).append(itemOne[2]).append(itemTwo[0]).append(itemTwo[1]).append(itemTwo[2])
                .append("0000").append(simSettings.get("deviceId")).append("0000").append(simSettings.get("simId")).append(MD5_Hash(phone + message));
        Log.d(TAG, String.valueOf(stringBuilder).toUpperCase());
        return String.valueOf(stringBuilder).toUpperCase();
    }

    private static String MD5_Hash(String s) {
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

    private List<IncomeSms> getSMS(){
        List<IncomeSms> sms = new ArrayList<>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, null);

        for (int i = 0; i <= 21; i++){
            if (cur != null) {
                String cursor = cur.getColumnName(i);
                Log.d(TAG, " from cursor => " + cursor);
            }
        }

        if (cur != null) {
            while (cur.moveToNext()) {
                String address = cur.getString(cur.getColumnIndex("address"));
                Log.d(TAG, "Address from cursor => " + cur.getString(cur.getColumnIndex("address")));
                String body = cur.getString(cur.getColumnIndexOrThrow("body"));
                Log.d(TAG, "Body from cursor => " + cur.getString(cur.getColumnIndex("body")));
                String simId = cur.getString(cur.getColumnIndexOrThrow("sub_id"));
                Log.d(TAG, "SimId from cursor => " + cur.getString(cur.getColumnIndex("sub_id")));
                sms.add(new IncomeSms(body, address, simId));
            }
        }


        return sms;
    }

//    private Queue<Message> getCurrentSimIncomeMessageList() {
//        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
//        Queue<Message> messages = new ConcurrentLinkedQueue<>();
//
//        if (cursor.moveToFirst()) {
//            Log.d(TAG, "Cursor : " + cursor.toString());
//            logService.appendLog( "Cursor : " + cursor.toString()  + TAG);
//            for (int i = 0; i < cursor.getColumnNames().length; i++) {
//                Log.d(TAG, cursor.getColumnName(i) + ": " + cursor.getString(i));
//            }
//            do {
//                if (cursor.getString(SIM_SLOT_NUMBER).equals(simSettings.get("android_sim_slot"))) {
//                    Message message = new Message(cursor.getString(PHONE), cursor.getString(MESSAGE));
//                    messages.add(message);
//                    Log.d(TAG, "Added to income message list message : " + message.toString());
//                    logService.appendLog( "Added to income message list message : " + message.toString()  + TAG);
//                    String pid = cursor.getString(P_ID);
//                    String uri = "content://sms/conversations/" + pid;
//                    context.getContentResolver().delete(Uri.parse(uri), null, null);
//                    Log.d(TAG, "Message was deleted");
//                    logService.appendLog( "Message was deleted"  + TAG);
//                }
//            } while (cursor.moveToNext());
//        } else {
//            logService.appendLog("Empty sms input box" + TAG);
//            Log.d(TAG, "Empty sms input box");
//        }
//
//        return messages;
//    }

}
