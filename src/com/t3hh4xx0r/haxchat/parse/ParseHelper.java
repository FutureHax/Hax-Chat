package com.t3hh4xx0r.haxchat.parse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.RefreshCallback;
import com.parse.SaveCallback;
import com.t3hh4xx0r.haxchat.DBAdapter;
import com.t3hh4xx0r.haxchat.activities.ChatMainActivity;
import com.t3hh4xx0r.haxchat.activities.ChatPrivateActivity;
import com.t3hh4xx0r.haxchat.activities.LoginActivity;
import com.t3hh4xx0r.haxchat.preferences.PreferencesProvider;

public 	class ParseHelper {
	
	public static final String ACTION_CHAT = "com.t3hh4xx0r.haxchat.ACTION_CHAT_SENT";
	public static final String ACTION_CHAT_UPDATE = "com.t3hh4xx0r.haxchat.ACTION_CHAT_SENT_UPDATE";
	
	public static void init(Context c) {
		Parse.initialize(c.getApplicationContext(), "ymsMlq604RSXbAZN3oQ50yyOpiELU7cNgudtzA15", "a9mDZkyzc9hv8VXR2knAstlmIhNEZOyO2sfqiczK"); 
		registerForPush(c);
	}

	public static void registerForPush(Context c) {
		PushService.subscribe(c.getApplicationContext(), "chat", ChatMainActivity.class);

		if (PreferencesProvider.Push.getPushChannels(c)[0]) {
			PushService.subscribe(c.getApplicationContext(), "", LoginActivity.class);
		} else {
			PushService.unsubscribe(c.getApplicationContext(), "");
		}
		
		if (PreferencesProvider.Push.getPushChannels(c)[1]) {
    		PushService.subscribe(c.getApplicationContext(), "testing", LoginActivity.class);
		} else {
			PushService.unsubscribe(c.getApplicationContext(), "testing");
		}
		
		if (PreferencesProvider.Push.getPushChannels(c)[2]) {
    		PushService.subscribe(c.getApplicationContext(), "updates", LoginActivity.class);
		} else {
			PushService.unsubscribe(c.getApplicationContext(), "updates");
		}		
		
		if (ParseUser.getCurrentUser() !=null &&
				ParseUser.getCurrentUser().getUsername() !=null) {
			subscribePrivateChat(c);
		}
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<String> getUsersFriends() {	
		if (!(ParseUser.getCurrentUser().get("friendsList") instanceof ArrayList<?>)) {
			return new ArrayList<String>();
		} else {
			return (ArrayList<String>) ParseUser.getCurrentUser().get("friendsList");
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void addUserFriend(ParseUser user, final Context c) {
		Object o = ParseUser.getCurrentUser().get("friendsList");
		if (!(o instanceof ArrayList<?>)) {
			o = new ArrayList<String>();
		}
		((ArrayList<String>) o).add(user.getUsername());
		ParseUser.getCurrentUser().put("friendsList", o);
		ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {			
			@Override
			public void done(ParseException e) {
				if (e != null) {
					e.printStackTrace();
					return;
				}
				Toast.makeText(c, "added", Toast.LENGTH_SHORT).show();
				ParseUser.getCurrentUser().refreshInBackground(new RefreshCallback() {
					@Override
					public void done(ParseObject arg0, ParseException arg1) {
						DBAdapter db = new DBAdapter(c);
						db.open(true);
						db.putFriendsList(ParseUser.getCurrentUser().getUsername(), getUsersFriends());
						db.close();									
					}
				});				
			}
		}); 
	}
	
	public static String getDeviceNick(Context c) {
		return PreferencesProvider.deviceNick(c);
	}
		
	public static boolean isUserAFriend(ParseUser u, Context c) {
		if (getCachedUsersFriends(c).contains(u.getUsername())) {
			return true;
		} else {
			return false;
		}
	}
	
	//Maybe not necessary
	public static ArrayList<String> getCachedUsersFriends(Context c) {	
		DBAdapter db = new DBAdapter(c);
		db.open(false);
		ArrayList<String> res = db.getFriendsList();
		db.close();	
		return res;
	}
	
	public static void getAllUsers(FindCallback cb) throws ParseException {				
		ParseUser.getQuery().findInBackground(cb);
	}

	public static void refresh() {
		ParseUser.getCurrentUser().refreshInBackground(null);
	}

//	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
//	public static void updateUser(Map<String, Object> opts, Context c) {		
//		ParseUser.getCurrentUser();
//		UserUpdateManager updateMan = new UserUpdateManager(c);
//		updateMan.up
////		synchronized (u) {
////			for (Map.Entry<String, Object> entry: opts.entrySet()) {
////				u.put(entry.getKey(), entry.getValue());
////			}
////			u.saveInBackground();
////		}
//	}
 
	public static void getUserByEmail(String s, FindCallback cb){
		ParseQuery userQuery = ParseUser.getQuery();
		userQuery.whereEqualTo("email", s);
		userQuery.findInBackground(cb);
	}
	
	public static void getUserByUser(String s, FindCallback cb){
		ParseQuery userQuery = ParseUser.getQuery();
		userQuery.whereEqualTo("username", s);
		userQuery.findInBackground(cb);
	}
	
	public static void sendPrivateMessage(final String message, final String time, final String user, Context c) {
//		ParseHelper.getDeviceNick(c, new FindCallback() {
//			@Override
//			public void done(List<ParseObject> r, com.parse.ParseException e) {
//				String name = ParseUser.getCurrentUser().getUsername();
//				if (e == null) {
//					name = r.get(0).getString("DeviceNick");
//				}
//				JSONObject data;
//				try {
//					data = new JSONObject("{\"action\": \""+ACTION_CHAT+"\"," +
//							"\"sender\": \""+name+"\"," +
//							"\"time\": \""+time+"\"," +
//							"\"type\": \"private\"," +
//							"\"message\": \""+message+"\"" +
//							"}");
//					ParsePush push = new ParsePush();
//			        push.setChannel("chat_"+user);
//			        push.setData(data);	
//			        push.sendInBackground();
//				} catch (JSONException e1) {
//					e1.printStackTrace();
//				}
//			}
//		}, false);
		if (TextUtils.isEmpty(message)) {
			return;
		}
		String name = PreferencesProvider.deviceNick(c);
		JSONObject data;
		try {
			data = new JSONObject("{\"action\": \""+ACTION_CHAT+"\"," +
					"\"sender\": \""+name+"\"," +
					"\"time\": \""+time+"\"," +
					"\"type\": \"private\"," +
					"\"message\": \""+message+"\"" +
					"}");
			ParsePush push = new ParsePush();
	        push.setChannel("chat_"+user);
	        push.setData(data);	
	        push.sendInBackground();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
        
	}
	
	public static void sendMessage(final String message, final String time, Context c) throws JSONException {
//		ParseHelper.getDeviceNick(c, new FindCallback() {
//			@Override
//			public void done(List<ParseObject> r, com.parse.ParseException e) {
//				String name = ParseUser.getCurrentUser().getUsername();
//				if (e == null) {
//					name = r.get(0).getString("DeviceNick");
//				}
//				JSONObject data;
//				try {
//					data = new JSONObject("{\"action\": \""+ACTION_CHAT+"\"," +
//							"\"sender\": \""+name+"\"," +
//							"\"time\": \""+time+"\"," +
//							"\"type\": \"public\"," +
//							"\"message\": \""+message+"\"" +
//							"}");
//					ParsePush push = new ParsePush();
//			        push.setChannel("chat");
//			        push.setData(data);	
//			        push.sendInBackground();
//				} catch (JSONException e1) {
//					e1.printStackTrace();
//				}
//			}
//		}, false);
		if (TextUtils.isEmpty(message)) {
			return;
		}
		JSONObject data;
		try {
			data = new JSONObject("{\"action\": \""+ACTION_CHAT+"\"," +
					"\"sender\": \""+PreferencesProvider.deviceNick(c)+"\"," +
					"\"time\": \""+time+"\"," +
					"\"type\": \"public\"," +
					"\"message\": \""+message+"\"" +
					"}");
			ParsePush push = new ParsePush();
	        push.setChannel("chat");
	        push.setData(data);	
	        push.sendInBackground();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

	}
		
	public static Date getDateInGMT() throws java.text.ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return (Date) sdf.parse(sdf.format(new Date()));
	}

	public static void isDeviceRegistered(Context c, FindCallback cb) {
		ParseQuery q = new ParseQuery("Device");
		q.whereEqualTo("DeviceID", PreferencesProvider.id(c));
		q.whereEqualTo("UserId", ParseUser.getCurrentUser().getObjectId());
		q.findInBackground(cb);
	}

	public static void subscribePrivateChat(Context c) {
		  PushService.subscribe(c, "chat_"+ParseUser.getCurrentUser().getUsername(), ChatPrivateActivity.class);		
	}

	public static void doLogoutSequence(Context ctx) {
		ParseUser.logOut();
		Object[] list = PushService.getSubscriptions(ctx).toArray();
		for (int i=0;i<list.length;i++) {
			if (!list[i].equals("Broadcast") &&
					!list[i].equals("testing") &&
					!list[i].equals("updates")) {
				PushService.unsubscribe(ctx, (String) list[i]);
			}
		}
		DBAdapter d = new DBAdapter(ctx);
		d.open(true);
		d.dropChats();
		d.close();
		Intent i = new Intent(ctx, LoginActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ctx.startActivity(i);		
		
	}

	public static void getAlldeviceNicks(Context c, FindCallback cb) {
		ParseQuery q = new ParseQuery("Device");
		q.whereEqualTo("UserId", ParseUser.getCurrentUser().getObjectId());
		q.findInBackground(cb);
	}
}
