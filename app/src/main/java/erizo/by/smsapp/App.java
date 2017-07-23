package erizo.by.smsapp;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import erizo.by.smsapp.service.TinyDb;


public class App extends Application {
    public static Map<String, String> firstSimSettings;
    public static Map<String, String> secondSimSettings;
    private static final String SETTINGS_FIRST_SIM = "first_sim_settings";
    private static final String SETTINGS_SECOND_SIM = "second_sim_settings";
    private TinyDb tinyDb;
    private TinyDb tinyDbSecondSim;

    public void onCreate() {
        super.onCreate();
        tinyDb = new TinyDb(this);
        if (tinyDb.keyContaints(SETTINGS_FIRST_SIM)) {
            Log.d("App", "settings contains. set them to view");
            String serializedSettingsFirstSim = tinyDb.getString(SETTINGS_FIRST_SIM);
            Gson gson = new Gson();
            firstSimSettings = gson.fromJson(serializedSettingsFirstSim, new TypeToken<Map<String, String>>(){}.getType());
        } else {
            firstSimSettings = new HashMap<>();
            firstSimSettings.put("status", "false");
            firstSimSettings.put("url", "https://con24.ru/testapi/");
            firstSimSettings.put("secretKey", "T687G798UHO786");
        }
        tinyDbSecondSim = new TinyDb(this);
        if (tinyDbSecondSim.keyContaints(SETTINGS_SECOND_SIM)) {
            Log.d("App", "settings contains. set them to view");
            String serializedSettingsSecondSim = tinyDbSecondSim.getString(SETTINGS_SECOND_SIM);
            Gson gson = new Gson();
            secondSimSettings = gson.fromJson(serializedSettingsSecondSim, new TypeToken<Map<String, String>>(){}.getType());
        } else {
            secondSimSettings = new HashMap<>();
            secondSimSettings.put("status", "false");
            secondSimSettings.put("url", "https://con24.ru/testapi/");
            secondSimSettings.put("secretKey", "T687G798UHO786");
        }

    }
}
