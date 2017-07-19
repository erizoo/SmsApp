package erizo.by.smsapp.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

public class TinyDb {

    private SharedPreferences preferences;

    public TinyDb(Context appContext) {
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    /**
     * Get String value from SharedPreferences at 'key'. If key not found, return ""
     * @param key SharedPreferences key
     * @return String value at 'key' or "" (empty String) if key not found
     */
    public String getString(String key) {
        return preferences.getString(key, "");
    }

    /**
     * Put String value into SharedPreferences with 'key' and save
     * @param key SharedPreferences key
     * @param value String value to be added
     */
    public void putString(String key, String value) {
        checkForNullKey(key); checkForNullValue(value);
        preferences.edit().putString(key, value).apply();
    }

    /**
     * Remove SharedPreferences item with 'key'
     * @param key SharedPreferences key
     */
    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }

    /**
     * Clear SharedPreferences (remove everything)
     */
    public void clear() {
        preferences.edit().clear().apply();
    }

    /**
     * Retrieve all values from SharedPreferences. Do not modify collection return by method
     * @return a Map representing a list of key/value pairs from SharedPreferences
     */
    public Map<String, ?> getAll() {
        return preferences.getAll();
    }

    /**
     * Checking key in preference
     * @param key checkable
     * @return result
     */
    public boolean keyContaints(String key) {
        return preferences.contains(key);
    }

    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive measure
     * @param key
     */
    public void checkForNullKey(String key){
        if (key == null){
            throw new NullPointerException();
        }
    }
    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive measure
     * @param value
     */
    public void checkForNullValue(String value){
        if (value == null){
            throw new NullPointerException();
        }
    }
}