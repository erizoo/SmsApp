package erizo.by.smsapp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
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

import static android.os.Build.VERSION.SDK;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static erizo.by.smsapp.App.firstSimSettings;


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

        deviceId.setText(firstSimSettings.get("deviceId"));
        simId.setText(firstSimSettings.get("simId"));
        url.setText(firstSimSettings.get("url"));
        secretKey.setText(firstSimSettings.get("secretKey"));
        frequencyOfRequests.setText(firstSimSettings.get("frequencyOfRequests"));
        frequencyOfSmsSending.setText(firstSimSettings.get("frequencyOfSmsSending"));
        frequencyAlert.setText(firstSimSettings.get("frequencyAlert"));
        numbersAlerts.setText(firstSimSettings.get("numbersAlerts"));
        email.setText(firstSimSettings.get("email"));
        loginForEmail.setText(firstSimSettings.get("loginForEmail"));
        passwordForEmail.setText(firstSimSettings.get("passwordForEmail"));
        portForEmail.setText(firstSimSettings.get("portForEmail"));
        maxNumberError.setText(firstSimSettings.get("maxNumberError"));
        maxNumbersMessages.setText(firstSimSettings.get("maxNumbersMessages"));
        timeMessages.setText(firstSimSettings.get("timeMessages"));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firstSimSettings.put("status", "true");
                    Toast.makeText(getApplicationContext(), "SET ON", Toast.LENGTH_SHORT).show();
                } else {
                    firstSimSettings.put("status", "false");
                    Toast.makeText(getApplicationContext(), "SET OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            if (firstSimSettings.get("status").equals("false")){
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
                firstSimSettings.put("simSlot", FIRST_SIM_SLOT_NUMBER);
                Log.d(TAG, firstSimSettings.get("simSlot"));
                firstSimSettings.put("deviceId", deviceId.getText().toString());
                Log.d(TAG, firstSimSettings.get("deviceId"));
                firstSimSettings.put("simId", simId.getText().toString());
                Log.d(TAG, firstSimSettings.get("simId"));
                firstSimSettings.put("url", url.getText().toString());
                Log.d(TAG, firstSimSettings.get("url"));
                firstSimSettings.put("secretKey", secretKey.getText().toString());
                Log.d(TAG, firstSimSettings.get("secretKey"));
                firstSimSettings.put("frequencyOfRequests", frequencyOfRequests.getText().toString());
                Log.d(TAG, firstSimSettings.get("frequencyOfRequests"));
                firstSimSettings.put("frequencyOfSmsSending", frequencyOfSmsSending.getText().toString());
                Log.d(TAG, firstSimSettings.get("frequencyOfSmsSending"));
                if(frequencyOfRequests.getText().toString().equals("")){
                    firstSimSettings.put("frequencyOfRequests", "60");
                }
                if(frequencyOfSmsSending.getText().toString().equals("")){
                    firstSimSettings.put("frequencyOfSmsSending", "60");
                }
                firstSimSettings.put("frequencyAlert", frequencyAlert.getText().toString());
                Log.d(TAG, firstSimSettings.get("frequencyAlert"));
                firstSimSettings.put("numbersAlerts", numbersAlerts.getText().toString());
                Log.d(TAG, firstSimSettings.get("numbersAlerts"));
                firstSimSettings.put("email", email.getText().toString());
                Log.d(TAG, firstSimSettings.get("email"));
                firstSimSettings.put("loginForEmail", loginForEmail.getText().toString());
                Log.d(TAG, firstSimSettings.get("loginForEmail"));
                firstSimSettings.put("passwordForEmail", passwordForEmail.getText().toString());
                Log.d(TAG, firstSimSettings.get("passwordForEmail"));
                firstSimSettings.put("portForEmail", portForEmail.getText().toString());
                Log.d(TAG, firstSimSettings.get("portForEmail"));
                firstSimSettings.put("maxNumberError", maxNumberError.getText().toString());
                Log.d(TAG, firstSimSettings.get("maxNumberError"));
                firstSimSettings.put("maxNumbersMessages", maxNumbersMessages.getText().toString());
                Log.d(TAG, firstSimSettings.get("maxNumbersMessages"));
                firstSimSettings.put("timeMessages", timeMessages.getText().toString());
                Log.d(TAG, firstSimSettings.get("timeMessages"));
                firstSimSettings.put("android_sim_slot", String.valueOf(getAndroidFirstSimSlotId()));
                Log.d(TAG, firstSimSettings.get("android_sim_slot"));

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
            return SubscriptionManager.from(this).getActiveSubscriptionInfoList().get(0).getSubscriptionId();
        }
        return SmsManager.getDefault().getSubscriptionId(); // TODO: 26.07.2017 doesn't work in api under 22
    }
}

