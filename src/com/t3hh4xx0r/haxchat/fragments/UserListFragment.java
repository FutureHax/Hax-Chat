package com.t3hh4xx0r.haxchat.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.t3hh4xx0r.haxchat.FileCache;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.parse.ParseHelper;

public class UserListFragment extends Fragment {
	
	ArrayList<String> groupItem = new ArrayList<String>();
	ArrayList<Object> childItem = new ArrayList<Object>();
	static ArrayList<ParseUser> allUsers;
	View rootView;
	ExpandableListView expList;
	
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
		expList = (ExpandableListView) rootView.findViewById(R.id.ExpList);
		expList.setOnChildClickListener(new OnChildClickListener() {			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				return false;
			}
		});

		try {
			ParseHelper.getAllUsers(new FindCallback() {			
				@Override
				public void done(List<ParseObject> r, ParseException e) {
					if (e == null) {
						allUsers = new ArrayList<ParseUser>(); 
						for (int i=0;i<r.size();i++) {
							allUsers.add((ParseUser) r.get(i));
						}	
						setGroupData();
						setChildGroupData();
						ExpListAdapter a = new ExpListAdapter(groupItem, childItem);
						try {
							a.setInflater((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE), getActivity());
							expList.setAdapter(a);
						} catch (NullPointerException npe) {}
					} else {
						allUsers = new ArrayList<ParseUser>(); 
					}
				}
			});
		} catch (ParseException e) {
			allUsers = new ArrayList<ParseUser>(); 
			e.printStackTrace();
		} 
		
		return rootView;
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

	public static ArrayList<ParseUser> getallUsers() {
		if (allUsers == null) {
			return new ArrayList<ParseUser>();
		}		
		return allUsers;
	}
	
	public void setGroupData() {
		groupItem.add("Friends");
		groupItem.add("All Users");
	}

	public void setChildGroupData() {
    	childItem.add(ParseHelper.getCachedUsersFriends(getActivity()));
    	childItem.add(allUsers);
	}
	
	public class ExpListAdapter extends BaseExpandableListAdapter {

		public ArrayList<String> groupItem, tempChildString;
		ArrayList<ParseUser> tempChildUser;
		public ArrayList<Object> Childtem = new ArrayList<Object>();
		public LayoutInflater minflater;
		public Activity activity;

		public ExpListAdapter(ArrayList<String> grList, ArrayList<Object> childItem) {
			groupItem = grList;
			this.Childtem = childItem;
		}

		public void setInflater(LayoutInflater mInflater, Activity act) {
			this.minflater = mInflater;
			activity = act;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			convertView = minflater.inflate(R.layout.exp_child_row, null);

			String textValue;
			Object item = ((ArrayList<?>) Childtem.get(groupPosition)).get(0);
			if (item instanceof String) {
				tempChildString = (ArrayList<String>) Childtem.get(groupPosition);
				textValue = tempChildString.get(childPosition);	
				final ImageView avatar = (ImageView) convertView.findViewById(R.id.childImage);
				FileCache fCache = new FileCache(activity);
				Bitmap b = fCache.getBitmap(textValue, true);
				if (b != null) {
					avatar.setImageBitmap(b);
				}
			} else {
				tempChildUser = (ArrayList<ParseUser>) Childtem.get(groupPosition);
				textValue = tempChildUser.get(childPosition).getUsername();
				final ImageView avatar = (ImageView) convertView.findViewById(R.id.childImage);
				FileCache fCache = new FileCache(activity);
				Bitmap b = fCache.getBitmap(tempChildUser.get(childPosition).getUsername(), true);
				if (b != null) {					
					avatar.setImageBitmap(b);
				}
			}
			final TextView text = (TextView) convertView.findViewById(R.id.textView1);
			text.setText(textValue);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mCallbacks.onChildItemSelected(text.getText().toString());					
				}
			});


			return convertView;
		}

		@SuppressWarnings("unchecked")
		@Override
		public int getChildrenCount(int groupPosition) {
			return ((ArrayList<String>) Childtem.get(groupPosition)).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public int getGroupCount() {
			return groupItem.size();
		}

		@Override
		public void onGroupCollapsed(int groupPosition) {
			super.onGroupCollapsed(groupPosition);
		}

		@Override
		public void onGroupExpanded(int groupPosition) {
			super.onGroupExpanded(groupPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = minflater.inflate(R.layout.exp_group_row, null);
			}
			ArrayList<String> tmp = ((ArrayList<String>) Childtem.get(groupPosition));
			((CheckedTextView) convertView).setText(groupItem.get(groupPosition));
			((CheckedTextView) convertView).setChecked(isExpanded);
			if (tmp.isEmpty()) {
				((CheckedTextView) convertView).setCompoundDrawables(null, null, null, null);
			}
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
	}
}
