package com.t3hh4xx0r.haxchat.preferences;

import com.actionbarsherlock.view.MenuItem;
import com.larswerkman.colorpicker.ColorPicker;
import com.slidingmenu.lib.SlidingMenu;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.activities.BaseChatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class UIFragment extends BaseChatActivity implements OnClickListener {
	
	ColorPicker picker;

	public UIFragment() {
		super("Main");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.ui_prefs);
		setupMenu();
		picker = (ColorPicker) findViewById(R.id.picker);
		Button bc = (Button) findViewById(R.id.set_colour);
		bc.setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.i("V", Integer.toString(v.getId()));
		if (v.getId() == R.id.set_colour) {
			int c = picker.getColor();
			setColor(c);
			
			Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
	}

}
