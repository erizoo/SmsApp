package erizo.by.smsapp.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import erizo.by.smsapp.R;

public class AdditionalSettingsFirstSim extends AppCompatActivity {

    EditText frequencyAlert, numbersAlerts, email, loginForEmail,
                     passwordForEmail, portForEmail, maxNumberError, maxNumbersMessages,
                     timeMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_settings_first_sim_activity);

        frequencyAlert = (EditText) findViewById(R.id.frequency_alert_first_sim_edit);
        numbersAlerts = (EditText) findViewById(R.id.numbers_alerts_first_sim_edit);
        email = (EditText) findViewById(R.id.mail_first_sim_edit);
        loginForEmail = (EditText) findViewById(R.id.login_first_sim_edit);
        passwordForEmail = (EditText) findViewById(R.id.password_first_sim_edit);
        portForEmail = (EditText) findViewById(R.id.port_first_sim_edit);
        maxNumberError = (EditText) findViewById(R.id.max_number_errors_first_sim_edit);
        maxNumbersMessages = (EditText) findViewById(R.id.max_numbers_messages_first_sim_edit);
        timeMessages = (EditText) findViewById(R.id.time_messages_first_sim_edit);
    }
}
