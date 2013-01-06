package com.t3hh4xx0r.haxchat.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.t3hh4xx0r.haxchat.R;

public class GeneralFragment extends SlidingFragmentActivity {
    SharedPreferences prefs;
    Editor editor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.general_prefs);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		editor = prefs.edit();
		
		setupMenu();
		
		LinearLayout reset = (LinearLayout) findViewById(R.id.reset);
		TextView t = (TextView) findViewById(R.id.title);
        final ParseUser u = ParseUser.getCurrentUser();        

        if (!u.getBoolean("emailVerified")) t.setTextColor(getResources().getColor(android.R.color.darker_gray));
        reset.setEnabled(u.getBoolean("emailVerified"));
        
        reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				ParseUser.requestPasswordResetInBackground(u.getEmail(), new RequestPasswordResetCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {
							Toast.makeText(v.getContext(), "Check your email for a reset message!", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(v.getContext(), "Unknown error, please try again.", Toast.LENGTH_LONG).show();						 
						}
					}
				});
			}
		});
	}

	void setupMenu() {
		SlidingMenu sm = getSlidingMenu();
		setBehindContentView(R.layout.chats_list_menu);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setMode(SlidingMenu.LEFT);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame_right, new PreferencesMenuFragment(this))
		.commit();			
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home: 
				toggle();
				break;
		}
		return true;		
	}
}