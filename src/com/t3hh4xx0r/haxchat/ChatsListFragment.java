package com.t3hh4xx0r.haxchat;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ChatsListFragment extends Fragment {

	private static View menu;
	private Typeface font;
	View searchET;
	int toggleId;
	private ArrayList<ChatListMenuItem> menuItemList;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		menu = inflater.inflate(R.layout.chats_list_menu, container, false);
		ListView list = (ListView) menu.findViewById(R.id.menu_listview);
		ChatListMenuItem[] items = menuItemList.toArray(new ChatListMenuItem[menuItemList.size()]);
		final ChatsListAdapter adap = new ChatsListAdapter(getActivity(), items, font);
		list.setAdapter(adap);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//startSubActivity(position, view.getContext());
			}
		});
		
		return menu;
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setupMenu();
	}


	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public static class ChatListMenuItem {
		public Drawable icon;
		public String label;
	}
	
	public static class ChatsListAdapter extends ArrayAdapter<ChatListMenuItem> {
			ChatListMenuItem[] items;
			Typeface itemFont;
			
			class MenuItemHolder {
				public TextView label;
				public ImageView icon;
			}
			
			public ChatsListAdapter(Context c, ChatListMenuItem[] items, Typeface itemFont) {
				super(c, R.id.menu_label, items);
				this.items = items;
				this.itemFont = itemFont;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View rowView = convertView;
				if (rowView == null) {
					LayoutInflater inflater = LayoutInflater.from(getContext());
					rowView = inflater.inflate(R.layout.slidemenu_listitem, null);
					MenuItemHolder viewHolder = new MenuItemHolder();
					viewHolder.label = (TextView) rowView.findViewById(R.id.menu_label);
					if(itemFont != null) 
						viewHolder.label.setTypeface(itemFont);
					viewHolder.icon = (ImageView) rowView.findViewById(R.id.menu_icon);
					rowView.setTag(viewHolder);
				}

				MenuItemHolder holder = (MenuItemHolder) rowView.getTag();
				String s = items[position].label;
				holder.label.setText(s);
				holder.icon.setImageDrawable(items[position].icon);

				return rowView;
			}
		}
	
	private void setupMenu() {
		menuItemList = new ArrayList<ChatListMenuItem>();
		ChatListMenuItem pubItem = new ChatListMenuItem();
		pubItem.icon = getResources().getDrawable(R.drawable.ic_launcher);
		pubItem.label = "Public Chat";
		menuItemList.add(pubItem);
		
		ArrayList<String> privateList = new ArrayList<String>();
		DBAdapter db = new DBAdapter(this.getActivity());
		db.open();
		Cursor c = db.getPrivateChatList();
		while (c.moveToNext()) {
			if (privateList.contains(c.getString(c.getColumnIndex("sender")))) {				
				continue;
			}
			privateList.add(c.getString(c.getColumnIndex("sender")));
			
			ChatListMenuItem item = new ChatListMenuItem();
			item.icon = getResources().getDrawable(R.drawable.ic_launcher);
			item.label = c.getString(c.getColumnIndex("sender"));
			menuItemList.add(item);
		}
		db.close();
	}
}
