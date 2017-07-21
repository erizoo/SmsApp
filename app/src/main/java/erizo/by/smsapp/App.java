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
    private static final String SETTINGS = "first_sim_settings";
    private TinyDb tinyDb;

    public void onCreate() {
        super.onCreate();
        tinyDb = new TinyDb(this);
        if (tinyDb.keyContaints(SETTINGS)) {
            Log.d("App", "settings contains. set them to view");
            String serializedSettings = tinyDb.getString(SETTINGS);
            Gson gson = new Gson();
            settingsFirstSims = gson.fromJson(serializedSettings, new TypeToken<Map<String, String>>(){}.getType());
        } else {
            settingsFirstSims = new HashMap<>();
            settingsFirstSims.put("status", "false");
        }

    }

    public TinyDb getTinyDb() {
        return tinyDb;
    }
}
