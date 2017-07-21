package erizo.by.smsapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import erizo.by.smsapp.R;
import erizo.by.smsapp.service.TinyDb;

import static erizo.by.smsapp.App.settingsFirstSims;

public class AdditionalSettingsFirstSim extends AppCompatActivity {

    private static final String TAG = SettingsFirstSim.class.getSimpleName();
    private static final String SETTINGS_FIRST_SIM = "first_sim_settings";
    private TinyDb tinyDb;

    EditText frequencyAlert, numbersAlerts, email, loginForEmail,
                     passwordForEmail, portForEmail, maxNumberError, maxNumbersMessages,
                     timeMessages;
    Button saveTestSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_settings_first_sim_activity);
        tinyDb = new TinyDb(this);
        frequencyAlert = (EditText) findViewById(R.id.frequency_alert_first_sim_edit);
        numbersAlerts = (EditText) findViewById(R.id.numbers_alerts_first_sim_edit);
        email = (EditText) findViewById(R.id.mail_first_sim_edit);
        loginForEmail = (EditText) findViewById(R.id.login_first_sim_edit);
        passwordForEmail = (EditText) findViewById(R.id.password_first_sim_edit);
        portForEmail = (EditText) findViewById(R.id.port_sim_first_edit);
        maxNumberError = (EditText) findViewById(R.id.max_number_errors_first_sim_edit);
        maxNumbersMessages = (EditText) findViewById(R.id.max_numbers_messages_first_sim_edit);
        timeMessages = (EditText) findViewById(R.id.time_messages_first_sim_edit);
        saveTestSettings = (Button) findViewById(R.id.button_save_test_settings_first_sim);

        frequencyAlert.setText(settingsFirstSims.get("frequencyAlert"));
        numbersAlerts.setText(settingsFirstSims.get("numbersAlerts"));
        email.setText(settingsFirstSims.get("email"));
        loginForEmail.setText(settingsFirstSims.get("loginForEmail"));
        passwordForEmail.setText(settingsFirstSims.get("passwordForEmail"));
        portForEmail.setText(settingsFirstSims.get("portForEmail"));
        maxNumberError.setText(settingsFirstSims.get("maxNumberError"));
        maxNumbersMessages.setText(settingsFirstSims.get("maxNumbersMessages"));
        timeMessages.setText(settingsFirstSims.get("timeMessages"));

        saveTestSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsFirstSims.put("frequencyAlert", frequencyAlert.getText().toString());
                Log.d(TAG, settingsFirstSims.get("frequencyAlert"));
                settingsFirstSims.put("numbersAlerts", numbersAlerts.getText().toString());
                Log.d(TAG, settingsFirstSims.get("numbersAlerts"));
                settingsFirstSims.put("email", email.getText().toString());
                Log.d(TAG, settingsFirstSims.get("email"));
                settingsFirstSims.put("loginForEmail", loginForEmail.getText().toString());
                Log.d(TAG, settingsFirstSims.get("loginForEmail"));
                settingsFirstSims.put("passwordForEmail", passwordForEmail.getText().toString());
                Log.d(TAG, settingsFirstSims.get("passwordForEmail"));
                settingsFirstSims.put("portForEmail", portForEmail.getText().toString());
                Log.d(TAG, settingsFirstSims.get("portForEmail"));
                settingsFirstSims.put("maxNumberError", maxNumberError.getText().toString());
                Log.d(TAG, settingsFirstSims.get("maxNumberError"));
                settingsFirstSims.put("maxNumbersMessages", maxNumbersMessages.getText().toString());
                Log.d(TAG, settingsFirstSims.get("maxNumbersMessages"));
                settingsFirstSims.put("timeMessages", timeMessages.getText().toString());
                Log.d(TAG, settingsFirstSims.get("timeMessages"));
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                Gson gson = new Gson();
                String serializedSettings = gson.toJson(settingsFirstSims);
                tinyDb.putString(SETTINGS_FIRST_SIM, serializedSettings);
            }
        });
    }
}
