package com.t3hh4xx0r.haxchat.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.RefreshCallback;
import com.parse.SaveCallback;
import com.t3hh4xx0r.haxchat.DBAdapter;
import com.t3hh4xx0r.haxchat.activities.ChatMainActivity;
import com.t3hh4xx0r.haxchat.activities.LoginActivity;
import com.t3hh4xx0r.haxchat.preferences.PreferencesProvider;

public 	class ParseHelper {
	
	public static void init(Context c) {
		Parse.initialize(c.getApplicationContext(), "ymsMlq604RSXbAZN3oQ50yyOpiELU7cNgudtzA15", "a9mDZkyzc9hv8VXR2knAstlmIhNEZOyO2sfqiczK"); 
		PushService.subscribe(c.getApplicationContext(), "chat", ChatMainActivity.class);
		PushService.subscribe(c.getApplicationContext(), "", ChatMainActivity.class);
	}

	public static void registerForPush(Context c) {
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
	}	
	
	public void updateLastActive(ParseUser u, long time) {
		u.put("lastActive", time);
		u.saveEventually();
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<String> getUsersFriends() {	
		if (!(ParseUser.getCurrentUser().get("friendsList") instanceof ArrayList<?>)) {
			return new ArrayList<String>();
		} else {
			return (ArrayList<String>) ParseUser.getCurrentUser().get("friendsList");
		}
	}
	
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
						DBAdapter db = new DBAdapter(c).open();;
						db.putFriendsList(ParseUser.getCurrentUser().getUsername(), getUsersFriends());
						db.close();									
					}
				});				
			}
		}); 
	}

	public static void getDeviceNick(ParseUser u, Context c, FindCallback cb, boolean dumpCacheFirst) {
		final ParseRelation device = u.getRelation("DeviceList");
		ParseQuery q = device.getQuery();
		if (dumpCacheFirst) {
			q.clearCachedResult();
		}
		q.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		q.whereEqualTo("DeviceID", PreferencesProvider.id(c)).findInBackground(cb);
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
		DBAdapter db = new DBAdapter(c).open();
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
	
//	public void userLastActiveAt(String email, String name) {
//		ParseQuery query = new ParseQuery("User");
//		query.whereEqualTo("username", name);
//		
//		
//		if (name != null) {
//			
//		}
//		
//		query.findInBackground(new FindCallback() {
//		    public void done(List<ParseObject> scoreList, ParseException e) {
//		        if (e == null) {
//		            Log.d("score", "Retrieved " + scoreList.size() + " scores");
//		        } else {
//		            Log.d("score", "Error: " + e.getMessage());
//		        }
//		    }
//		});
//		return null;
//	}

	public static void updateUser(Map<String, Object> opts) {
		ParseUser u = ParseUser.getCurrentUser();
		for (Map.Entry<String, Object> entry: opts.entrySet()) {
			u.put(entry.getKey(), entry.getValue());
		}
		u.saveInBackground();
		
	}
 
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
}
