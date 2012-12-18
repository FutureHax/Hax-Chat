package com.t3hh4xx0r.haxchat.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.StrictMode;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
			((ArrayList<String>) o).add(user.getUsername());
			
			ParseUser.getCurrentUser().put("friendsList", o);
			ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {			
				@Override
				public void done(ParseException e) {
					if (e != null) {
						e.printStackTrace();
						return;
					}
					DBAdapter db = new DBAdapter(c).open();;
					db.putFriendsList(ParseUser.getCurrentUser().getUsername(), getUsersFriends());
					db.close();				
				}
			});
		}
//		ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {			
//			@Override
//			public void done(ParseException e) {
//				DBAdapter db = new DBAdapter(c).open();;
//				db.putFriendsList(ParseUser.getCurrentUser().getUsername(), getUsersFriends());
//				db.close();				
//			}
//		});
//		ParseObject fList = (ParseObject) ParseUser.getCurrentUser().get("friendsList");
//		fList.addUnique("friendsList", user.getUsername());
//		fList.saveInBackground(new SaveCallback() {
//			@Override
//			public void done(ParseException e) {
//				if (e == null) {
//					DBAdapter db = new DBAdapter(c).open();;
//					db.putFriendsList(ParseUser.getCurrentUser().getUsername(), getUsersFriends());
//					db.close();
//				}
//			}
//		});

	}

	public static ArrayList<String> getCachedUsersFriends(Context c) {	
		DBAdapter db = new DBAdapter(c).open();
		ArrayList<String> res = db.getFriendsList();
		db.close();		
		return res;
	}
	
	public static ArrayList<String> getAllUsers() throws ParseException {		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		ArrayList<String> userNames = new ArrayList<String>();
		List<ParseObject> users = ParseUser.getQuery().find();
		for (int i=0;i<users.size();i++) {
			//THis works. Get a list of full arseuser objects this way
			//ParseUser u1 = (ParseUser) users.get(i);
			userNames.add(users.get(i).getString("username"));
		}
		return userNames;
	}

	public static void refresh() {
		ParseUser.getCurrentUser().refreshInBackground(new RefreshCallback() {
			@Override
			public void done(ParseObject arg0, ParseException e) {
				if (e == null) {
					//good
				}
			}
		});
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
