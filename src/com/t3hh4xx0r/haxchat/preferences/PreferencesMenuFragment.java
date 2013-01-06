package com.t3hh4xx0r.haxchat.preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.t3hh4xx0r.haxchat.R;

public class PreferencesMenuFragment extends Fragment {
	View v;
	Activity act;
	
	public PreferencesMenuFragment(Activity a) {
		this.act = a;
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.preferences, container, false);
		LinearLayout gen = (LinearLayout) v.findViewById(R.id.general_prefs);
		LinearLayout push = (LinearLayout) v.findViewById(R.id.push_prefs);
		LinearLayout alerts = (LinearLayout) v.findViewById(R.id.alert_prefs);
		gen.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (!(act instanceof GeneralFragment)) {
					Intent i = new Intent(v.getContext(), GeneralFragment.class);
					startActivity(i);
					act.finish();
				} else {
					((GeneralFragment) act).toggle();
				}
			}
		});
		alerts.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
			}
		});
		push.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (!(act instanceof PushFragment)) {
					Intent i = new Intent(v.getContext(), PushFragment.class);
					startActivity(i);
					act.finish();
				} else {
					((PushFragment) act).toggle();
				}
			}
		});
		return v;
	}
}
