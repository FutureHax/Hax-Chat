package com.t3hh4xx0r.haxchat.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.parse.ParseHelper;

/**
 * A list fragment representing a list of Users. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link UserDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class UserListFragment extends Fragment implements OnClickListener{
	LinearLayout friendsL;
	LinearLayout allL;
	LinearLayout onlineL;
	
	TextView friendsText;
	TextView allText;
	TextView onlineText;
	
	View rootView;
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public UserListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO: replace with a real list adapter.
//		setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
//				android.R.layout.simple_list_item_activated_1,
//				android.R.id.text1, DummyContent.ITEMS));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView != null) {
			rootView.invalidate();
		}
		
		rootView = inflater.inflate(R.layout.user_list_frag, container, false);
		
		friendsL = (LinearLayout) rootView.findViewById(R.id.friends_users);
		friendsL.setTag("contracted");
		allL = (LinearLayout) rootView.findViewById(R.id.all_users);
		allL.setTag("contracted");
		onlineL = (LinearLayout) rootView.findViewById(R.id.online_users);
		onlineL.setTag("contracted");		
		
		friendsText = (TextView) rootView.findViewById(R.id.friends_text);
		friendsText.setOnClickListener(expandListener);
		allText = (TextView) rootView.findViewById(R.id.all_text);
		allText.setOnClickListener(expandListener);
		onlineText = (TextView) rootView.findViewById(R.id.online_text);
		onlineText.setOnClickListener(expandListener);		
		
		return rootView;
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onClick(View view) {
		TextView v = (TextView) view;
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(v.getText().toString());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	private void setActivatedPosition(int position) {
//		if (position == ListView.INVALID_POSITION) {
//			getListView().setItemChecked(mActivatedPosition, false);
//		} else {
//			getListView().setItemChecked(position, true);
//		}

		mActivatedPosition = position;
	}

	
	public OnClickListener expandListener = new OnClickListener() {
		public void onClick(View v) {			
			doExpand(v);				
		}
	};
	
	public OnClickListener contractListener = new OnClickListener() {
		public void onClick(View v) {
			doContract(v);					
		}
	};

	@SuppressWarnings("unchecked")
	protected void doContract(View view) {
		LinearLayout v = (LinearLayout) view.getParent();
		v.setTag("contracted");
		
		ListView theList = (ListView) v.getChildAt(v.getChildCount()-1);
		ArrayAdapter<String> a = (ArrayAdapter<String>) theList.getAdapter();
		a.clear();	
		view.setOnClickListener(expandListener);
	}

	protected void doExpand(View view) {
		Context c = view.getContext();
		LinearLayout v = (LinearLayout) view.getParent();
		v.setTag("expanded");
		final ListView theList = (ListView) v.getChildAt(v.getChildCount()-1);
		switch (view.getId()) {
			case R.id.friends_text:
				ArrayAdapter<String> fA = new ArrayAdapter<String>(c, R.layout.simple_list_item_white, ParseHelper.getCachedUsersFriends(c));
				theList.setAdapter(fA);
				theList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> a, View v,
							int pos, long id) {
						Toast.makeText(v.getContext(), theList.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
						mCallbacks.onItemSelected(((TextView) v).getText().toString());
					}					
				});
			break;
			
			case R.id.online_text:
				
			break;
				
			case R.id.all_text:
				try {
					ArrayAdapter<String> aA = new ArrayAdapter<String>(c, R.layout.simple_list_item_white, ParseHelper.getAllUsers());
					theList.setAdapter(aA);
					theList.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> a, View v,
								int pos, long id) {
							Toast.makeText(v.getContext(), theList.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
							mCallbacks.onItemSelected(((TextView) v).getText().toString());
						}
						
					});	
				} catch (ParseException e) {
					e.printStackTrace();
				}							
			break;	
		}
		
		view.setOnClickListener(contractListener);
		
	}
}
