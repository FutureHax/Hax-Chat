package com.t3hh4xx0r.haxchat.preferences;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.t3hh4xx0r.haxchat.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UserFragment extends PreferenceFragment implements OnPreferenceChangeListener {
	    
    PreferenceScreen prefs;

	Preference passwordPref; 
	
	ParseUser u;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.user_frag);

        u = ParseUser.getCurrentUser();
        
        prefs = getPreferenceScreen();
        
        passwordPref = prefs.findPreference("password");
        passwordPref.setEnabled(u.getBoolean("emailVerified"));
	}

	@Override
	public boolean onPreferenceTreeClick(final PreferenceScreen screen, Preference p) {
		String key = p.getKey();
		if (key.equals("password")) {
			ParseUser.requestPasswordResetInBackground(u.getEmail(), new RequestPasswordResetCallback() {
				@Override
				public void done(ParseException e) {
					if (e == null) {
						Toast.makeText(screen.getContext(), "Check your email for a reset message!", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getActivity(), "Unknown error, please try again.", Toast.LENGTH_LONG).show();						 
					}
				}
			});
		}
	    return false;
	}
	
	@Override
	public boolean onPreferenceChange(Preference p, final Object v) {
		String key = p.getKey();
		
        return true;
    }
}