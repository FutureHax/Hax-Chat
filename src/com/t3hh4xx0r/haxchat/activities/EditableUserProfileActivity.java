package com.t3hh4xx0r.haxchat.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.t3hh4xx0r.haxchat.FileCache;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.fragments.UserListFragment;
import com.t3hh4xx0r.haxchat.parse.ParseHelper;

public class EditableUserProfileActivity extends SlidingFragmentActivity implements UserListFragment.Callbacks {
	private ParseUser detailedUser;
	Button pmButton;
	Button friendButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editable_user_detail_activity);
		detailedUser = ParseUser.getCurrentUser();

		setupMenu();
		updateUI();

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

	class NameHolder {
		TextView name;
		TextView device;
		ImageButton editIcon;
	}
	
	public void updateUI() {	

		try {
			String dateString = detailedUser.get("lastActive").toString();
			String lastActive = "Last Active : " + getPeriodizedTime(dateString);
			((TextView) findViewById(R.id.last_active)).setText(lastActive);
		} catch (Exception e) {
			((TextView) findViewById(R.id.last_active)).setText("Last Active : Never");
		}
		
		final LinearLayout namesHolder = (LinearLayout) findViewById(R.id.names_holder);
		ParseHelper.getAlldeviceNicks(this, new FindCallback() {
			@Override
			public void done(List<ParseObject> r, ParseException e) {
				if (e == null) {
					LayoutInflater lI = LayoutInflater.from(namesHolder.getContext());
					for (int i=0;i<r.size();i++) {						
						LinearLayout row = (LinearLayout) lI.inflate(R.layout.editable_name_item, namesHolder, false);
						row.setTag(i);
						final NameHolder h = new NameHolder();
						
						h.name = (TextView) row.findViewById(R.id.user_name);
						h.device = (TextView) row.findViewById(R.id.user_device);
						h.editIcon = (ImageButton) row.findViewById(R.id.user_name_edit);
						
						h.name.setText(r.get(i).getString("DeviceNick"));
						h.device.setText(r.get(i).getString("DeviceModel"));
						h.editIcon.setOnClickListener(new OnClickListener() {							
							@Override
							public void onClick(View v) {
								showEditNickAlert(v.getContext(), (Integer) ((LinearLayout) v.getParent()).getTag(), h.device);						
							}			
						});
						namesHolder.addView(row);
					}
				} else {
					e.printStackTrace();
				}
				RelativeLayout mask = (RelativeLayout) findViewById(R.id.loading_mask);
				mask.setVisibility(View.GONE);
			}
		});
		
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
		ImageView avatarEdit = (ImageView) findViewById(R.id.avatar_edit);
		avatarEdit.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK,
			               android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, 420); 
			}
		});		
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

	private void showEditNickAlert(Context c, final int tag, final TextView nameView) {
		String model = nameView.getText().toString();
		final EditText input = new EditText(c);
		final AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle("Device Nickname")
		.setView(input)
		.setMessage("Input a new nickname for your " + model)
		    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, int which) {						
					ParseHelper.getAlldeviceNicks(getApplicationContext(), new FindCallback() {						
						@Override
						public void done(List<ParseObject> r, ParseException e) {
							if (e == null) {
								ParseObject device;
								device = r.get(tag);
								device.put("DeviceNick", input.getText().toString());
								device.saveInBackground();
								detailedUser.refreshInBackground(null);
								nameView.setText(input.getText().toString());
							} else {
								e.printStackTrace();
							}
							dialog.cancel();
						}
					});	
				}
		    })
		    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {	
					dialog.cancel();
				}
		    });

		AlertDialog dialog = builder.create();
		dialog.show();								
	}
	
	public void onActivityResult(int requestCode, int resultCode, 
		       Intent imageReturnedIntent) {
		    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

		    switch(requestCode) { 
		    case 420:
		        if(resultCode == Activity.RESULT_OK){  
		            Uri selectedImage = imageReturnedIntent.getData();
		            try {
						Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
			            ((ImageView) findViewById(R.id.avatar)).setImageBitmap(b);
			            ByteArrayOutputStream stream = new ByteArrayOutputStream();
			            b.compress(Bitmap.CompressFormat.PNG, 100, stream);
			            byte[] byteArray = stream.toByteArray();
			            final ParseFile file = new ParseFile(byteArray);
			            file.saveInBackground(new SaveCallback() {							
							@Override
							public void done(ParseException e) {
								if (e == null) {
									detailedUser.put("Avatar", file);
						            detailedUser.saveInBackground();
								}
							}
						});
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
		        }
		    }
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
