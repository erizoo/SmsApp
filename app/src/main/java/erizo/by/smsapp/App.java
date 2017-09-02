package erizo.by.smsapp;

import android.app.Application;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import erizo.by.smsapp.service.TinyDb;

import static erizo.by.smsapp.SimSettings.SECRET_KEY;
import static erizo.by.smsapp.SimSettings.SIM_IDENTIFIER;
import static erizo.by.smsapp.SimSettings.STATUS;
import static erizo.by.smsapp.SimSettings.URL;


public class App extends Application {

    public static Map<String, String> firstSimSettings;
    public static Map<String, String> secondSimSettings;
    private static final String SETTINGS_FIRST_SIM = "first_sim_settings";
    private static final String SETTINGS_SECOND_SIM = "second_sim_settings";
    private TinyDb tinyDbFirstSim;
    private TinyDb tinyDbSecondSim;

    public void onCreate() {
        super.onCreate();
        tinyDbFirstSim = new TinyDb(this);
        if (tinyDbFirstSim.keyContaints(SETTINGS_FIRST_SIM)) {
            Log.d("App", "settings contains. set them to view");
            String serializedSettingsFirstSim = tinyDbFirstSim.getString(SETTINGS_FIRST_SIM);
            Gson gson = new Gson();
            firstSimSettings = gson.fromJson(serializedSettingsFirstSim, new TypeToken<Map<String, String>>() {
            }.getType());
        } else {
            setDefaultSettings(firstSimSettings);
        }
        tinyDbSecondSim = new TinyDb(this);
        if (tinyDbSecondSim.keyContaints(SETTINGS_SECOND_SIM)) {
            Log.d("App", "settings contains. set them to view");
            String serializedSettingsSecondSim = tinyDbSecondSim.getString(SETTINGS_SECOND_SIM);
            Gson gson = new Gson();
            secondSimSettings = gson.fromJson(serializedSettingsSecondSim, new TypeToken<Map<String, String>>() {
            }.getType());
        } else {
            setDefaultSettings(secondSimSettings);
        }
        List<SubscriptionInfo> infoList = SubscriptionManager.from(this).getActiveSubscriptionInfoList();
        if (firstSimSettings.containsKey(SIM_IDENTIFIER)) {
            if (!infoList.get(0).getIccId().equals(firstSimSettings.get(SIM_IDENTIFIER))) {
                firstSimSettings.remove(SIM_IDENTIFIER);
            }
        }
        if (secondSimSettings.containsKey(SIM_IDENTIFIER)) {
            if (!infoList.get(1).getIccId().equals(secondSimSettings.get(SIM_IDENTIFIER))) {
                secondSimSettings.remove(SIM_IDENTIFIER);
            }
        }
     }

    private void setDefaultSettings(Map<String, String> settings) {
        settings = new HashMap<>();
        settings.put(STATUS, "false");
        settings.put(URL, "https://con24.ru/testapi/");
        settings.put(SECRET_KEY, "T687G798UHO786");
    }

}
