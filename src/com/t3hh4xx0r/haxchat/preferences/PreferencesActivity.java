package com.t3hh4xx0r.haxchat.preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.t3hh4xx0r.haxchat.R;

public class PreferencesActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
				
		LinearLayout gen = (LinearLayout) findViewById(R.id.general_prefs);
		LinearLayout push = (LinearLayout) findViewById(R.id.push_prefs);
		LinearLayout alerts = (LinearLayout) findViewById(R.id.alert_prefs);
		LinearLayout ui = (LinearLayout) findViewById(R.id.ui_prefs);		
		gen.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), GeneralFragment.class);
				startActivity(i);
			}
		});
		ui.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), UIFragment.class);
				startActivity(i);
			}
		});
		alerts.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
			}
		});
		push.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), PushFragment.class);
				startActivity(i);
			}
		});
	}	
}
