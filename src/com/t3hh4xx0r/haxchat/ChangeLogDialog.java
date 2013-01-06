package com.t3hh4xx0r.haxchat;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

public class ChangeLogDialog {

	private static String changelog_PREFIX = "changelog_";

	private static PackageInfo getPackageInfo(Activity a) {
        PackageInfo pi = null;
        try {
             pi = a.getPackageManager().getPackageInfo(a.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi; 
    }

     public static void show(final Activity a) {
        PackageInfo versionInfo = getPackageInfo(a);

		final String changelogKey = changelog_PREFIX + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(a);
        boolean hasBeenShown = prefs.getBoolean(changelogKey, false);
        if(hasBeenShown == false){
            String title = a.getString(R.string.app_name) + " v" + versionInfo.versionName;
            String message = a.getString(R.string.updates);

            AlertDialog.Builder builder = new AlertDialog.Builder(a)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(changelogKey, true);
                            editor.commit();
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							a.finish(); 
						}
                    	
                    });
            builder.create().show();
        }
    }
}
