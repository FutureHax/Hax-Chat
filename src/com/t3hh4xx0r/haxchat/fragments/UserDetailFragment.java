package com.t3hh4xx0r.haxchat.fragments;

import java.util.List;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.activities.ChatMainActivity;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {		
			if (getArguments().getString(ARG_ITEM_ID).contains("@")) {
				ParseHelper.getUserByEmail(getArguments().getString(ARG_ITEM_ID), new FindCallback() {
					@Override
					public void done(List<ParseObject> res, ParseException e) {
						if (e == null) {
							detailedUser = (ParseUser) res.get(0);
							updateUI();
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
							updateUI();
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
				            try {
								ChatMainActivity.sendPrivateMessage(value, Long.toString(System.currentTimeMillis()), getArguments().getString(
										ARG_ITEM_ID));
							} catch (JSONException e) {
								Toast.makeText(v.getContext(), "FAIL", Toast.LENGTH_SHORT).show();
								e.printStackTrace();
							}
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
		String s = (detailedUser.get("lastActive") != null) ? "Last Active : " + detailedUser.get("lastActive").toString() : "Last Active : Never";
		((TextView) rootView.findViewById(R.id.last_active)).setText(s);
	}
}
