package com.t3hh4xx0r.haxchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.fragments.UserDetailFragment;
import com.t3hh4xx0r.haxchat.fragments.UserListFragment;

/**
 * An activity representing a list of Users. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link UserDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link UserListFragment} and the item details (if present) is a
 * {@link UserDetailFragment}.
 * <p>
 * This activity also implements the required {@link UserListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class UserListActivity extends FragmentActivity implements
		UserListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list);

		if (findViewById(R.id.user_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;			
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link UserListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			Log.d("TWO PANES BRAH!", "item callback fired");
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(UserDetailFragment.ARG_ITEM_ID, id);
			UserDetailFragment fragment = new UserDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.user_detail_container, fragment).commit();

		} else {
			Log.d("ONE PANE BRAH!", "item callback fired");
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, UserDetailActivity.class);
			detailIntent.putExtra(UserDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
