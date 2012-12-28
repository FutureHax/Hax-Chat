package com.t3hh4xx0r.haxchat.fragments;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.activities.UserDetailActivity;
import com.t3hh4xx0r.haxchat.activities.UserListActivity;
import com.t3hh4xx0r.haxchat.parse.ParseHelper;

/**
 * A fragment representing a single User detail screen. This fragment is either
 * contained in a {@link UserListActivity} in two-pane mode (on tablets) or a
 * {@link UserDetailActivity} on handsets.
 */
public class UserDetailFragment extends Fragment {

	Button pmButton;
	Button friendButton;
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
	public UserDetailFragment() {
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		userCheck();
	}

	private void userCheck() {
		if (getArguments().containsKey(ARG_ITEM_ID)) {		
			if (getArguments().getString(ARG_ITEM_ID).contains("@")) {
				ParseHelper.getUserByEmail(getArguments().getString(ARG_ITEM_ID), new FindCallback() {
					@Override
					public void done(List<ParseObject> res, ParseException e) {
						if (e == null) {
							detailedUser = (ParseUser) res.get(0);
							if (detailedUser.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
								Log.d("W00T "+detailedUser.getUsername(), ParseUser.getCurrentUser().getUsername());

								Bundle arguments = new Bundle();
								arguments.putString(UserDetailFragment.ARG_ITEM_ID, getArguments().getString(ARG_ITEM_ID));
								EditableUserDetailFragment fragment = new EditableUserDetailFragment();
								fragment.setArguments(arguments);
								getActivity().getSupportFragmentManager().beginTransaction()
										.replace(R.id.user_detail_container, fragment).commit();
							} else {
								Log.d("FAIL "+detailedUser.getUsername(), ParseUser.getCurrentUser().getUsername());
								updateUI();
							}
						} else {
							e.printStackTrace();
						}
					}				
				});
			} else {
				ParseHelper.getUserByUser(getArguments().getString(ARG_ITEM_ID), new FindCallback() {
					@Override
					public void done(List<ParseObject> res, ParseException e) {
						if (e == null) {
							detailedUser = (ParseUser) res.get(0);
							if (detailedUser.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
								Log.d("W00T "+detailedUser.getUsername(), ParseUser.getCurrentUser().getUsername());

								Bundle arguments = new Bundle();
								arguments.putString(UserDetailFragment.ARG_ITEM_ID, getArguments().getString(ARG_ITEM_ID));
								EditableUserDetailFragment fragment = new EditableUserDetailFragment();
								fragment.setArguments(arguments);
								getActivity().getSupportFragmentManager().beginTransaction()
										.replace(R.id.user_detail_container, fragment).commit();
							} else {
								Log.d("FAIL "+detailedUser.getUsername(), ParseUser.getCurrentUser().getUsername());
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView != null) {
			rootView.invalidate();
		}
		
		rootView = inflater.inflate(R.layout.fragment_user_detail,
				container, false);

		pmButton = (Button) rootView.findViewById(R.id.pm_button);
		pmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				   final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
				    final EditText input = new EditText(v.getContext());
				    alert.setView(input);
				    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				            String value = input.getText().toString().trim();
				            ParseHelper.sendPrivateMessage(value, Long.toString(System.currentTimeMillis()), detailedUser.getUsername(), alert.getContext());
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
		
		friendButton = (Button) rootView.findViewById(R.id.add_friend);
		friendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				ParseHelper.addUserFriend(detailedUser, v.getContext());
				friendButton.setVisibility(View.GONE);
			}
		});

		return rootView;
	}
	
	public void updateUI() {
		friendButton.setVisibility(ParseHelper.isUserAFriend(detailedUser, getActivity()) ? View.GONE : View.VISIBLE);
		RelativeLayout mask = (RelativeLayout) rootView.findViewById(R.id.loading_mask);
		mask.setVisibility(View.GONE);
		((TextView) rootView.findViewById(R.id.user_name)).setText(detailedUser.getUsername());
		try {
			String dateString = detailedUser.get("lastActive").toString();
			String lastActive = "Last Active : " + dateString;
			((TextView) rootView.findViewById(R.id.last_active)).setText(lastActive);
		} catch (Exception e) {
			e.printStackTrace();
			((TextView) rootView.findViewById(R.id.last_active)).setText("Last Active : Never");
		}
		
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
	}
}
