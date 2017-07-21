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

public class SettingsSecondSim extends Activity {

    private static final String TAG = SettingsSecondSim.class.getSimpleName();
    private static final String SETTINGS = "second_sim_settings";
    private Switch aSwitch;
    private EditText simId, url, secretKey, frequencyOfRequests, frequencyOfSmsSending;
    private Button saveSettings;
    static Map<String, String> settingsSecondSims;

    private TinyDb tinyDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_second_sim_activity);
        tinyDb = new TinyDb(this);
        if (tinyDb.keyContaints(SETTINGS)) {
            Log.d(TAG, "settings contains. set them to view");
            String serializedSettings = tinyDb.getString(SETTINGS);
            Gson gson = new Gson();
            settingsSecondSims = gson.fromJson(serializedSettings, new TypeToken<Map<String, String>>(){}.getType());
        } else {
            settingsSecondSims = new HashMap<>();
        }
        aSwitch = (Switch) findViewById(R.id.switch_second_sim);
        simId = (EditText) findViewById(R.id.sim_second_id_edit);
        url = (EditText) findViewById(R.id.url_second_sim_edit);
        secretKey = (EditText) findViewById(R.id.secret_key_second_sim_edit);
        frequencyOfRequests = (EditText) findViewById(R.id.frequency_requests_second_sim_edit_text);
        frequencyOfSmsSending = (EditText) findViewById(R.id.frequency_sent_sms_second_sim_edit_text);
        saveSettings = (Button) findViewById(R.id.save_button_settings_second_sim);
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

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                Gson gson = new Gson();
                String serializedSettings = gson.toJson(settingsSecondSims);
                tinyDb.putString(SETTINGS, serializedSettings);
            }
        });
    }
}
