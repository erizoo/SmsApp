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


public class SettingsFirstSim extends Activity {

    private static final String TAG = SettingsFirstSim.class.getSimpleName();
    private static final String SETTINGS_FIRST_SIM = "first_sim_settings";
    private Switch aSwitch;
    private EditText simId, url, secretKey, frequencyOfRequests, frequencyOfSmsSending;
    private Button saveSettings, nextSettings;
//    static Map<String, String> settingsFirstSims;
    private TinyDb tinyDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_first_sim_activity);
        tinyDb = new TinyDb(this);
        aSwitch = (Switch) findViewById(R.id.switch_first_sim);
        simId = (EditText) findViewById(R.id.sim_first_id_edit);
        url = (EditText) findViewById(R.id.url_first_sim_edit);
        secretKey = (EditText) findViewById(R.id.secret_key_first_sim_edit);
        frequencyOfRequests = (EditText) findViewById(R.id.frequency_requests_first_sim_edit_text);
        frequencyOfSmsSending = (EditText) findViewById(R.id.frequency_sent_sms_first_sim_edit_text);
        saveSettings = (Button) findViewById(R.id.save_button_settings_first_sim);
        nextSettings = (Button) findViewById(R.id.test_button);
        simId.setText(settingsFirstSims.get("simId"));
        url.setText(settingsFirstSims.get("url"));
        secretKey.setText(settingsFirstSims.get("secretKey"));
        frequencyOfRequests.setText(settingsFirstSims.get("frequencyOfRequests"));
        frequencyOfSmsSending.setText(settingsFirstSims.get("frequencyOfSmsSending"));
        saveSettings = (Button) findViewById(R.id.save_button_settings_first_sim);
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
        try {
            if (settingsFirstSims.get("status").equals("false")){
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
                Intent intent = new Intent(getBaseContext(), AdditionalSettingsFirstSim.class );
                startActivity(intent);
            }
        });

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsFirstSims.put("simId", simId.getText().toString());
                Log.d(TAG, settingsFirstSims.get("simId"));
                settingsFirstSims.put("url", url.getText().toString());
                Log.d(TAG, settingsFirstSims.get("url"));
                settingsFirstSims.put("secretKey", secretKey.getText().toString());
                Log.d(TAG, settingsFirstSims.get("secretKey"));
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
                Intent intent = new Intent(getBaseContext(), AdditionalSettingsFirstSim.class);
                startActivity(intent);
                Gson gson = new Gson();
                String serializedSettings = gson.toJson(settingsFirstSims);
                tinyDb.putString(SETTINGS_FIRST_SIM, serializedSettings);
            }
        });
    }
}
