package com.t3hh4xx0r.haxchat.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.parse.ParseHelper;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PushFragment extends PreferenceFragment {
	
	    SharedPreferences sharedPrefs;
	    SharedPreferences.Editor editor;
	    PreferenceScreen prefs;

	    public static final String ENABLE_TEST_PUSH = "com.t3hh4xx0r.haxchat.push_enable_test"; 
	    public static final String ENABLE_UPDATES_PUSH = "com.t3hh4xx0r.haxchat.push_enable_updates"; 
	    public static final String ENABLE_OTHER_PUSH = "com.t3hh4xx0r.haxchat.push_enable_other"; 

	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.push_frag);
	        
	        sharedPrefs = this.getActivity().getSharedPreferences(PreferencesProvider.PREFERENCES_KEY, Context.MODE_PRIVATE);
	        prefs = getPreferenceScreen();
	        editor = sharedPrefs.edit();
	      	editor.putBoolean(PreferencesProvider.PREFERENCES_CHANGED, true);
	      	editor.commit();	        
	      		      	
	      	setCurrentValues();
	   }

	private void setCurrentValues() {
		CheckBoxPreference enableOther = (CheckBoxPreference) prefs.findPreference(ENABLE_OTHER_PUSH);
		enableOther.setChecked(sharedPrefs.getBoolean(ENABLE_OTHER_PUSH, true));
		
		CheckBoxPreference enableTest = (CheckBoxPreference) prefs.findPreference(ENABLE_TEST_PUSH);
		enableTest.setChecked(sharedPrefs.getBoolean(ENABLE_TEST_PUSH, false));
		
		CheckBoxPreference enableUpdates = (CheckBoxPreference) prefs.findPreference(ENABLE_UPDATES_PUSH);
		enableUpdates.setChecked(sharedPrefs.getBoolean(ENABLE_UPDATES_PUSH, true));		
	}

	public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
		String key = preference.getKey();
		if (key.equals(ENABLE_OTHER_PUSH) ||
				key.equals(ENABLE_TEST_PUSH) ||
				key.equals(ENABLE_UPDATES_PUSH)) {
			boolean value = ((CheckBoxPreference)preference).isChecked();
			editor.putBoolean(key, value).apply();
			ParseHelper.registerForPush(this.getActivity());
		} 
		return false;
	}
		
	@Override
	public void onResume(){
		super.onResume();
      	setCurrentValues();
	}
}
