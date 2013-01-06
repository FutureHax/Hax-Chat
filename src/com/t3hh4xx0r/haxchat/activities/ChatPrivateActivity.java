package com.t3hh4xx0r.haxchat.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.parse.ParseUser;
import com.t3hh4xx0r.haxchat.DBAdapter;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.parse.ParseHelper;
import com.t3hh4xx0r.haxchat.preferences.PreferencesActivity;
import com.t3hh4xx0r.haxchat.preferences.PreferencesProvider;

public class ChatPrivateActivity extends BaseChatActivity {

	public static ParseUser currentUser;
	String chattingUserNick;
	String currentUserNick;
	ListView lv1;
	Button send;
	EditText input;
	int chatCount = 0;

	ChatMainActivity.ChatListAdapter a;
	ArrayList<ChatMainActivity.Message> chatList;
	
	public ChatPrivateActivity() {
		super("Main");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		
		currentUser = ParseUser.getCurrentUser();
		currentUserNick = PreferencesProvider.deviceNick(this);
//		ParseHelper.getDeviceNick(this, new FindCallback() {			
//			@Override
//			public void done(List<ParseObject> r, com.parse.ParseException e) {
//				if (e == null) {
//					currentUserNick = r.get(0).getString("DeviceNick");
//					setup();
//				} else {
//					Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
//					e.printStackTrace();
//				}
//			}
//		}, false);
		setup();
	}
	
	protected void setup() {
	    lv1 = (ListView) findViewById(R.id.display_list);  
	    lv1.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	    lv1.setStackFromBottom(true);	    
	    lv1.setStackFromBottom(true);
	    lv1.setDivider(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
	    lv1.setDividerHeight(5);
	    chattingUserNick = getIntent().getStringExtra("user");
	    chatList = getChatListFromUser(this, chattingUserNick);
	    a = new ChatMainActivity.ChatListAdapter(this, chatList);
	    lv1.setAdapter(a);

	    input = (EditText) findViewById(R.id.input);
	    send = (Button) findViewById(R.id.send_button);
	    send.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ParseHelper.sendPrivateMessage(input.getText().toString(), Long.toString(System.currentTimeMillis()), getIntent().getStringExtra("user"), v.getContext());				
				Message m = new Message();
				m.setDateTime(convertRawTime(System.currentTimeMillis()));
				m.setMessage(input.getText().toString());
				m.setSender(getIntent().getStringExtra("user"));
				m.setType(getIntent().getStringExtra("user").equals(currentUserNick) ? Message.OUTGOING : Message.INCOMING);
				chatList.add(m);
				a.notifyDataSetChanged();
				DBAdapter db = new DBAdapter(v.getContext());
				db.open(true);
				db.insertChatMessage(currentUserNick, input.getText().toString(), Long.toString(System.currentTimeMillis()), "private");
				db.close();					
				input.setText("");				
			}	    	
	    });
	    
        IntentFilter filter = new IntentFilter();
        filter.addAction(ParseHelper.ACTION_CHAT_UPDATE);
        registerReceiver(LocalChatReceiver, filter);
        
        currentUser = ParseUser.getCurrentUser();
		ChatPrivateActivity.this.setTitle("Private Chat - "+chattingUserNick);		
	}

	private ArrayList<Message> getChatListFromUser(Context c, String name) {
		 ArrayList<Message> res = new ArrayList<Message>();
		 DBAdapter db = new DBAdapter(c);
		 db.open(false);
		 Cursor cur = db.getPrivateChatsForUser(name);
		 chatCount = cur.getCount();
		 while (cur.moveToNext()) { 
			Message m = new Message();
			m.setDateTime(convertRawTime(Long.parseLong(cur.getString(cur.getColumnIndex("sent_time")))));
			m.setMessage(cur.getString(cur.getColumnIndex("message")));
			m.setSender(cur.getString(cur.getColumnIndex("sender")));
			m.setType(cur.getString(cur.getColumnIndex("sender")).equals(currentUserNick) ? Message.OUTGOING : Message.INCOMING);
			res.add(m);											
		 }
		 cur.close();
		 db.close();
		 return res;
	}
	
	public BroadcastReceiver LocalChatReceiver  = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {	
			Bundle b = i.getExtras();
			Message m = new Message();
			m.setDateTime(convertRawTime(Long.parseLong(b.getString("time"))));
			m.setSender(b.getString("sender"));
			m.setMessage(b.getString("message"));
			m.setType(b.getString("sender").equals(currentUserNick) ? Message.OUTGOING : Message.INCOMING);			
			chatList.add(m);
			a.notifyDataSetChanged();
		}
	};
	
    @Override
	public void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ParseHelper.ACTION_CHAT_UPDATE);
        registerReceiver(LocalChatReceiver, filter);
        
        currentUser = ParseUser.getCurrentUser();
		ChatPrivateActivity.this.setTitle("Private Chat - "+chattingUserNick);

        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(LocalChatReceiver);
        super.onPause();
    }
	
	public String convertRawTime(long rawTime) {		
		DateFormat f = SimpleDateFormat.getDateTimeInstance();
		return f.format(rawTime);
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		getSupportMenuInflater().inflate(R.menu.chat_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_sign_out:
				ParseHelper.doLogoutSequence(this);	
				finish();
				break;
				
			case R.id.menu_settings:
				Intent settingsIntent = new Intent(this, PreferencesActivity.class);
				startActivity(settingsIntent);
				break;
				
			case R.id.menu_users:
				getSlidingMenu().showSecondaryMenu();
				break;
				
			case android.R.id.home: 
				toggle();
				break;
				
		}
		return true;		
	}
}