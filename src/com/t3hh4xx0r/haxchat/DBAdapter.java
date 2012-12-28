package com.t3hh4xx0r.haxchat;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

    private final Context context; 
    
    private static DatabaseHelper DBHelper;
    public SQLiteDatabase db;
    
    private static final String CREATE_CHAT =
            "create table chat (_id integer primary key autoincrement, "
            + "sender text not null, message text not null, sent_time text not null, type text not null, "
            + "unique(sender, message, sent_time) on conflict ignore);";
    
    private static final String CREATE_USER_CACHE =
            "create table user_cache (_id integer primary key autoincrement, "
            + "name text not null, friends text not null);";
    
    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, "hax_chat.db", null, 3);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(CREATE_CHAT);
        	db.execSQL(CREATE_USER_CACHE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
        int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS chat");
            db.execSQL("DROP TABLE IF EXISTS user_cache");
            onCreate(db);
        }
    }    
    
    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
    	DBHelper.close();
    }	
    
    public void dropChats() {
        db.execSQL("DROP TABLE IF EXISTS chat");
    	db.execSQL(CREATE_CHAT);
    }
	
	public void putFriendsList(String user, ArrayList<String> fList) {
        db.execSQL("DROP TABLE IF EXISTS user_cache");
    	db.execSQL(CREATE_USER_CACHE);
    	
    	ContentValues values = new ContentValues();
    	values.put("name", user);
    	StringBuilder sB = new StringBuilder();
    	for (int i=0;i<fList.size();i++) {
    		sB.append(fList.get(i));
    		sB.append(",");
    	}
    	values.put("friends", sB.toString());
        db.insert("user_cache", null, values);      		
	}
	
	public void updateFriendsList(String user, ArrayList<String> fList) {
        db.execSQL("DROP TABLE IF EXISTS user_cache");
    	db.execSQL(CREATE_USER_CACHE);

		ContentValues values = new ContentValues();
    	values.put("name", user);
    	StringBuilder sB = new StringBuilder();
    	for (int i=0;i<fList.size();i++) {
    		sB.append(fList.get(i));
    		sB.append(",");
    	}
    	values.put("friends", sB.toString());
        db.insert("user_cache", null, values);      		
	}
	
	public ArrayList<String> getFriendsList() {
		ArrayList<String> res = new ArrayList<String>();
		Cursor mCursor = db.query("user_cache", new String[] {
    			"friends"}, 
        		null, 
        		null, 
                null, 
                null, 
                null, 
                null);	
		
		mCursor.moveToFirst();
		String fString = mCursor.getString(mCursor.getColumnIndex("friends"));
		String[] list = fString.split(",");
		for (int i=0;i<list.length;i++) {
			res.add(list[i]);
		}
		return res;
	}
	
	public void insertChatMessage(String user, String message, String sent_time, String type) {
		ContentValues values = new ContentValues();
    	values.put("sender", user);
    	values.put("message", message);
    	values.put("sent_time", sent_time);
    	values.put("type", type);
        db.insert("chat", null, values);      		
	}
	
	public Cursor getChats() {
		Cursor mCursor = db.query("chat", new String[] {
        			"sender", 
        			"sent_time", 
	        		"message"}, 
	        		"type = ?", 
	        		new String[] {"public"}, 
	                null, 
	                null, 
	                null, 
	                null);	
			return mCursor;
	}
	
	public Cursor getPrivateChatList() {
		Cursor mCursor = db.query("chat", new String[] {
    			"sender", 
    			"sent_time", 
        		"message"}, 
        		"type = ?", 
        		new String[] {"private"}, 
                null, 
                null, 
                null, 
                null);	
		return mCursor;
	}
	
	public Cursor getPrivateChatsForUser(String name) {
		Cursor mCursor = db.query("chat", new String[] {
    			"sender", 
    			"sent_time", 
        		"message"}, 
        		"type = ? AND sender = ?", 
        		new String[] {"private", name}, 
                null, 
                null, 
                null, 
                null);	
		return mCursor;
	}
}
