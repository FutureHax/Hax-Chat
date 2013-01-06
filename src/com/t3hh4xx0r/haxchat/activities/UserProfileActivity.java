package com.t3hh4xx0r.haxchat.activities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.t3hh4xx0r.haxchat.FileCache;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.fragments.UserListFragment;
import com.t3hh4xx0r.haxchat.parse.ParseHelper;

public class UserProfileActivity extends SlidingFragmentActivity implements UserListFragment.Callbacks {
	private ParseUser detailedUser;
	Button pmButton;
	Button friendButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_detail_activity);

		pmButton = (Button) findViewById(R.id.pm_button);
		pmButton.setVisibility(View.GONE);
		pmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				   final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
				    final EditText input = new EditText(v.getContext());
				    alert.setView(input);
				    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				            String value = input.getText().toString().trim();
				            ParseHelper.sendPrivateMessage(value, Long.toString(System.currentTimeMillis()), detailedUser.getUsername(), input.getContext());
				        }
				    });

				    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				            dialog.cancel();
				        }
				    });
				    alert.show();               
			}
		});
		
		friendButton = (Button) findViewById(R.id.add_friend);
		friendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				ParseHelper.addUserFriend(detailedUser, v.getContext());
				friendButton.setVisibility(View.GONE);
			}
		});
		
		setupMenu();
		userCheck();
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
		.replace(R.id.menu_frame_right, new UserListFragment())
		.commit();			
	}

	private void userCheck() {
		if (getIntent().getExtras().containsKey("id")) {		
			if (getIntent().getExtras().getString("id").contains("@")) {
				ParseHelper.getUserByEmail(getIntent().getExtras().getString("id"), new FindCallback() {
					@Override
					public void done(List<ParseObject> res, ParseException e) {
						if (e == null) {
							detailedUser = (ParseUser) res.get(0);
							if (detailedUser.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
								Bundle arguments = new Bundle();
								arguments.putString("id", getIntent().getExtras().getString("id"));
								Intent i = new Intent(getApplicationContext(), EditableUserProfileActivity.class);
								i.putExtras(arguments);
								startActivity(i);
								finish();
							} else {
								updateUI();
							}
						} else {
							e.printStackTrace();
						}
					}				
				});
			} else {
				ParseHelper.getUserByUser(getIntent().getExtras().getString("id"), new FindCallback() {
					@Override
					public void done(List<ParseObject> res, ParseException e) {
						if (e == null) {
							detailedUser = (ParseUser) res.get(0);
							if (detailedUser.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
								Bundle arguments = new Bundle();
								arguments.putString("id", getIntent().getExtras().getString("id"));
								Intent i = new Intent(getApplicationContext(), EditableUserProfileActivity.class);
								i.putExtras(arguments);
								startActivity(i);
								finish();
							} else {
								updateUI();
							}
						} else {
							e.printStackTrace();
						}
					}				
				});
			}
		}
	}

	public void updateUI() {
		friendButton.setVisibility(ParseHelper.isUserAFriend(detailedUser, this) ? View.GONE : View.VISIBLE);
		RelativeLayout mask = (RelativeLayout) findViewById(R.id.loading_mask);
		mask.setVisibility(View.GONE);
		((TextView) findViewById(R.id.user_name)).setText(detailedUser.getUsername());
		try {
			String dateString = detailedUser.get("lastActive").toString();
			String lastActive = "Last Active : " + getPeriodizedTime(dateString);
			((TextView) findViewById(R.id.last_active)).setText(lastActive);
		} catch (Exception e) {
			e.printStackTrace();
			((TextView) findViewById(R.id.last_active)).setText("Last Active : Never");
		}
		
		final ImageView avatar = (ImageView) findViewById(R.id.avatar);
		final FileCache fCache = new FileCache(getApplicationContext());
		Bitmap b = fCache.getBitmap(detailedUser.getUsername(), false);
		if (b != null) {
			avatar.setImageBitmap(b);
		} else {
			ParseFile avatarBitmap = (ParseFile) detailedUser.get("Avatar");
			if (avatarBitmap != null) {
				avatarBitmap.getDataInBackground(new GetDataCallback() {
					public void done(byte[] data, ParseException e) {
						if (e == null) {
							InputStream is = new ByteArrayInputStream(data);
							Bitmap bmp = BitmapFactory.decodeStream(is);
							fCache.putBitmap(bmp, detailedUser.getUsername(), false);
							avatar.setImageBitmap(bmp);
					    } else {
					      e.printStackTrace();
					    }
					  }
				});
			}
		}
	}

	private String getPeriodizedTime(String dateString) throws java.text.ParseException {
		DateFormat df = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
		Date lastActive = df.parse(dateString);
		LocalDateTime start = new LocalDateTime(lastActive);
		LocalDateTime now = new LocalDateTime(System.currentTimeMillis());
		Period p1 = Period.fieldDifference(start, now);

		PeriodFormatter pF = new PeriodFormatterBuilder()
		    .appendDays()
		    .appendSuffix(" day", " days")
		    .appendSeparator(" and ")
		    .appendHours()
			.appendSuffix(" hour", " hours")
		    .appendSeparator(" and ")
		    .appendMinutes()
		    .appendSuffix(" minute", " minutes")
		    .appendSeparator(" and ")
		    .appendSeconds()
		    .appendSuffix(" second ago", " seconds ago")
		    .toFormatter();
		
		return pF.print(p1.normalizedStandard());
	}
	
	@Override
	public void onChildItemSelected(String id) {
		if (!id.equals(detailedUser.getUsername())) {
			Intent detailIntent = new Intent(this, UserProfileActivity.class);
			Bundle b = new Bundle();
			b.putString("id", id);
			detailIntent.putExtras(b);
			startActivity(detailIntent);		
		} else {
			toggle();
		}
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
