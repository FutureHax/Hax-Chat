package com.t3hh4xx0r.haxchat.parse;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.t3hh4xx0r.haxchat.DBAdapter;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.activities.ChatMainActivity;
import com.t3hh4xx0r.haxchat.activities.ChatPrivateActivity;

public class ChatReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context c, Intent i) {	
		String message = null;
		final String sender;
		String time;		 
		final String type;		 
		try {
			JSONObject json = new JSONObject(i.getExtras().getString("com.parse.Data"));
			message = json.getString("message");
			time = json.getString("time");
			type = json.getString("type");
			sender = json.getString("sender");
			DBAdapter db = new DBAdapter(c);
			db.open(true);
			db.insertChatMessage(sender, message, time, type);
			db.close();	
			
			Intent intent = new Intent();
			intent.setAction(ParseHelper.ACTION_CHAT_UPDATE);
			final Bundle b = new Bundle();
			b.putString("message", message);
			b.putString("sender", sender);
			b.putString("time", time);
			intent.putExtras(b);
			c.sendOrderedBroadcast(intent, null);
			if (!sender.equals(ParseHelper.getDeviceNick(c, null, false))) {
				if (type.equals("private")) {
					if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
						notifyPrivateJellyBean(b, c);
					} else {
						notifyPrivatePreJellyBean(b, c);
					}
				} else {
					if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
						notifyJellyBean(b, c);
					} else {
						notifyPreJellyBean(b, c);
					}
				}
			}
			ParseHelper.getDeviceNick(c, new FindCallback() {
				@Override
				public void done(List<ParseObject> r, com.parse.ParseException e) {						
					if (e == null) {
						if (!sender.equals(r.get(0).get("DeviceNick"))) {
							if (type.equals("private")) {
								if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
									notifyPrivateJellyBean(b, c);
								} else {
									notifyPrivatePreJellyBean(b, c);
								}
							} else {
								if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
									notifyJellyBean(b, c);
								} else {
									notifyPreJellyBean(b, c);
								}
							}
						}
					} else {
						e.printStackTrace();
					}
				}
			}, false);				  
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private void notifyPreJellyBean(Bundle b, Context c) {
		String message = b.getString("message");
		String sender = b.getString("sender");
		
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		CharSequence contentTitle = "New message from "+ sender; 
		Intent notificationIntent = new Intent(c, ChatMainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(c, 0, notificationIntent, 0);
		Notification notification = new Notification(icon, "New Message!", when);
		notification.defaults |= Notification.DEFAULT_VIBRATE | 
	    		Notification.DEFAULT_LIGHTS |
	    		Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(c, contentTitle, message, contentIntent);
		final int HELLO_ID = 1;
		NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(
	                Context.NOTIFICATION_SERVICE);	
		mNotificationManager.notify(HELLO_ID, notification);			
	}
	
	@SuppressWarnings("deprecation")
	private void notifyPrivatePreJellyBean(Bundle b, Context c) {
		String message = b.getString("message");
		String sender = b.getString("sender");
		
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		CharSequence contentTitle = "New private message from "+ sender; 
		Intent notificationIntent = new Intent(c, ChatPrivateActivity.class);
		Bundle chatBundle = new Bundle();
		chatBundle.putString("user", sender);
		notificationIntent.putExtras(chatBundle);
		PendingIntent contentIntent = PendingIntent.getActivity(c, 0, notificationIntent, 0);
		Notification notification = new Notification(icon, "New Message!", when);
		notification.defaults |= Notification.DEFAULT_VIBRATE | 
	    		Notification.DEFAULT_LIGHTS |
	    		Notification.DEFAULT_SOUND;
	    notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(c, contentTitle, message, contentIntent);
		final int HELLO_ID = 1;
		NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(
	                Context.NOTIFICATION_SERVICE);	
		mNotificationManager.notify(HELLO_ID, notification);			
	}
	
	  @TargetApi(16)
	  private void notifyJellyBean(Bundle b, Context c) {
			String message = b.getString("message");
			String messageShort = null;
			if (message.length() > 25) {
				messageShort = b.getString("message").substring(0, 24);
			}
			String sender = b.getString("sender");
			
		    Intent notiIntent = new Intent(c, ChatMainActivity.class);
			PendingIntent pIntent = PendingIntent.getActivity(c, 0, notiIntent, 0);
			Notification noti = new Notification.Builder(c)
			        .setContentTitle("New message from "+ sender)
			        .setContentText(messageShort)
			        .setSmallIcon(R.drawable.ic_launcher)
			        .setContentIntent(pIntent)		        
			        .setStyle(new Notification.BigTextStyle().bigText(message)).build();
			    
			  
			NotificationManager notificationManager = 
			  (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

		    noti.defaults |= Notification.DEFAULT_VIBRATE | 
		    		Notification.DEFAULT_LIGHTS |
		    		Notification.DEFAULT_SOUND;


			notificationManager.notify(0, noti); 		
		}
	  
	  @TargetApi(16)
	  private void notifyPrivateJellyBean(Bundle b, Context c) {
		  String message = b.getString("message");
			String messageShort = null;
			if (message.length() > 25) {
				messageShort = b.getString("message").substring(0, 24);
			}
			String sender = b.getString("sender");
			
		    Intent notiIntent = new Intent(c, ChatMainActivity.class);
			//Intent unSubIntent = new Intent(c, ParseUnsubscribeConfirmActivity.class);
			
			Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
			sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "r2doesinc@gmail.com" });
			sendIntent.setData(Uri.parse("r2doesinc@gmail.com"));
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, "LAME");
			sendIntent.setType("plain/text");
//			
//			b.putInt("channel", parseChannel(datas.get("channel")));
//			unSubIntent.putExtras(b);
			
//			PendingIntent pSendIntent = PendingIntent.getActivity(c, 0, sendIntent, 0);
			PendingIntent pIntent = PendingIntent.getActivity(c, 0, notiIntent, 0);
//			PendingIntent pUnSubIntent = PendingIntent.getActivity(c, 0, unSubIntent, 0);

			
			Notification noti = new Notification.Builder(c)
			        .setContentTitle("New private message from "+ sender)
			        .setContentText(messageShort)
			        .setSmallIcon(R.drawable.ic_launcher)
			        .setContentIntent(pIntent)		        
//			        .addAction(R.drawable.ic_unsub, "Unsubscribe", pUnSubIntent)
//			        .addAction(R.drawable.ic_reply, "Reply", pSendIntent)
			        .setStyle(new Notification.BigTextStyle().bigText(message)).build();
			    
			  
			NotificationManager notificationManager = 
			  (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

		    noti.defaults |= Notification.DEFAULT_VIBRATE | 
		    		Notification.DEFAULT_LIGHTS |
		    		Notification.DEFAULT_SOUND;


			notificationManager.notify(0, noti); 		
		}

}