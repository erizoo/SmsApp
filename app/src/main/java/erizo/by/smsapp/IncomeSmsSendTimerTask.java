package erizo.by.smsapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.model.Status;
import erizo.by.smsapp.service.APIService;
import erizo.by.smsapp.service.FileLogService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    private FileLogService logService = new FileLogService();

    public IncomeSmsSendTimerTask(Context context, Map<String, String> simSettings) {
        this.context = context;
        this.simSettings = simSettings;
        retrofit  = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(simSettings.get("url"))
                .build();
        service = retrofit.create(APIService.class);
    }

    @Override
    public void run() {
        Queue<Message> messages = getCurrentSimIncomeMessageList();
        Log.d(TAG, "List size: " + messages.size());
        if (!messages.isEmpty()) {
            for (Message message : messages) {
                try {
                    service.sendSms(
                            NEW_INCOME_MESSAGE,
                            simSettings.get("deviceId"),
                            simSettings.get("simId"),
                            simSettings.get("secretKey"),
                            message.getPhone(),
                            message.getMessage(),
                            getMessageIdForSms(
                                    message.getPhone(),
                                    message.getMessage())).enqueue(new Callback<Status>() {
                        @Override
                        public void onResponse(Call<Status> call, Response<Status> response) {
                            if (response.body() != null) {
                                Log.d(TAG, "Message status: " + response.body().getStatus());
                            }
                        }

                        @Override
                        public void onFailure(Call<Status> call, Throwable t) {
//                            counter++;
                            Log.e(TAG, t.getMessage());
                            logService.appendLog(t.getMessage());
                            Log.e(TAG, "Error get status pending " + t.getMessage());
                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "No new sms ");
                }
            }
            messages.clear();
        } else {
            Log.d(TAG, "No new sms ");
        }
    }

    private String getMessageIdForSms(String phone, String message) {
        Calendar c = Calendar.getInstance();
        Log.d(TAG, "Current time => " + c.getTime());
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

    private Queue<Message> getCurrentSimIncomeMessageList() {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        Queue<Message> messages = new ConcurrentLinkedQueue<>();

        if (cursor.moveToFirst()) {
            Log.d(TAG, "Cursor : " + cursor.toString());
            for (int i = 0; i < cursor.getColumnNames().length; i++) {
                Log.d(TAG, cursor.getColumnName(i) + ": " + cursor.getString(i));
            }
            do {
                if (cursor.getString(SIM_SLOT_NUMBER).equals(simSettings.get("android_sim_slot"))) {
                    Message message = new Message(cursor.getString(PHONE), cursor.getString(MESSAGE));
                    messages.add(message);
                    Log.d(TAG, "Added to income message list message : " + message.toString());
                    String pid = cursor.getString(P_ID);
                    String uri = "content://sms/conversations/" + pid;
                    context.getContentResolver().delete(Uri.parse(uri), null, null);
                    Log.d(TAG, "Message was deleted");
                }
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "Empty sms input box");
        }

        return messages;
    }

}
