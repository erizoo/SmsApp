package erizo.by.smsapp.timertasks;

import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.Map;
import java.util.TimerTask;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import erizo.by.smsapp.asynctasks.SendEmailAsyncTask;
import erizo.by.smsapp.model.Mail;
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
import static erizo.by.smsapp.SimSettings.LOGIN_FOR_EMAIL;
import static erizo.by.smsapp.SimSettings.MAX_NUMBER_ERROR;
import static erizo.by.smsapp.SimSettings.PASSWORD_FOR_EMAIL;
import static erizo.by.smsapp.SimSettings.SECRET_KEY;
import static erizo.by.smsapp.SimSettings.SIM_ID;
import static erizo.by.smsapp.SimSettings.URL;

/**
 * Created by valera on 2.9.17.
 */

public class SystemErrorCounterTestingTimerTask extends TimerTask {

    private static final String TAG = SystemErrorCounterTestingTimerTask.class.getSimpleName();

    private static final String SPLITTER = ";";

    private Integer errorCounter;
    private Map<String, String> settings;
    private SmsManager smsManager;
    private APIService service;

    public SystemErrorCounterTestingTimerTask(Integer errorCounter, Map<String, String> settings) {
        this.errorCounter = errorCounter;
        this.settings = settings;
        smsManager = SmsManager.getSmsManagerForSubscriptionId(Integer.valueOf(settings.get(ANDROID_SIM_SLOT)));
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(settings.get(URL))
                .build();
        service = retrofit.create(APIService.class);
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
            service.sendAlert("alert",
                    settings.get(DEVICE_ID),
                    settings.get(SIM_ID),
                    settings.get(SECRET_KEY),
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
            String[] emails = settings.get(EMAILS).split(SPLITTER);
            Mail mail = new Mail(settings.get(LOGIN_FOR_EMAIL), settings.get(PASSWORD_FOR_EMAIL));
            mail.setSubject("error overflow");
            mail.setBody("error overflow");
            mail.setTo(emails);
            new SendEmailAsyncTask(mail).execute();
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
