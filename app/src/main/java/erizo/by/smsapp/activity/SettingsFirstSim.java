package erizo.by.smsapp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import erizo.by.smsapp.R;
import erizo.by.smsapp.service.TinyDb;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static erizo.by.smsapp.App.firstSimSettings;
import static erizo.by.smsapp.SimSettings.ANDROID_SIM_SLOT;
import static erizo.by.smsapp.SimSettings.DEVICE_ID;
import static erizo.by.smsapp.SimSettings.EMAIL;
import static erizo.by.smsapp.SimSettings.FREQUENCY_ALERT;
import static erizo.by.smsapp.SimSettings.FREQUENCY_OF_REQUESTS;
import static erizo.by.smsapp.SimSettings.FREQUENCY_OF_SMS_SENDING;
import static erizo.by.smsapp.SimSettings.LOGIN_FOR_EMAIL;
import static erizo.by.smsapp.SimSettings.MAX_NUMBERS_MESSAGES;
import static erizo.by.smsapp.SimSettings.MAX_NUMBER_ERROR;
import static erizo.by.smsapp.SimSettings.NUMBERS_ALERTS;
import static erizo.by.smsapp.SimSettings.PASSWORD_FOR_EMAIL;
import static erizo.by.smsapp.SimSettings.PORT_FOR_EMAIL;
import static erizo.by.smsapp.SimSettings.SECRET_KEY;
import static erizo.by.smsapp.SimSettings.SIM_ID;
import static erizo.by.smsapp.SimSettings.SIM_IDENTIFIER;
import static erizo.by.smsapp.SimSettings.SIM_SLOT;
import static erizo.by.smsapp.SimSettings.STATUS;
import static erizo.by.smsapp.SimSettings.TIME_MESSAGES;
import static erizo.by.smsapp.SimSettings.URL;


public class SettingsFirstSim extends Activity {

    private static final String TAG = SettingsFirstSim.class.getSimpleName();
    private static final String FIRST_SIM_SLOT_NUMBER = "0";
    private static final String SETTINGS_FIRST_SIM = "first_sim_settings";

    private Switch aSwitch;
    private EditText deviceId, simId, url, secretKey, frequencyOfRequests, frequencyOfSmsSending,
            frequencyAlert, numbersAlerts, email, loginForEmail,
            passwordForEmail, portForEmail, maxNumberError, maxNumbersMessages,
            timeMessages;
    private Button saveSettings;
    private TinyDb tinyDb;

