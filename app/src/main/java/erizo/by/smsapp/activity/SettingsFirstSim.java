package erizo.by.smsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.HashMap;

import erizo.by.smsapp.R;

public class SettingsFirstSim extends AppCompatActivity {

    private static final String TAG = SettingsFirstSim.class.getSimpleName();
    private Switch aSwitch;
    private EditText simId, url, secretKey,frequencyOfRequests,frequencyOfSmsSending;
    private Button saveSettings;
    static HashMap<String, String> settingsFirstSims = new HashMap<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_first_sim_activity);
        aSwitch = (Switch) findViewById(R.id.switch_first_sim);
//        simId = (EditText) findViewById(R.id.id_sim_first_edit_text);
//        url = (EditText) findViewById(R.id.url_sim_first_edit_text);
//        secretKey = (EditText) findViewById(R.id.secret_key_first_sim_edit_text);
        frequencyOfRequests = (EditText) findViewById(R.id.frequency_requests_first_sim_edit_text);
        frequencyOfSmsSending = (EditText) findViewById(R.id.frequency_sent_sms_first_sim_edit_text);
        saveSettings = (Button) findViewById(R.id.save_button_settings_first_sim);
        aSwitch.setChecked(false);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settingsFirstSims.put("status", "true");
                    Toast.makeText(getApplicationContext(), "SET ON", Toast.LENGTH_SHORT).show();
                } else {
                    settingsFirstSims.put("status", "false");
                    Toast.makeText(getApplicationContext(), "SET OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                settingsFirstSims.put("simId", String.valueOf(simId.getText()));
//                Log.d(TAG, settingsFirstSims.get("simId"));
//                settingsFirstSims.put("url", url.getText().toString());
//                Log.d(TAG, settingsFirstSims.get("url"));
//                settingsFirstSims.put("secretKey", secretKey.getText().toString());
//                Log.d(TAG, settingsFirstSims.get("secretKey"));
                settingsFirstSims.put("frequencyOfRequests", frequencyOfRequests.getText().toString());
                Log.d(TAG, settingsFirstSims.get("frequencyOfRequests"));
                settingsFirstSims.put("frequencyOfSmsSending", frequencyOfSmsSending.getText().toString());
                Log.d(TAG, settingsFirstSims.get("frequencyOfSmsSending"));
                if(frequencyOfRequests.getText().toString().equals("")){
                    settingsFirstSims.put("frequencyOfRequests", "60");
                }
                if(frequencyOfSmsSending.getText().toString().equals("")){
                    settingsFirstSims.put("frequencyOfSmsSending", "60");
                }
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
