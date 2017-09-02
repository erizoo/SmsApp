package erizo.by.smsapp.timertasks;

import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Queue;
import java.util.TimerTask;

import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.model.Status;
import erizo.by.smsapp.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static erizo.by.smsapp.App.firstSimSettings;
import static erizo.by.smsapp.App.secondSimSettings;
import static erizo.by.smsapp.SimSettings.ANDROID_SIM_SLOT;
import static erizo.by.smsapp.SmsStatus.NEW_INCOME_MESSAGE;
import static erizo.by.smsapp.activity.MainActivity.logService;

public class IncomeSmsSendTimerTask extends TimerTask {

    private static final String TAG = IncomeSmsSendTimerTask.class.getSimpleName();

    private Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(firstSimSettings.get("url"))
            .build();
    private APIService service = retrofit.create(APIService.class);
    private Queue<Message> incomeMessages;
    private Integer systemErrorCounter;

    public IncomeSmsSendTimerTask(Queue<Message> incomeMessages, Integer systemErrorCounter) {
        this.incomeMessages = incomeMessages;
        this.systemErrorCounter = systemErrorCounter;
    }

    @Override
    public void run() {
        while (!incomeMessages.isEmpty()) {
            Log.d(TAG, "starting send sms to server");
            logService.appendLog(TAG + " : " + "starting send sms to server");
            Message message = incomeMessages.poll();
            String messageId;
            if (message.getInternalSimIds().equals(firstSimSettings.get(ANDROID_SIM_SLOT))) {
                messageId = getMessageIdForSmsFirstSim(message.getPhone(), message.getMessage());
            } else {
                messageId = getMessageIdForSmsSecondSim(message.getPhone(), message.getMessage());
            }
            service.sendSms(
                    NEW_INCOME_MESSAGE,
                    firstSimSettings.get("deviceId"),
                    message.getInternalSimIds(),
                    firstSimSettings.get("secretKey"),
                    message.getPhone(),
                    message.getMessage(),
                    messageId).enqueue(new Callback<Status>() {
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

    private String getMessageIdForSmsFirstSim(String phone, String message) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        String[] item = formattedDate.split(" ");
        String[] itemOne = item[0].split("-");
        String[] itemTwo = item[1].split(":");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemOne[0]).append(itemOne[1]).append(itemOne[2]).append(itemTwo[0]).append(itemTwo[1]).append(itemTwo[2])
                .append("0000").append(firstSimSettings.get("deviceId")).append("0000").append(firstSimSettings.get("simId")).append(MD5_Hash(phone + message));
        Log.d(TAG, "Message id for inbox sms from first sim" +
                String.valueOf(stringBuilder).toUpperCase());
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
                .append("0000").append(secondSimSettings.get("deviceId")).append("0000").append(secondSimSettings.get("simId")).append(MD5_Hash(phone + message));
        Log.d(TAG, "Message id for inbox sms from second sim" +
                String.valueOf(stringBuilder).toUpperCase());
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
