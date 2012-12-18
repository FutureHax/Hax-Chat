package com.t3hh4xx0r.haxchat.parse;

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

import com.parse.ParseUser;
import com.t3hh4xx0r.haxchat.DBAdapter;
import com.t3hh4xx0r.haxchat.R;
import com.t3hh4xx0r.haxchat.activities.ChatMainActivity;

public class ChatReceiver extends BroadcastReceiver {
	String ACTION_CHAT_UPDATE = "com.t3hh4xx0r.haxchat.ACTION_CHAT_SENT_UPDATE";

	@Override
	public void onReceive(Context c, Intent i) {	
		ParseUser user = ParseUser.getCurrentUser();
		String message = null;
		String sender;
		String time;		 
		String type;		 
		 try {
		      JSONObject json = new JSONObject(i.getExtras().getString("com.parse.Data"));
		      message = json.getString("message");
		      time = json.getString("time");
		      type = json.getString("type");
		      sender = json.getString("sender");
		      DBAdapter db = new DBAdapter(c);
			  db.open();
			  db.insertChatMessage(sender, message, time, type);
			  db.close();	
			  
			  Intent intent = new Intent();
			  intent.setAction(ACTION_CHAT_UPDATE);
			  Bundle b = new Bundle();
			  b.putString("message", message);
			  b.putString("sender", sender);
			  b.putString("time", time);
			  intent.putExtras(b);
			  c.sendOrderedBroadcast(intent, null);
			 
			  if (!sender.equals(user.getUsername())) {
				  if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 16) {
					  if (type.equals("private")) {
						notifyPrivateJellyBean(b, c);
					  } else {
						notifyJellyBean(b, c);
					  }
					} else {
						notifyPreJellyBean(b, c);
					}
			  }
		    } catch (JSONException e) {
		    	e.printStackTrace();
		    }
	}

	private void notifyPreJellyBean(Bundle b, Context c) {
		String message = b.getString("message");
		String time = b.getString("time");
		String sender = b.getString("sender");
		
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		CharSequence contentTitle = "New message from "+ sender; 
		Intent notificationIntent = new Intent(c, ChatMainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(c, 0, notificationIntent, 0);
		Notification notification = new Notification(icon, "New VIP Message!", when);
 	    notification.defaults = Notification.DEFAULT_VIBRATE;
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
			String time = b.getString("time");
			String sender = b.getString("sender");
			
		    Intent notiIntent = new Intent(c, ChatMainActivity.class);
			PendingIntent pIntent = PendingIntent.getActivity(c, 0, notiIntent, 0);
			Notification noti = new Notification.Builder(c)
			        .setContentTitle("new message")
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
			String time = b.getString("time");
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
			
			PendingIntent pSendIntent = PendingIntent.getActivity(c, 0, sendIntent, 0);
			PendingIntent pIntent = PendingIntent.getActivity(c, 0, notiIntent, 0);
//			PendingIntent pUnSubIntent = PendingIntent.getActivity(c, 0, unSubIntent, 0);

			
			Notification noti = new Notification.Builder(c)
			        .setContentTitle("new message")
			        .setContentText(messageShort)
			        .setSmallIcon(R.drawable.ic_launcher)
			        .setContentIntent(pIntent)		        
//			        .addAction(R.drawable.ic_unsub, "Unsubscribe", pUnSubIntent)
			        .addAction(R.drawable.ic_reply, "Reply", pSendIntent)
			        .setStyle(new Notification.BigTextStyle().bigText(message)).build();
			    
			  
			NotificationManager notificationManager = 
			  (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

		    noti.defaults |= Notification.DEFAULT_VIBRATE | 
		    		Notification.DEFAULT_LIGHTS |
		    		Notification.DEFAULT_SOUND;


			notificationManager.notify(0, noti); 		
		}

}