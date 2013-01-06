package com.t3hh4xx0r.haxchat.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.parse.ParseHelper;

public class PushFragment extends SlidingFragmentActivity {
    public static final String ENABLE_TEST_PUSH = "com.t3hh4xx0r.haxchat.push_enable_test"; 
    public static final String ENABLE_UPDATES_PUSH = "com.t3hh4xx0r.haxchat.push_enable_updates"; 
    public static final String ENABLE_OTHER_PUSH = "com.t3hh4xx0r.haxchat.push_enable_other"; 
    SharedPreferences prefs;
    Editor editor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push_prefs);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		editor = prefs.edit();
		setupMenu();
		setCurrentValues();
		
		CheckBox otherPush = (CheckBox) findViewById(R.id.push_other_checkbox);
		otherPush.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton v, boolean b) {
				editor.putBoolean(ENABLE_OTHER_PUSH, b).apply();
				ParseHelper.registerForPush(v.getContext());
			}
		});
		CheckBox testPush = (CheckBox) findViewById(R.id.push_test_checkbox);
		testPush.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton v, boolean b) {
				editor.putBoolean(ENABLE_TEST_PUSH, b).apply();
				ParseHelper.registerForPush(v.getContext());
			}
		});
		CheckBox updatePush = (CheckBox) findViewById(R.id.push_updates_checkbox);
		updatePush.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton v, boolean b) {
				editor.putBoolean(ENABLE_UPDATES_PUSH, b).apply();
				ParseHelper.registerForPush(v.getContext());
			}
		});		
	}
	
	private void setCurrentValues() {
		CheckBox otherPush = (CheckBox) findViewById(R.id.push_other_checkbox);
		otherPush.setChecked(prefs.getBoolean(ENABLE_OTHER_PUSH, true));
		
		CheckBox testPush = (CheckBox) findViewById(R.id.push_test_checkbox);
		testPush.setChecked(prefs.getBoolean(ENABLE_TEST_PUSH, false));
		
		CheckBox updatePush = (CheckBox) findViewById(R.id.push_updates_checkbox);
		updatePush.setChecked(prefs.getBoolean(ENABLE_UPDATES_PUSH, true));		
	}
	
	@Override
	public void onResume(){
		super.onResume();
      	setCurrentValues();
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