    private List<SubscriptionInfo> subscriptionInfoList = SubscriptionManager.from(this).getActiveSubscriptionInfoList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_first_sim_activity);
        tinyDb = new TinyDb(this);
        aSwitch = (Switch) findViewById(R.id.switch_first_sim);
        deviceId = (EditText) findViewById(R.id.device_id_first_sim_edit);
        simId = (EditText) findViewById(R.id.sim_first_id_edit);
        url = (EditText) findViewById(R.id.url_first_sim_edit);
        secretKey = (EditText) findViewById(R.id.secret_key_first_sim_edit);
        frequencyOfRequests = (EditText) findViewById(R.id.frequency_requests_first_sim_edit_text);
        frequencyOfSmsSending = (EditText) findViewById(R.id.frequency_sent_sms_first_sim_edit_text);
        frequencyAlert = (EditText) findViewById(R.id.frequency_alert_first_sim_edit);
        numbersAlerts = (EditText) findViewById(R.id.numbers_alerts_first_sim_edit);
        email = (EditText) findViewById(R.id.mail_first_sim_edit);
        loginForEmail = (EditText) findViewById(R.id.login_first_sim_edit);
        passwordForEmail = (EditText) findViewById(R.id.password_first_sim_edit);
        portForEmail = (EditText) findViewById(R.id.port_sim_first_edit);
        maxNumberError = (EditText) findViewById(R.id.max_number_errors_first_sim_edit);
        maxNumbersMessages = (EditText) findViewById(R.id.max_numbers_messages_first_sim_edit);
        timeMessages = (EditText) findViewById(R.id.time_messages_first_sim_edit);
        saveSettings = (Button) findViewById(R.id.button_save_test_settings_first_sim);

        deviceId.setText(firstSimSettings.get(DEVICE_ID));
        simId.setText(firstSimSettings.get(SIM_ID));
        url.setText(firstSimSettings.get(URL));
        secretKey.setText(firstSimSettings.get(SECRET_KEY));
        frequencyOfRequests.setText(firstSimSettings.get(FREQUENCY_OF_REQUESTS));
        frequencyOfSmsSending.setText(firstSimSettings.get(FREQUENCY_OF_SMS_SENDING));
        frequencyAlert.setText(firstSimSettings.get(FREQUENCY_ALERT));
        numbersAlerts.setText(firstSimSettings.get(NUMBERS_ALERTS));
        email.setText(firstSimSettings.get(EMAIL));
        loginForEmail.setText(firstSimSettings.get(LOGIN_FOR_EMAIL));
        passwordForEmail.setText(firstSimSettings.get(PASSWORD_FOR_EMAIL));
        portForEmail.setText(firstSimSettings.get(PORT_FOR_EMAIL));
        maxNumberError.setText(firstSimSettings.get(MAX_NUMBER_ERROR));
        maxNumbersMessages.setText(firstSimSettings.get(MAX_NUMBERS_MESSAGES));
        timeMessages.setText(firstSimSettings.get(TIME_MESSAGES));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firstSimSettings.put(STATUS, "true");
                    Toast.makeText(getApplicationContext(), "SET ON", Toast.LENGTH_SHORT).show();
                } else {
                    firstSimSettings.put(STATUS, "false");
                    Toast.makeText(getApplicationContext(), "SET OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            if (firstSimSettings.get(STATUS).equals("false")){
                aSwitch.setChecked(false);
            } else {
                aSwitch.setChecked(true);
            }
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstSimSettings.put(SIM_SLOT, FIRST_SIM_SLOT_NUMBER);
                Log.d(TAG, firstSimSettings.get(SIM_SLOT));
                firstSimSettings.put(DEVICE_ID, deviceId.getText().toString());
                Log.d(TAG, firstSimSettings.get(DEVICE_ID));
                firstSimSettings.put(SIM_ID, simId.getText().toString());
                Log.d(TAG, firstSimSettings.get(SIM_ID));
                firstSimSettings.put(URL, url.getText().toString());
                Log.d(TAG, firstSimSettings.get(URL));
                firstSimSettings.put(SECRET_KEY, secretKey.getText().toString());
                Log.d(TAG, firstSimSettings.get(SECRET_KEY));
                firstSimSettings.put(FREQUENCY_OF_REQUESTS, frequencyOfRequests.getText().toString());
                Log.d(TAG, firstSimSettings.get(FREQUENCY_OF_REQUESTS));
                firstSimSettings.put(FREQUENCY_OF_SMS_SENDING, frequencyOfSmsSending.getText().toString());
                Log.d(TAG, firstSimSettings.get(FREQUENCY_OF_SMS_SENDING));
                if (frequencyOfRequests.getText().toString().equals("")) {
                    firstSimSettings.put(FREQUENCY_OF_REQUESTS, "60");
                }
                if (frequencyOfSmsSending.getText().toString().equals("")) {
                    firstSimSettings.put(FREQUENCY_OF_SMS_SENDING, "60");
                }
                firstSimSettings.put(FREQUENCY_ALERT, frequencyAlert.getText().toString());
                Log.d(TAG, firstSimSettings.get(FREQUENCY_ALERT));
                firstSimSettings.put(NUMBERS_ALERTS, numbersAlerts.getText().toString());
                Log.d(TAG, firstSimSettings.get(NUMBERS_ALERTS));
                firstSimSettings.put(EMAIL, email.getText().toString());
                Log.d(TAG, firstSimSettings.get(EMAIL));
                firstSimSettings.put(LOGIN_FOR_EMAIL, loginForEmail.getText().toString());
                Log.d(TAG, firstSimSettings.get(LOGIN_FOR_EMAIL));
                firstSimSettings.put(PASSWORD_FOR_EMAIL, passwordForEmail.getText().toString());
                Log.d(TAG, firstSimSettings.get(PASSWORD_FOR_EMAIL));
                firstSimSettings.put(PORT_FOR_EMAIL, portForEmail.getText().toString());
                Log.d(TAG, firstSimSettings.get(PORT_FOR_EMAIL));
                firstSimSettings.put(MAX_NUMBER_ERROR, maxNumberError.getText().toString());
                Log.d(TAG, firstSimSettings.get(MAX_NUMBER_ERROR));
                firstSimSettings.put(MAX_NUMBERS_MESSAGES, maxNumbersMessages.getText().toString());
                Log.d(TAG, firstSimSettings.get(MAX_NUMBERS_MESSAGES));
                firstSimSettings.put(TIME_MESSAGES, timeMessages.getText().toString());
                Log.d(TAG, firstSimSettings.get(TIME_MESSAGES));
                firstSimSettings.put(ANDROID_SIM_SLOT, String.valueOf(getAndroidFirstSimSlotId()));
                Log.d(TAG, firstSimSettings.get(ANDROID_SIM_SLOT));
                firstSimSettings.put(SIM_IDENTIFIER, getSimIdentifier());
                Log.d(TAG, firstSimSettings.get(SIM_IDENTIFIER));

                Gson gson = new Gson();
                String serializedSettings = gson.toJson(firstSimSettings);
                tinyDb.putString(SETTINGS_FIRST_SIM, serializedSettings);

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @TargetApi(LOLLIPOP_MR1)
    private int getAndroidFirstSimSlotId() {
        if (SDK_INT >= LOLLIPOP_MR1) {
            return subscriptionInfoList.get(0).getSubscriptionId();
        }
        return Integer.valueOf(firstSimSettings.get(SIM_SLOT)) + 1;
    }

    private String getSimIdentifier() {
        return subscriptionInfoList.get(0).getIccId();
    }
}

