package erizo.by.smsapp;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import erizo.by.smsapp.service.TinyDb;

/**
 * Created by Erizo on 21.07.2017.
 */

public class App extends Application {
    public static Map<String, String> settingsFirstSims;
    public static Map<String, String> settingsSecondSims;
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
            settingsFirstSims = gson.fromJson(serializedSettingsFirstSim, new TypeToken<Map<String, String>>(){}.getType());
        } else {
            settingsFirstSims = new HashMap<>();
            settingsFirstSims.put("status", "false");
        }
        tinyDbSecondSim = new TinyDb(this);
        if (tinyDbSecondSim.keyContaints(SETTINGS_SECOND_SIM)) {
            Log.d("App", "settings contains. set them to view");
            String serializedSettingsSecondSim = tinyDbSecondSim.getString(SETTINGS_SECOND_SIM);
            Gson gson = new Gson();
            settingsSecondSims = gson.fromJson(serializedSettingsSecondSim, new TypeToken<Map<String, String>>(){}.getType());
        } else {
            settingsSecondSims = new HashMap<>();
            settingsSecondSims.put("status", "false");
        }

    }

    public TinyDb getTinyDb() {
        return tinyDb;
    }

    public TinyDb getTinyDbSecondSim() {
        return tinyDbSecondSim;
    }
}
