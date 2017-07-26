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
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import erizo.by.smsapp.model.Status;
import erizo.by.smsapp.service.APIService;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static erizo.by.smsapp.App.firstSimSettings;

public class IncomeSmsSendTimerTask extends TimerTask implements SmsStatus {

    private static final String TAG = IncomeSmsSendTimerTask.class.getSimpleName();

    private Context context;
    private Map<String, String> simSettings;

    private Retrofit retrofit;
    private APIService service;

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
        TelephonyProvider telephonyProvider = new TelephonyProvider(context);
        final List<Sms> smsList = telephonyProvider.getSms(TelephonyProvider.Filter.INBOX).getList();
        Log.d(TAG, "List size: " + smsList.size());
        if (!smsList.isEmpty()) {
            for (int n = 0; n <= smsList.size(); n++) {
                if (n == smsList.size()) {
                    context.getContentResolver().delete(Uri.parse("content://sms"), null, null);
                    smsList.clear();
                    n = 0;
                }
                try {
                    service.sendSms(
                            NEW_INCOME_MESSAGE,
                            simSettings.get("deviceId"),
                            simSettings.get("simId"),
                            simSettings.get("secretKey"),
                            smsList.get(n).address,
                            smsList.get(n).body,
                            getMessageIdForSms(
                                    smsList.get(n).address,
                                    smsList.get(n).body)).enqueue(new Callback<Status>() {
                        @Override
                        public void onResponse(Call<Status> call, Response<Status> response) {
                            if (response.body() != null) {
                                Log.d(TAG, "Message status: " + response.body().getStatus());
                            }
//                            counter = 0;
                        }

                        @Override
                        public void onFailure(Call<Status> call, Throwable t) {
//                            counter++;
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

    private String getMessageIdForSms(String phone, String message) {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
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

}
