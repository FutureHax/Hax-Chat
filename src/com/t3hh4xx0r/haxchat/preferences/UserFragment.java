package com.t3hh4xx0r.haxchat.preferences;

import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.t3hh4xx0r.haxchat.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UserFragment extends PreferenceFragment implements OnPreferenceChangeListener {
	    
    PreferenceScreen prefs;

	Preference userNamePref; 
	Preference passwordPref; 
	
	ParseUser u;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.user_frag);

        u = ParseUser.getCurrentUser();
        
        prefs = getPreferenceScreen();
        userNamePref = prefs.findPreference("username");
        userNamePref.setSummary(ParseUser.getCurrentUser().getUsername());
        userNamePref.setOnPreferenceChangeListener(this);

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
		if (key.equals("username")) {
			final ParseRelation device = u.getRelation("DeviceList");
			device.getQuery().whereEqualTo("DeviceID", PreferencesProvider.id(p.getContext())).findInBackground(
					new FindCallback() {						
						@Override
						public void done(List<ParseObject> r, ParseException e) {
							if (e == null && r.size() == 1) {
								ParseObject device = r.get(0);
								device.put("DeviceNick", v.toString());
								device.saveInBackground();
								u.refreshInBackground(null);
								userNamePref.setSummary(v.toString());
							}
						}
					});
		}
        return true;
    }
}