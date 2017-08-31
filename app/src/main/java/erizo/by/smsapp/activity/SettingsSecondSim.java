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
import static erizo.by.smsapp.App.secondSimSettings;
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

public class SettingsSecondSim extends Activity {

    private static final String TAG = SettingsSecondSim.class.getSimpleName();
    private static final String SECOND_SIM_SLOT_NUMBER = "1";
    private static final String SETTINGS_SECOND_SIM = "second_sim_settings";

    private Switch aSwitch;
    private EditText deviceId,simId, url, secretKey, frequencyOfRequests, frequencyOfSmsSending,
            frequencyAlert, numbersAlerts, email, loginForEmail,
            passwordForEmail, portForEmail, maxNumberError, maxNumbersMessages,
            timeMessages;
    private Button saveSettings;

    private TinyDb tinyDbSecondSim;

    private List<SubscriptionInfo> subscriptionInfoList = SubscriptionManager.from(this).getActiveSubscriptionInfoList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_second_sim_activity);
        tinyDbSecondSim = new TinyDb(this);
        aSwitch = (Switch) findViewById(R.id.switch_second_sim);
        deviceId = (EditText) findViewById(R.id.device_id_second_sim_edit);
        simId = (EditText) findViewById(R.id.sim_second_id_edit);
        url = (EditText) findViewById(R.id.url_second_sim_edit);
        secretKey = (EditText) findViewById(R.id.secret_key_second_sim_edit);
        frequencyOfRequests = (EditText) findViewById(R.id.frequency_requests_second_sim_edit_text);
        frequencyOfSmsSending = (EditText) findViewById(R.id.frequency_sent_sms_second_sim_edit_text);
        frequencyAlert = (EditText) findViewById(R.id.frequency_alert_second_sim_edit);
        numbersAlerts = (EditText) findViewById(R.id.numbers_alerts_second_sim_edit);
        email = (EditText) findViewById(R.id.mail_second_sim_edit);
        loginForEmail = (EditText) findViewById(R.id.login_second_sim_edit);
        passwordForEmail = (EditText) findViewById(R.id.password_second_sim_edit);
        portForEmail = (EditText) findViewById(R.id.port_sim_second_edit);
        maxNumberError = (EditText) findViewById(R.id.max_number_errors_second_sim_edit);
        maxNumbersMessages = (EditText) findViewById(R.id.max_numbers_messages_second_sim_edit);
        timeMessages = (EditText) findViewById(R.id.time_messages_second_sim_edit);
        saveSettings = (Button) findViewById(R.id.button_save_test_settings_second_sim);

        deviceId.setText(secondSimSettings.get(DEVICE_ID));
        simId.setText(secondSimSettings.get(SIM_ID));
        url.setText(secondSimSettings.get(URL));
        secretKey.setText(secondSimSettings.get(SECRET_KEY));
        frequencyOfRequests.setText(secondSimSettings.get(FREQUENCY_OF_REQUESTS));
        frequencyOfSmsSending.setText(secondSimSettings.get(FREQUENCY_OF_SMS_SENDING));
        frequencyAlert.setText(secondSimSettings.get(FREQUENCY_ALERT));
        numbersAlerts.setText(secondSimSettings.get(NUMBERS_ALERTS));
        email.setText(secondSimSettings.get(EMAIL));
        loginForEmail.setText(secondSimSettings.get(LOGIN_FOR_EMAIL));
        passwordForEmail.setText(secondSimSettings.get(PASSWORD_FOR_EMAIL));
        portForEmail.setText(secondSimSettings.get(PORT_FOR_EMAIL));
        maxNumberError.setText(secondSimSettings.get(MAX_NUMBER_ERROR));
        maxNumbersMessages.setText(secondSimSettings.get(MAX_NUMBERS_MESSAGES));
        timeMessages.setText(secondSimSettings.get(TIME_MESSAGES));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    secondSimSettings.put(STATUS, "true");
                    Toast.makeText(getApplicationContext(), "SET ON", Toast.LENGTH_SHORT).show();
                } else {
                    secondSimSettings.put(STATUS, "false");
                    Toast.makeText(getApplicationContext(), "SET OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            if (secondSimSettings.get(STATUS).equals("false")){
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
                secondSimSettings.put(SIM_SLOT, SECOND_SIM_SLOT_NUMBER);
                Log.d(TAG, secondSimSettings.get(SIM_SLOT));
                secondSimSettings.put(DEVICE_ID, deviceId.getText().toString());
                Log.d(TAG, secondSimSettings.get(DEVICE_ID));
                secondSimSettings.put(SIM_ID, simId.getText().toString());
                Log.d(TAG, secondSimSettings.get(SIM_ID));
                secondSimSettings.put(URL, url.getText().toString());
                Log.d(TAG, secondSimSettings.get(URL));
                secondSimSettings.put(SECRET_KEY, secretKey.getText().toString());
                Log.d(TAG, secondSimSettings.get(SECRET_KEY));
                secondSimSettings.put(FREQUENCY_OF_REQUESTS, frequencyOfRequests.getText().toString());
                Log.d(TAG, secondSimSettings.get(FREQUENCY_OF_REQUESTS));
                secondSimSettings.put(FREQUENCY_OF_SMS_SENDING, frequencyOfSmsSending.getText().toString());
                Log.d(TAG, secondSimSettings.get(FREQUENCY_OF_SMS_SENDING));
                if (frequencyOfRequests.getText().toString().equals("")) {
                    secondSimSettings.put(FREQUENCY_OF_REQUESTS, "60");
                }
                if (frequencyOfSmsSending.getText().toString().equals("")) {
                    secondSimSettings.put(FREQUENCY_OF_SMS_SENDING, "60");
                }
                secondSimSettings.put(FREQUENCY_ALERT, frequencyAlert.getText().toString());
                Log.d(TAG, secondSimSettings.get(FREQUENCY_ALERT));
                secondSimSettings.put(NUMBERS_ALERTS, numbersAlerts.getText().toString());
                Log.d(TAG, secondSimSettings.get(NUMBERS_ALERTS));
                secondSimSettings.put(EMAIL, email.getText().toString());
                Log.d(TAG, secondSimSettings.get(EMAIL));
                secondSimSettings.put(LOGIN_FOR_EMAIL, loginForEmail.getText().toString());
                Log.d(TAG, secondSimSettings.get(LOGIN_FOR_EMAIL));
                secondSimSettings.put(PASSWORD_FOR_EMAIL, passwordForEmail.getText().toString());
                Log.d(TAG, secondSimSettings.get(PASSWORD_FOR_EMAIL));
                secondSimSettings.put(PORT_FOR_EMAIL, portForEmail.getText().toString());
                Log.d(TAG, secondSimSettings.get(PORT_FOR_EMAIL));
                secondSimSettings.put(MAX_NUMBER_ERROR, maxNumberError.getText().toString());
                Log.d(TAG, secondSimSettings.get(MAX_NUMBER_ERROR));
                secondSimSettings.put(MAX_NUMBERS_MESSAGES, maxNumbersMessages.getText().toString());
                Log.d(TAG, secondSimSettings.get(MAX_NUMBERS_MESSAGES));
                secondSimSettings.put(TIME_MESSAGES, timeMessages.getText().toString());
                Log.d(TAG, secondSimSettings.get(TIME_MESSAGES));
                secondSimSettings.put(ANDROID_SIM_SLOT, String.valueOf(getAndroidSecondSimSlotId()));
                Log.d(TAG, secondSimSettings.get(ANDROID_SIM_SLOT));
                secondSimSettings.put(SIM_IDENTIFIER, getSimIdentifier());
                Log.d(TAG, secondSimSettings.get(SIM_IDENTIFIER));

                Gson gson = new Gson();
                String serializedSettings = gson.toJson(secondSimSettings);
                tinyDbSecondSim.putString(SETTINGS_SECOND_SIM, serializedSettings);

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @TargetApi(LOLLIPOP_MR1)
    private int getAndroidSecondSimSlotId() {
        if (SDK_INT >= LOLLIPOP_MR1) {
            return subscriptionInfoList.get(1).getSubscriptionId();
        }
        return Integer.valueOf(secondSimSettings.get(SIM_SLOT)) + 1;
    }

    private String getSimIdentifier() {
        return subscriptionInfoList.get(1).getIccId();
    }
}
