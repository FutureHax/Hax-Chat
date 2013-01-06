package com.t3hh4xx0r.haxchat.preferences;

import java.util.UUID;

import com.parse.ParseUser;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public final class PreferencesProvider {
    public static final String PREFERENCES_KEY = "com.t3hh4xx0r.haxchat_preferences";
    public static final String PREFERENCES_CHANGED = "preferences_changed";

    public static final String ENABLE_PUSH_OTHER = "com.t3hh4xx0r.haxchat.push_enable_other"; 
    public static final String ENABLE_TEST_PUSH = "com.t3hh4xx0r.haxchat.push_enable_test"; 
    public static final String ENABLE_UPDATES_PUSH = "com.t3hh4xx0r.haxchat.push_enable_updates"; 

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    
    public static class Push {
      	public static boolean[] getPushChannels(Context context) {
      		boolean values[] = new boolean[3];
    		final SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
    		values[0] = preferences.getBoolean(ENABLE_PUSH_OTHER, true);    		
    		values[1] = preferences.getBoolean(ENABLE_TEST_PUSH, true); 
    		values[2] = preferences.getBoolean(ENABLE_UPDATES_PUSH, true); 		    		
    		return values;
    	}
    }
    
    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }
    
    public static String deviceNick(Context c) {
    	return PreferenceManager.getDefaultSharedPreferences(c).getString("deviceNick", ParseUser.getCurrentUser().getUsername());
    }
    
    public static void setNick(Context c, String nick) {
    	PreferenceManager.getDefaultSharedPreferences(c).edit().putString("deviceNick", nick).apply();
    }
}
