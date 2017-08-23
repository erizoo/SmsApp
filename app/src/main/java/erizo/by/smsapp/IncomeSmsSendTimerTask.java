package erizo.by.smsapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

//import erizo.by.smsapp.model.IncomeSms;
import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.model.Status;
import erizo.by.smsapp.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static erizo.by.smsapp.activity.MainActivity.logService;

public class IncomeSmsSendTimerTask extends TimerTask implements SmsStatus {

    private static final String TAG = IncomeSmsSendTimerTask.class.getSimpleName();

    private Context context;
    private Map<String, String> simSettingsFirstSim;
    private Map<String, String> simSettingsSecondSim;
    private Retrofit retrofit;
    private APIService service;
    private Integer systemErrorCounter;

    public IncomeSmsSendTimerTask(Context context, Map<String, String> simSettingsFirstSim, Map<String, String> simSettingsSecondSim, Integer systemErrorCounter) {
        this.context = context;
        this.simSettingsFirstSim = simSettingsFirstSim;
        this.simSettingsSecondSim = simSettingsSecondSim;
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(simSettingsFirstSim.get("url"))
                .build();
        service = retrofit.create(APIService.class);
        this.systemErrorCounter = systemErrorCounter;
    }

    @Override
    public void run() {
        List<Message> messages = getSMS();
        Log.d(TAG, "List size: " + messages.size());
        if (!messages.isEmpty()) {
            for (Message message : messages) {
                try {
//                    switch (message.getInternalSimIds()) {
//                        case "1":
//                            service.sendSms(
//                                    NEW_INCOME_MESSAGE,
//                                    simSettingsFirstSim.get("deviceId"),
//                                    simSettingsFirstSim.get("simId"),
//                                    simSettingsFirstSim.get("secretKey"),
//                                    message.getAddress(),
//                                    message.getBody(),
//                                    getMessageIdForSmsFirstSim(
//                                            message.getAddress(),
//                                            message.getBody())).enqueue(new Callback<Status>() {
//                                @Override
//                                public void onResponse(Call<Status> call, Response<Status> response) {
//                                    if (response.body() != null) {
//                                        logService.appendLog("Message status: " + response.body().getStatus() + TAG);
//                                        Log.d(TAG, "Message status: " + response.body().getStatus());
//                                        systemErrorCounter = 0;
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<Status> call, Throwable t) {
//                                    systemErrorCounter++;
//                                    Log.e(TAG, t.getMessage());
//                                    logService.appendLog(t.getMessage());
//                                    Log.e(TAG, "Error get status pending " + t.getMessage());
//                                }
//                            });
//                            break;
//                        case "2":
//                            service.sendSms(
//                                    NEW_INCOME_MESSAGE,
//                                    simSettingsSecondSim.get("deviceId"),
//                                    simSettingsSecondSim.get("simId"),
//                                    simSettingsSecondSim.get("secretKey"),
//                                    message.getAddress(),
//                                    message.getBody(),
//                                    getMessageIdForSmsSecondSim(
//                                            message.getAddress(),
//                                            message.getBody())).enqueue(new Callback<Status>() {
//                                @Override
//                                public void onResponse(Call<Status> call, Response<Status> response) {
//                                    if (response.body() != null) {
//                                        logService.appendLog("Message status: " + response.body().getStatus() + TAG);
//                                        Log.d(TAG, "Message status: " + response.body().getStatus());
//                                        systemErrorCounter = 0;
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<Status> call, Throwable t) {
//                                    systemErrorCounter++;
//                                    Log.e(TAG, t.getMessage());
//                                    logService.appendLog(t.getMessage());
//                                    Log.e(TAG, "Error get status pending " + t.getMessage());
//                                }
//                            });
//                            break;
//                        default:
//                            Log.d(TAG, "SIM ID is different ");
//                            break;
//                    }

                } catch (Exception e) {
                    logService.appendLog("No new sms " + TAG);
                    Log.d(TAG, "No new sms ");
                }
            }
            messages.clear();
        } else {
            logService.appendLog("No new sms " + TAG);
            Log.d(TAG, "No new sms ");
        }
    }

    private String getMessageIdForSmsFirstSim(String phone, String message) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        String[] item = formattedDate.split(" ");
        String[] itemOne = item[0].split("-");
        String[] itemTwo = item[1].split(":");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemOne[0]).append(itemOne[1]).append(itemOne[2]).append(itemTwo[0]).append(itemTwo[1]).append(itemTwo[2])
                .append("0000").append(simSettingsFirstSim.get("deviceId")).append("0000").append(simSettingsFirstSim.get("simId")).append(MD5_Hash(phone + message));
        Log.d(TAG, String.valueOf(stringBuilder).toUpperCase());
        return String.valueOf(stringBuilder).toUpperCase();
    }

    private String getMessageIdForSmsSecondSim(String phone, String message) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        String[] item = formattedDate.split(" ");
        String[] itemOne = item[0].split("-");
        String[] itemTwo = item[1].split(":");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemOne[0]).append(itemOne[1]).append(itemOne[2]).append(itemTwo[0]).append(itemTwo[1]).append(itemTwo[2])
                .append("0000").append(simSettingsSecondSim.get("deviceId")).append("0000").append(simSettingsSecondSim.get("simId")).append(MD5_Hash(phone + message));
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

    private List<Message> getSMS() {
        List<Message> sms = new ArrayList<>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, null);

        if (cur != null) {
            while (cur.moveToNext()) {
                String address = cur.getString(cur.getColumnIndex("address"));
                Log.d(TAG, "Address from cursor => " + cur.getString(cur.getColumnIndex("address")));
                String body = cur.getString(cur.getColumnIndexOrThrow("body"));
                Log.d(TAG, "Body from cursor => " + cur.getString(cur.getColumnIndex("body")));
                String simId = cur.getString(cur.getColumnIndexOrThrow("sub_id"));
                Log.d(TAG, "SimId from cursor => " + cur.getString(cur.getColumnIndex("sub_id")));
                sms.add(new Message(body, address, simId));
            }
        }
        return sms;
    }
}
