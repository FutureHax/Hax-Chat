package com.t3hh4xx0r.haxchat.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseUser;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.fragments.ChatsListFragment;
import com.t3hh4xx0r.haxchat.fragments.UserListFragment;
import com.t3hh4xx0r.haxchat.parse.ParseHelper;

public class BaseChatActivity extends SlidingFragmentActivity implements
		UserListFragment.Callbacks {

	private String mTitleRes;
	protected Fragment mFrag;

	public BaseChatActivity(String titleRes) {
		mTitleRes = titleRes;
	}

	public static class Message {
		static final int INCOMING = 0;
		static final int OUTGOING = 1;
		
		String sender, message, dateTime;
		int type;
		
		public String getSender() {
			return sender;
		}
		public void setSender(String sender) {
			this.sender = sender;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getDateTime() {
			return dateTime;
		}
		public void setDateTime(String dateTime) {
			this.dateTime = dateTime;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
	}

	public static class ChatListAdapter extends BaseAdapter {
		Context ctx;
		ArrayList<Message> chats;
		
		public ChatListAdapter(Context ctx, ArrayList<Message> chats) {
			super();
			this.ctx = ctx;
			this.chats = chats;
		}

		@Override
		public int getCount() {
			return chats.size();
		}

		@Override
		public Object getItem(int arg0) {
			return chats.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return chats.get(arg0).getType();
		}

		@Override
		public View getView(int pos, View convert, ViewGroup parent) {
			LayoutInflater lI = LayoutInflater.from(ctx);
			Message chat = chats.get(pos);
			if (chat.getType() == Message.INCOMING) {
				return getIncomingChat(lI, chat);
			} else {
				return getOutgoingChat(lI, chat);

			}

		}

		private View getOutgoingChat(LayoutInflater lI, Message chat) {
			View v = lI.inflate(R.layout.chat_row_out, null, false);
			TextView message = (TextView) v.findViewById(R.id.message);
			TextView time = (TextView) v.findViewById(R.id.time);
			
			message.setText(chat.getMessage());
			time.setText(chat.getDateTime());
			
			return v;
		}
	
		private View getIncomingChat(LayoutInflater lI, Message chat) {
			View v = lI.inflate(R.layout.chat_row_in, null, false);				
			TextView message = (TextView) v.findViewById(R.id.message);
			TextView sender = (TextView) v.findViewById(R.id.sender);
			TextView time = (TextView) v.findViewById(R.id.time);
			
			message.setText(chat.getMessage());
			sender.setText(chat.getSender());
			time.setText(chat.getDateTime());
			
			return v;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ParseHelper.init(this);			
		ParseUser user = ParseUser.getCurrentUser();
		if (user == null) {
			Intent i = new Intent(this, LoginActivity.class);
			startActivityForResult(i, 0);
		} 
		
		setTitle(mTitleRes);
		SlidingMenu sm = getSlidingMenu();
		setBehindContentView(R.layout.chats_list_menu);
		sm.setSecondaryMenu(R.layout.user_list_menu);
		sm.setSecondaryShadowDrawable(R.drawable.shadowright);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		sm.setMode(SlidingMenu.LEFT_RIGHT);		
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame_left, new UserListFragment())
		.commit();							
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame_right, new ChatsListFragment())
		.commit();			
		
	}

	@Override
	public void onChildItemSelected(final String id) {
		String[] items = {getString(R.string.options_view_user), getString(R.string.options_start_pm)};
		AlertDialog.Builder options = new AlertDialog.Builder(this);
		options.setTitle(R.string.options_dialog_title);
		options.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface d, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					Intent detailIntent = new Intent(BaseChatActivity.this, UserProfileActivity.class);
					Bundle b = new Bundle();
					b.putString("id", id);
					detailIntent.putExtras(b);
					startActivity(detailIntent);
					break;
				case 1:
					Intent pmIntent = new Intent(BaseChatActivity.this, ChatPrivateActivity.class);
					Bundle pb = new Bundle();
					pb.putString("user", ParseHelper.getDeviceNick(BaseChatActivity.this, null, false));
					pb.putString("reciever", id);
					pmIntent.putExtras(pb);
					startActivity(pmIntent);
					break;
				}
			}
			
		});
		
		options.create().show();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
	    super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (getSlidingMenu().isMenuShowing()) {
			getSlidingMenu().showContent();
		}
	}
}
