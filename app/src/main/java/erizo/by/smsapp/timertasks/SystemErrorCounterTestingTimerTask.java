package erizo.by.smsapp.timertasks;

import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.Map;
import java.util.TimerTask;

import erizo.by.smsapp.model.Status;
import erizo.by.smsapp.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static erizo.by.smsapp.SimSettings.ALERT_NUMBERS;
import static erizo.by.smsapp.SimSettings.ANDROID_SIM_SLOT;
import static erizo.by.smsapp.SimSettings.DEVICE_ID;
import static erizo.by.smsapp.SimSettings.EMAILS;
import static erizo.by.smsapp.SimSettings.MAX_NUMBER_ERROR;

/**
 * Created by valera on 2.9.17.
 */

public class SystemErrorCounterTestingTimerTask extends TimerTask {

    private static final String TAG = SystemErrorCounterTestingTimerTask.class.getSimpleName();

    private static final String SPLITTER = ";";

    private Integer errorCounter;
    private Map<String, String> settings;
    private SmsManager smsManager;

    public SystemErrorCounterTestingTimerTask(Integer errorCounter, Map<String, String> settings) {
        this.errorCounter = errorCounter;
        this.settings = settings;
        smsManager = SmsManager.getSmsManagerForSubscriptionId(Integer.valueOf(settings.get(ANDROID_SIM_SLOT)));
    }

    @Override
    public void run() {
        if (errorCounter > Integer.valueOf(settings.get(MAX_NUMBER_ERROR))) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    String[] alertNumbers = settings.get(ALERT_NUMBERS).split(SPLITTER);
                    for (String alertNumber : alertNumbers) {
                        sendSms(alertNumber);
                    }
                    return null;
                }
            };
            String[] urls = settings.get(EMAILS).split(SPLITTER);
            for (String url : urls) {
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(url)
                        .build();
                retrofit.create(APIService.class).sendAlert("alert",
                        settings.get(DEVICE_ID),
                        settings.get("simId"),
                        settings.get("secretKey"),
                        "0")
                        .enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(Call<Status> call, Response<Status> response) {
                                Log.d(TAG, "sent alert message");
                            }

                            @Override
                            public void onFailure(Call<Status> call, Throwable t) {
                                Log.d(TAG, "message alert unsent");
                            }
                        });
            }
        }
    }

    private void sendSms(String phoneNumber) {
        smsManager.sendTextMessage(phoneNumber,
                null,
                "Error counter limit out of range.",
                null,
                null);
    }
}
