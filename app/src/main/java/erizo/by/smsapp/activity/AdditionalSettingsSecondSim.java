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
import static erizo.by.smsapp.App.settingsSecondSims;

public class AdditionalSettingsSecondSim extends AppCompatActivity {

    private static final String TAG = SettingsFirstSim.class.getSimpleName();
    private static final String SETTINGS_SECOND_SIM = "second_sim_settings";
    private TinyDb tinyDbSecondSim;

    EditText frequencyAlert, numbersAlerts, email, loginForEmail,
            passwordForEmail, portForEmail, maxNumberError, maxNumbersMessages,
            timeMessages;
    Button saveTestSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_settings_second_sim_activity);
        tinyDbSecondSim = new TinyDb(this);
        frequencyAlert = (EditText) findViewById(R.id.frequency_alert_second_sim_edit);
        numbersAlerts = (EditText) findViewById(R.id.numbers_alerts_second_sim_edit);
        email = (EditText) findViewById(R.id.mail_second_sim_edit);
        loginForEmail = (EditText) findViewById(R.id.login_second_sim_edit);
        passwordForEmail = (EditText) findViewById(R.id.password_second_sim_edit);
        portForEmail = (EditText) findViewById(R.id.port_sim_second_edit);
        maxNumberError = (EditText) findViewById(R.id.max_number_errors_second_sim_edit);
        maxNumbersMessages = (EditText) findViewById(R.id.max_numbers_messages_second_sim_edit);
        timeMessages = (EditText) findViewById(R.id.time_messages_second_sim_edit);
        saveTestSettings = (Button) findViewById(R.id.button_save_test_settings_second_sim);

        frequencyAlert.setText(settingsSecondSims.get("frequencyAlert"));
        numbersAlerts.setText(settingsSecondSims.get("numbersAlerts"));
        email.setText(settingsSecondSims.get("email"));
        loginForEmail.setText(settingsSecondSims.get("loginForEmail"));
        passwordForEmail.setText(settingsSecondSims.get("passwordForEmail"));
        portForEmail.setText(settingsSecondSims.get("portForEmail"));
        maxNumberError.setText(settingsSecondSims.get("maxNumberError"));
        maxNumbersMessages.setText(settingsSecondSims.get("maxNumbersMessages"));
        timeMessages.setText(settingsSecondSims.get("timeMessages"));

        saveTestSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsSecondSims.put("frequencyAlert", frequencyAlert.getText().toString());
                Log.d(TAG, settingsSecondSims.get("frequencyAlert"));
                settingsSecondSims.put("numbersAlerts", numbersAlerts.getText().toString());
                Log.d(TAG, settingsSecondSims.get("numbersAlerts"));
                settingsSecondSims.put("email", email.getText().toString());
                Log.d(TAG, settingsSecondSims.get("email"));
                settingsSecondSims.put("loginForEmail", loginForEmail.getText().toString());
                Log.d(TAG, settingsSecondSims.get("loginForEmail"));
                settingsSecondSims.put("passwordForEmail", passwordForEmail.getText().toString());
                Log.d(TAG, settingsSecondSims.get("passwordForEmail"));
                settingsSecondSims.put("portForEmail", portForEmail.getText().toString());
                Log.d(TAG, settingsSecondSims.get("portForEmail"));
                settingsSecondSims.put("maxNumberError", maxNumberError.getText().toString());
                Log.d(TAG, settingsSecondSims.get("maxNumberError"));
                settingsSecondSims.put("maxNumbersMessages", maxNumbersMessages.getText().toString());
                Log.d(TAG, settingsSecondSims.get("maxNumbersMessages"));
                settingsSecondSims.put("timeMessages", timeMessages.getText().toString());
                Log.d(TAG, settingsSecondSims.get("timeMessages"));
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                Gson gson = new Gson();
                String serializedSettings = gson.toJson(settingsSecondSims);
                tinyDbSecondSim.putString(SETTINGS_SECOND_SIM, serializedSettings);
            }
        });
    }
}
