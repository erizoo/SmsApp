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
import static erizo.by.smsapp.App.secondSimSettings;

public class SettingsSecondSim extends Activity {

    private static final String TAG = SettingsSecondSim.class.getSimpleName();
    private static final String SECOND_SIM_SLOT_NUMBER = "1";
    private static final String SETTINGS_SECOND_SIM = "second_sim_settings";
    private Switch aSwitch;
    private EditText deviceId,simId, url, secretKey, frequencyOfRequests, frequencyOfSmsSending;
    private Button saveSettings, nextSettings;

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
        saveSettings = (Button) findViewById(R.id.save_button_settings_second_sim);
        nextSettings = (Button) findViewById(R.id.test_button_second_sim);

        deviceId.setText(secondSimSettings.get("deviceId"));
        simId.setText(secondSimSettings.get("simId"));
        url.setText(secondSimSettings.get("url"));
        secretKey.setText(secondSimSettings.get("secretKey"));
        frequencyOfRequests.setText(secondSimSettings.get("frequencyOfRequests"));
        frequencyOfSmsSending.setText(secondSimSettings.get("frequencyOfSmsSending"));
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
                secondSimSettings.put("simSlot", SECOND_SIM_SLOT_NUMBER);
                Log.d(TAG, firstSimSettings.get("simSlot"));
                secondSimSettings.put("deviceId", deviceId.getText().toString());
                Log.d(TAG, secondSimSettings.get("deviceId"));
                secondSimSettings.put("simId", simId.getText().toString());
                Log.d(TAG, secondSimSettings.get("simId"));
                secondSimSettings.put("url", url.getText().toString());
                Log.d(TAG, secondSimSettings.get("url"));
                secondSimSettings.put("secretKey", secretKey.getText().toString());
                Log.d(TAG, secondSimSettings.get("secretKey"));
                secondSimSettings.put("frequencyOfRequests", frequencyOfRequests.getText().toString());
                Log.d(TAG, secondSimSettings.get("frequencyOfRequests"));
                secondSimSettings.put("frequencyOfSmsSending", frequencyOfSmsSending.getText().toString());
                Log.d(TAG, secondSimSettings.get("frequencyOfSmsSending"));
                if(frequencyOfRequests.getText().toString().equals("")){
                    secondSimSettings.put("frequencyOfRequests", "60");
                }
                if(frequencyOfSmsSending.getText().toString().equals("")){
                    secondSimSettings.put("frequencyOfSmsSending", "60");
                }
                Intent intent = new Intent(getBaseContext(), AdditionalSettingsSecondSim.class);
                startActivity(intent);
                Gson gson = new Gson();
                String serializedSettings = gson.toJson(secondSimSettings);
                tinyDbSecondSim.putString(SETTINGS_SECOND_SIM, serializedSettings);
            }
        });
    }
}
