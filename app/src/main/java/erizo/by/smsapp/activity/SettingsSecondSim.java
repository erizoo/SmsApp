package erizo.by.smsapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;

import erizo.by.smsapp.R;
import erizo.by.smsapp.service.TinyDb;

import static erizo.by.smsapp.App.settingsFirstSims;
import static erizo.by.smsapp.App.settingsSecondSims;

public class SettingsSecondSim extends Activity {

    private static final String TAG = SettingsSecondSim.class.getSimpleName();
    private static final String SETTINGS_SECOND_SIM = "second_sim_settings";
    private Switch aSwitch;
    private EditText simId, url, secretKey, frequencyOfRequests, frequencyOfSmsSending;
    private Button saveSettings, nextSettings;

    private TinyDb tinyDbSecondSim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_second_sim_activity);
        tinyDbSecondSim = new TinyDb(this);
        aSwitch = (Switch) findViewById(R.id.switch_second_sim);
        simId = (EditText) findViewById(R.id.sim_second_id_edit);
        url = (EditText) findViewById(R.id.url_second_sim_edit);
        secretKey = (EditText) findViewById(R.id.secret_key_second_sim_edit);
        frequencyOfRequests = (EditText) findViewById(R.id.frequency_requests_second_sim_edit_text);
        frequencyOfSmsSending = (EditText) findViewById(R.id.frequency_sent_sms_second_sim_edit_text);
        saveSettings = (Button) findViewById(R.id.save_button_settings_second_sim);
        nextSettings = (Button) findViewById(R.id.test_button_second_sim);

        simId.setText(settingsSecondSims.get("simId"));
        url.setText(settingsSecondSims.get("url"));
        secretKey.setText(settingsSecondSims.get("secretKey"));
        frequencyOfRequests.setText(settingsSecondSims.get("frequencyOfRequests"));
        frequencyOfSmsSending.setText(settingsSecondSims.get("frequencyOfSmsSending"));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settingsSecondSims.put("status", "true");
                    Toast.makeText(getApplicationContext(), "SET ON", Toast.LENGTH_SHORT).show();
                } else {
                    settingsSecondSims.put("status", "false");
                    Toast.makeText(getApplicationContext(), "SET OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try {
            if (settingsSecondSims.get("status").equals("false")){
                aSwitch.setChecked(false);
            } else {
                aSwitch.setChecked(true);
            }
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }

        nextSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AdditionalSettingsSecondSim.class);
                startActivity(intent);
            }
        });

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsSecondSims.put("simId", simId.getText().toString());
                Log.d(TAG, settingsSecondSims.get("simId"));
                settingsSecondSims.put("url", url.getText().toString());
                Log.d(TAG, settingsSecondSims.get("url"));
                settingsSecondSims.put("secretKey", secretKey.getText().toString());
                Log.d(TAG, settingsSecondSims.get("secretKey"));
                settingsSecondSims.put("frequencyOfRequests", frequencyOfRequests.getText().toString());
                Log.d(TAG, settingsSecondSims.get("frequencyOfRequests"));
                settingsSecondSims.put("frequencyOfSmsSending", frequencyOfSmsSending.getText().toString());
                Log.d(TAG, settingsSecondSims.get("frequencyOfSmsSending"));
                if(frequencyOfRequests.getText().toString().equals("")){
                    settingsSecondSims.put("frequencyOfRequests", "60");
                }
                if(frequencyOfSmsSending.getText().toString().equals("")){
                    settingsSecondSims.put("frequencyOfSmsSending", "60");
                }
                Intent intent = new Intent(getBaseContext(), AdditionalSettingsSecondSim.class);
                startActivity(intent);
                Gson gson = new Gson();
                String serializedSettings = gson.toJson(settingsSecondSims);
                tinyDbSecondSim.putString(SETTINGS_SECOND_SIM, serializedSettings);
            }
        });
    }
}
