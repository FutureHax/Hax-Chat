package com.t3hh4xx0r.haxchat.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.t3hh4xx0r.haxchat.ExpandListAdapter;
import com.t3hh4xx0r.haxchat.ExpandListChild;
import com.t3hh4xx0r.haxchat.ExpandListGroup;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.parse.ParseHelper;

public class UserListFragment extends Fragment {

	static ArrayList<String> allUserNames;
	View rootView;

	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	private Callbacks mCallbacks = sDummyCallbacks;

	public interface Callbacks {
		void onChildItemSelected(String id);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onChildItemSelected(String id) {
		}
	};

	public UserListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView != null) {
			rootView.invalidate();
		}
		
		rootView = inflater.inflate(R.layout.user_list_menu, container, false);
		final ExpandableListView ExpandList = (ExpandableListView) rootView.findViewById(R.id.ExpList);
		ExpandList.setOnChildClickListener(new OnChildClickListener() {			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				mCallbacks.onChildItemSelected(((TextView) v).getText().toString());
				return false;
			}
		});
		ExpandList.setOnGroupCollapseListener(new OnGroupCollapseListener() {			
			@Override
			public void onGroupCollapse(int groupPosition) {		
				((ImageView) rootView.findViewById(R.id.arrow)).setImageResource(android.R.drawable.arrow_down_float);
			}
		});
		ExpandList.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {				
				((ImageView) rootView.findViewById(R.id.arrow)).setImageResource(android.R.drawable.arrow_up_float);
			}
		});

		try {
			ParseHelper.getAllUsers(new FindCallback() {			
				@Override
				public void done(List<ParseObject> r, ParseException e) {
					allUserNames = new ArrayList<String>(); 
					for (int i=0;i<r.size();i++) {
						allUserNames.add(r.get(i).getString("username"));
					}	
					ArrayList<ExpandListGroup> ExpListItems = SetStandardGroups();
					ExpandListAdapter ExpAdapter = new ExpandListAdapter(getActivity(), ExpListItems);
					ExpandList.setAdapter(ExpAdapter);
					rootView.findViewById(R.id.pBar).setVisibility(View.GONE);
				}
			});
		} catch (ParseException e) {
			allUserNames = new ArrayList<String>(); 
			allUserNames.clear();
			e.printStackTrace();
		}
		
		return rootView;
	}
	
    public ArrayList<ExpandListGroup> SetStandardGroups() {
    	ArrayList<ExpandListGroup> list = new ArrayList<ExpandListGroup>();
    	ArrayList<ExpandListChild> allChildren = new ArrayList<ExpandListChild>();
    	ArrayList<ExpandListChild> friendChildren = new ArrayList<ExpandListChild>();
    	
        ExpandListGroup friendsList = new ExpandListGroup();
        friendsList.setName("Friends");
        for (int i=0;i<ParseHelper.getCachedUsersFriends(getActivity()).size();i++) {
        	ExpandListChild friend = new ExpandListChild();
        	friend.setName(ParseHelper.getCachedUsersFriends(getActivity()).get(i));
        	friendChildren.add(friend);
        }
        friendsList.setItems(friendChildren);
        
        ExpandListGroup allList = new ExpandListGroup();
        allList.setName("All Users");
        for (int i=0;i<allUserNames.size();i++) {
        	ExpandListChild user = new ExpandListChild();
        	user.setName(allUserNames.get(i));
            allChildren.add(user);
        }        
        allList.setItems(allChildren);
        
        list.add(friendsList);
        list.add(allList);
        
        return list;
    }
    
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	public static ArrayList<String> getAllUserNames() {
		if (allUserNames == null) {
			return new ArrayList<String>();
		}		
		return allUserNames;
	}

}
