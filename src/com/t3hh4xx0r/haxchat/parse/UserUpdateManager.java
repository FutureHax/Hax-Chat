package com.t3hh4xx0r.haxchat.parse;

import java.util.Date;

import android.content.Context;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class UserUpdateManager {
	Context ctx;
	ParseUser u;
	static boolean locked = false;
	static Date cachedLastActive;
	
	public UserUpdateManager(Context c) {
		ctx = c;
		u = ParseUser.getCurrentUser();
	}
	
	public void updateUserLastActive() {
		Date lastActive = new Date();
		try {
			lastActive = ParseHelper.getDateInGMT();
		} catch (java.text.ParseException e) {}
		if (!locked) {
			locked = true;
			u.put("lastActive", lastActive);
			u.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException arg0) {
					locked = false;
				}
			});
		} else {
			cachedLastActive = lastActive;
		}
	}
}
