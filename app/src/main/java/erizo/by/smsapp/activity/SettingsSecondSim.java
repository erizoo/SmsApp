package erizo.by.smsapp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;

import erizo.by.smsapp.R;
import erizo.by.smsapp.service.InternalSimSlotIdCheckerService;
import erizo.by.smsapp.service.TinyDb;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static erizo.by.smsapp.App.firstSimSettings;
import static erizo.by.smsapp.App.secondSimSettings;

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

        deviceId.setText(secondSimSettings.get("deviceId"));
        simId.setText(secondSimSettings.get("simId"));
        url.setText(secondSimSettings.get("url"));
        secretKey.setText(secondSimSettings.get("secretKey"));
        frequencyOfRequests.setText(secondSimSettings.get("frequencyOfRequests"));
        frequencyOfSmsSending.setText(secondSimSettings.get("frequencyOfSmsSending"));
        frequencyAlert.setText(secondSimSettings.get("frequencyAlert"));
        numbersAlerts.setText(secondSimSettings.get("numbersAlerts"));
        email.setText(secondSimSettings.get("email"));
        loginForEmail.setText(secondSimSettings.get("loginForEmail"));
        passwordForEmail.setText(secondSimSettings.get("passwordForEmail"));
        portForEmail.setText(secondSimSettings.get("portForEmail"));
        maxNumberError.setText(secondSimSettings.get("maxNumberError"));
        maxNumbersMessages.setText(secondSimSettings.get("maxNumbersMessages"));
        timeMessages.setText(secondSimSettings.get("timeMessages"));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    secondSimSettings.put("status", "true");
                    Toast.makeText(getApplicationContext(), "SET ON", Toast.LENGTH_SHORT).show();
                } else {
                    secondSimSettings.put("status", "false");
                    Toast.makeText(getApplicationContext(), "SET OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            if (secondSimSettings.get("status").equals("false")){
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
                secondSimSettings.put("simSlot", SECOND_SIM_SLOT_NUMBER);
                Log.d(TAG, secondSimSettings.get("simSlot"));
                secondSimSettings.put("deviceId", deviceId.getText().toString());
                Log.d(TAG, secondSimSettings.get("deviceId"));
                secondSimSettings.put("simId", simId.getText().toString());
                Log.d(TAG, secondSimSettings.get("simId"));
                new Thread(new InternalSimSlotIdCheckerService(secondSimSettings.get("simSlot"), SettingsSecondSim.this)).start();
                secondSimSettings.put("url", url.getText().toString());
                Log.d(TAG, secondSimSettings.get("url"));
                secondSimSettings.put("secretKey", secretKey.getText().toString());
                Log.d(TAG, secondSimSettings.get("secretKey"));
                secondSimSettings.put("frequencyOfRequests", frequencyOfRequests.getText().toString());
                Log.d(TAG, secondSimSettings.get("frequencyOfRequests"));
                secondSimSettings.put("frequencyOfSmsSending", frequencyOfSmsSending.getText().toString());
                Log.d(TAG, secondSimSettings.get("frequencyOfSmsSending"));
                if(frequencyOfRequests.getText().toString().equals("")) {
                    secondSimSettings.put("frequencyOfRequests", "60");
                }
                if(frequencyOfSmsSending.getText().toString().equals("")) {
                    secondSimSettings.put("frequencyOfSmsSending", "60");
                }
                secondSimSettings.put("frequencyAlert", frequencyAlert.getText().toString());
                Log.d(TAG, secondSimSettings.get("frequencyAlert"));
                secondSimSettings.put("numbersAlerts", numbersAlerts.getText().toString());
                Log.d(TAG, secondSimSettings.get("numbersAlerts"));
                secondSimSettings.put("email", email.getText().toString());
                Log.d(TAG, secondSimSettings.get("email"));
                secondSimSettings.put("loginForEmail", loginForEmail.getText().toString());
                Log.d(TAG, secondSimSettings.get("loginForEmail"));
                secondSimSettings.put("passwordForEmail", passwordForEmail.getText().toString());
                Log.d(TAG, secondSimSettings.get("passwordForEmail"));
                secondSimSettings.put("portForEmail", portForEmail.getText().toString());
                Log.d(TAG, secondSimSettings.get("portForEmail"));
                secondSimSettings.put("maxNumberError", maxNumberError.getText().toString());
                Log.d(TAG, secondSimSettings.get("maxNumberError"));
                secondSimSettings.put("maxNumbersMessages", maxNumbersMessages.getText().toString());
                Log.d(TAG, secondSimSettings.get("maxNumbersMessages"));
                secondSimSettings.put("timeMessages", timeMessages.getText().toString());
                Log.d(TAG, secondSimSettings.get("timeMessages"));
                secondSimSettings.put("android_sim_slot", String.valueOf(getAndroidSecondSimSlotId()));
                Log.d(TAG, secondSimSettings.get("android_sim_slot"));

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
            return SubscriptionManager.from(this).getActiveSubscriptionInfoList().get(1).getSubscriptionId();
        }
        return Integer.valueOf(secondSimSettings.get("simSlot")) + 1;
    }
}
