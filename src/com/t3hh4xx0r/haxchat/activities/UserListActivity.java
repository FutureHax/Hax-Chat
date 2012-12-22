package com.t3hh4xx0r.haxchat.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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
			mTwoPane = true;			
		}
	}

	
	public class MenuItemHolder {
		public TextView label;
		public ListView lv;
	}
	
	@Override
	public void onChildItemSelected(String id) {
		if (mTwoPane) {			
			Bundle arguments = new Bundle();
			arguments.putString(UserDetailFragment.ARG_ITEM_ID, id);
			UserDetailFragment fragment = new UserDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.user_detail_container, fragment).commit();
		} else {
			Intent detailIntent = new Intent(this, UserDetailActivity.class);
			detailIntent.putExtra(UserDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
