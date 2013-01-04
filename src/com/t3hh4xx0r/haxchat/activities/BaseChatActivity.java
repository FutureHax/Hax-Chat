package com.t3hh4xx0r.haxchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.t3hh4xx0r.haxchat.ChatsListFragment;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.fragments.UserListFragment;
import com.t3hh4xx0r.haxchat.parse.ParseHelper;
import com.t3hh4xx0r.haxchat.preferences.Preferences;

public class BaseChatActivity extends SlidingFragmentActivity implements
		UserListFragment.Callbacks {

	private String mTitleRes;
	protected Fragment mFrag;

	public BaseChatActivity(String titleRes) {
		mTitleRes = titleRes;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(mTitleRes);
		SlidingMenu sm = getSlidingMenu();
		setBehindContentView(R.layout.chats_list_menu);
		sm.setSecondaryMenu(R.layout.user_list_menu);
		sm.setSecondaryShadowDrawable(R.drawable.shadowright);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setMode(SlidingMenu.LEFT_RIGHT);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame_right, new ChatsListFragment())
		.commit();
		
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame_left, new UserListFragment())
		.commit();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_chat_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_sign_out:
				ParseHelper.doLogoutSequence(this);	
				break;
				
			case R.id.menu_settings:
				Intent settingsIntent = new Intent(this, Preferences.class);
				startActivity(settingsIntent);
				break;
				
			case R.id.menu_users:
				getSlidingMenu().showSecondaryMenu();
				break;
				
			case android.R.id.home: 
				toggle();
				break;
				
		}
		return true;		
	}

	@Override
	public void onChildItemSelected(String id) {
	}

}
