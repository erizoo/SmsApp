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
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import erizo.by.smsapp.R;
import erizo.by.smsapp.service.TinyDb;

public class SettingsFirstSim extends Activity {

    private static final String TAG = SettingsFirstSim.class.getSimpleName();
    private static final String SETTINGS = "first_sim_settings";
    private Switch aSwitch;
    private EditText simId, url, secretKey, frequencyOfRequests, frequencyOfSmsSending;
    private Button saveSettings;
    static Map<String, String> settingsFirstSims;
    private TinyDb tinyDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_first_sim_activity);
        tinyDb = new TinyDb(this);
        if (tinyDb.keyContaints(SETTINGS)) {
            Log.d(TAG, "settings contains. set them to view");
            String serializedSettings = tinyDb.getString(SETTINGS);
            Gson gson = new Gson();
            settingsFirstSims = gson.fromJson(serializedSettings, new TypeToken<Map<String, String>>(){}.getType());
        } else {
            settingsFirstSims = new HashMap<>();
        }
        aSwitch = (Switch) findViewById(R.id.switch_first_sim);
        simId = (EditText) findViewById(R.id.sim_first_id_edit);
        url = (EditText) findViewById(R.id.url_first_sim_edit);
        secretKey = (EditText) findViewById(R.id.secret_key_first_sim_edit);
        frequencyOfRequests = (EditText) findViewById(R.id.frequency_requests_first_sim_edit_text);
        frequencyOfSmsSending = (EditText) findViewById(R.id.frequency_sent_sms_first_sim_edit_text);
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

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Gson gson = new Gson();
                String serializedSettings = gson.toJson(settingsFirstSims);
                tinyDb.putString(SETTINGS, serializedSettings);
            }
        });
    }
}
