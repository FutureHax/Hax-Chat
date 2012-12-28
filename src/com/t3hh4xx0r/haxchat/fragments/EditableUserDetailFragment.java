package com.t3hh4xx0r.haxchat.fragments;

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

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.activities.UserDetailActivity;
import com.t3hh4xx0r.haxchat.activities.UserListActivity;
import com.t3hh4xx0r.haxchat.parse.ParseHelper;

/**
 * A fragment representing a single User detail screen. This fragment is either
 * contained in a {@link UserListActivity} in two-pane mode (on tablets) or a
 * {@link UserDetailActivity} on handsets.
 */
public class EditableUserDetailFragment extends Fragment {
	public static final int REQ_CODE_PICK_IMAGE = 420;
	View rootView;
	
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private ParseUser detailedUser;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public EditableUserDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		detailedUser = ParseUser.getCurrentUser();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView != null) {
			rootView.invalidate();
		}
		
		rootView = inflater.inflate(R.layout.fragment_editable_user_detail,
				container, false);
		updateUI();
		return rootView;
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
			((TextView) rootView.findViewById(R.id.last_active)).setText(lastActive);
		} catch (Exception e) {
			((TextView) rootView.findViewById(R.id.last_active)).setText("Last Active : Never");
		}
		
		final LinearLayout namesHolder = (LinearLayout) rootView.findViewById(R.id.names_holder);
		ParseHelper.getAlldeviceNicks(rootView.getContext(), new FindCallback() {
			@Override
			public void done(List<ParseObject> r, ParseException e) {
				if (e == null) {
					LayoutInflater lI = LayoutInflater.from(getActivity());
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
								showEditNickAlert(v.getContext(), (Integer) ((LinearLayout) v.getParent()).getTag(), h.name);						
							}			
						});
						namesHolder.addView(row);
					}
				} else {
					e.printStackTrace();
				}
				RelativeLayout mask = (RelativeLayout) rootView.findViewById(R.id.loading_mask);
				mask.setVisibility(View.GONE);
			}
		});
		
		final ImageView avatar = (ImageView) rootView.findViewById(R.id.avatar);
		ParseFile avatarBitmap = (ParseFile) detailedUser.get("Avatar");
		if (avatarBitmap != null) {
			avatarBitmap.getDataInBackground(new GetDataCallback() {
				public void done(byte[] data, ParseException e) {
					if (e == null) {
						InputStream is = new ByteArrayInputStream(data);
						Bitmap bmp = BitmapFactory.decodeStream(is);
						avatar.setImageBitmap(bmp);
					} else {
						e.printStackTrace();
					}
				}
			});
		}
		ImageView avatarEdit = (ImageView) rootView.findViewById(R.id.avatar_edit);
		avatarEdit.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK,
			               android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, REQ_CODE_PICK_IMAGE); 
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
					ParseHelper.getAlldeviceNicks(getActivity().getApplicationContext(), new FindCallback() {						
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
		    case REQ_CODE_PICK_IMAGE:
		        if(resultCode == Activity.RESULT_OK){  
		            Uri selectedImage = imageReturnedIntent.getData();
		            try {
						Bitmap b = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
			            ((ImageView) rootView.findViewById(R.id.avatar)).setImageBitmap(b);
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
}
