package com.t3hh4xx0r.haxchat.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.t3hh4xx0r.haxchat.DBAdapter;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.SlideMenu;
import com.t3hh4xx0r.haxchat.parse.ParseHelper;
import com.t3hh4xx0r.haxchat.preferences.Preferences;

public class ChatMainActivity extends SherlockActivity {
	ParseUser user;
	ListView lv1;
	Button send;
	EditText input;
	
	static final String ACTION_CHAT = "com.t3hh4xx0r.haxchat.ACTION_CHAT_SENT";
	static final String ACTION_CHAT_UPDATE = "com.t3hh4xx0r.haxchat.ACTION_CHAT_SENT_UPDATE";
	int chatCount = 0;

	ArrayAdapter<String> a;
	ArrayList<String> chatList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_main);
		
		ParseHelper.init(this);
		
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setBackgroundDrawable(new ColorDrawable(android.R.color.background_dark));
		
		user = ParseUser.getCurrentUser();
		if (user == null) {
			Intent i = new Intent(this, LoginActivity.class);
			startActivityForResult(i, 0);
		} 
		
		
		
	    lv1 = (ListView) findViewById(R.id.display_list);  
	    lv1.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	    lv1.setStackFromBottom(true);
	    chatList = getChatList(this);
	    a = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, chatList);
	    lv1.setAdapter(a);
	    
	    input = (EditText) findViewById(R.id.input);
	    send = (Button) findViewById(R.id.send_button);
	    send.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				try {
					sendMessage(input.getText().toString(), Long.toString(System.currentTimeMillis()));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				input.setText("");
			}	    	
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_chat_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_sign_out:
				ParseUser.logOut();
				user = null;
				Object[] list = PushService.getSubscriptions(this).toArray();
				for (int i=0;i<list.length;i++) {
					if (!list[i].equals("Broadcast") &&
							!list[i].equals("testing") &&
							!list[i].equals("updates")) {
						PushService.unsubscribe(this, (String) list[i]);
					}
				}
				Intent i = new Intent(this, LoginActivity.class);
    			startActivityForResult(i, 0);
				break;
				
			case R.id.menu_settings:
				Intent settingsIntent = new Intent(ChatMainActivity.this, Preferences.class);
				startActivity(settingsIntent);
				break;
				
			case R.id.menu_users:
				Intent usersIntent = new Intent(ChatMainActivity.this, UserListActivity.class);
				startActivity(usersIntent);
				break;
				
			case android.R.id.home: 
				SlideMenu slide = (SlideMenu) findViewById(R.id.menu);
				slide.toggle();
				
				break;
				
		}
		return true;		
	}
	
	private ArrayList<String> getChatList(Context c) {
		 ArrayList<String> res = new ArrayList<String>();
		 DBAdapter db = new DBAdapter(c);
		 db.open();
		 Cursor cur = db.getChats();
		 chatCount = cur.getCount();
		 while (cur.moveToNext()) {
			StringBuilder sB = new StringBuilder();
			sB.append(convertRawTime(Long.parseLong(cur.getString(cur.getColumnIndex("sent_time")))));
			sB.append(":");
			sB.append(cur.getString(cur.getColumnIndex("sender")));
			sB.append(" - ");
			sB.append(cur.getString(cur.getColumnIndex("message")));
			res.add(sB.toString());
		 }
		 return res;
	}
	
	public void sendMessage(String message, String time) throws JSONException {
		JSONObject data = new JSONObject("{\"action\": \""+ACTION_CHAT+"\"," +
				"\"sender\": \""+user.getUsername()+"\"," +
				"\"time\": \""+time+"\"," +
				"\"type\": \"public\"," +
				"\"message\": \""+message+"\"" +
				"}");
		
        ParsePush push = new ParsePush();
        push.setChannel("chat");
        push.setData(data);	
        push.sendInBackground();
        
        try {
        	Map<String, Object> opts = new HashMap<String, Object>();
        	opts.put("lastActive", getDateInGMT());
			ParseHelper.updateUser(opts);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private Date getDateInGMT() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return (Date) sdf.parse(sdf.format(new Date()));
	}

	public static void sendPrivateMessage(String message, String time, String user) throws JSONException {
		JSONObject data = new JSONObject("{\"action\": \""+ACTION_CHAT+"\"," +
				"\"sender\": \""+ParseUser.getCurrentUser().getUsername()+"\"," +
				"\"time\": \""+time+"\"," +
				"\"type\": \"private\"," +
				"\"message\": \""+message+"\"" +
				"}");

        ParsePush push = new ParsePush();
        push.setChannel("chat_"+user);
        push.setData(data);	
        push.sendInBackground();
	}
	
	public BroadcastReceiver LocalChatReceiver  = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {		
			Bundle b = i.getExtras();
			String message = b.getString("message");
			String time = convertRawTime(Long.parseLong(b.getString("time")));
			String sender = b.getString("sender");
			
			StringBuilder sB = new StringBuilder();
			sB.append(time);
			sB.append(":");
			sB.append(sender);
			sB.append(" - ");
			sB.append(message);
			chatList.add(sB.toString());
			a.notifyDataSetChanged();
		}
	};
	
    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CHAT_UPDATE);
        registerReceiver(LocalChatReceiver, filter);
        
        user = ParseUser.getCurrentUser();
        try {
        	ParseHelper.getDeviceNick(user, this, new FindCallback() {						
    			@Override
    			public void done(List<ParseObject> r, com.parse.ParseException e) {
    				if (e == null && r.size() == 1) {
    					ParseObject device = r.get(0);
    					ChatMainActivity.this.setTitle(device.getString("DeviceNick"));
    				}
    			}						
    		}, false);	
        } catch (Exception e) {
        	
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(LocalChatReceiver);
        super.onPause();
    }
    
	@Override
	protected void onActivityResult(
	    int aRequestCode, int aResultCode, Intent aData) {
	    switch (aRequestCode) {
	        case 0:
	            if (aResultCode == Activity.RESULT_OK) {
	            	user = ParseUser.getCurrentUser();
	            	ParseHelper.getDeviceNick(user, this, new FindCallback() {						
	        			@Override
	        			public void done(List<ParseObject> r, com.parse.ParseException e) {
	        				if (e == null && r.size() == 1) {
	        					ParseObject device = r.get(0);
	        					ChatMainActivity.this.setTitle(device.getString("DeviceNick"));
	        				}
	        			}						
	        		}, false);	
	            	if (user == null) {
	        			Intent i = new Intent(this, LoginActivity.class);
	        			startActivityForResult(i, 0);
	        		} 
	            } else {
	            	Intent i = new Intent(this, LoginActivity.class);
	    			startActivityForResult(i, 0);
	    		}
	            break;	        
	    }
	    super.onActivityResult(aRequestCode, aResultCode, aData);
	}
	
	public String convertRawTime(long rawTime) {		
		DateFormat f = SimpleDateFormat.getDateTimeInstance();
		return f.format(rawTime);
	}
}