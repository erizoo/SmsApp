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

import static erizo.by.smsapp.App.firstSimSettings;


public class SettingsFirstSim extends Activity {

    private static final String TAG = SettingsFirstSim.class.getSimpleName();
    private static final String FIRST_SIM_SLOT_NUMBER = "0";
    private static final String SETTINGS_FIRST_SIM = "first_sim_settings";
    private Switch aSwitch;
    private EditText deviceId, simId, url, secretKey, frequencyOfRequests, frequencyOfSmsSending;
    private Button saveSettings, nextSettings;
//    static Map<String, String> firstSimSettings;
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
        saveSettings = (Button) findViewById(R.id.save_button_settings_first_sim);
        nextSettings = (Button) findViewById(R.id.test_button);
        deviceId.setText(firstSimSettings.get("deviceId"));
        simId.setText(firstSimSettings.get("simId"));
        url.setText(firstSimSettings.get("url"));
        secretKey.setText(firstSimSettings.get("secretKey"));
        frequencyOfRequests.setText(firstSimSettings.get("frequencyOfRequests"));
        frequencyOfSmsSending.setText(firstSimSettings.get("frequencyOfSmsSending"));
        saveSettings = (Button) findViewById(R.id.save_button_settings_first_sim);
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
                Intent intent = new Intent(getBaseContext(), AdditionalSettingsFirstSim.class);
                startActivity(intent);
                Gson gson = new Gson();
                String serializedSettings = gson.toJson(firstSimSettings);
                tinyDb.putString(SETTINGS_FIRST_SIM, serializedSettings);
            }
        });
    }
}
