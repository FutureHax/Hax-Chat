package com.t3hh4xx0r.haxchat.preferences;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailsFragmentManager {

	
	public static final int PUSH = 0;
	public static final int USER = 1;
					
	 public static Fragment newInstance(int index) { 
		 Fragment mShownFragment = null;
		 Bundle b = new Bundle();
		 switch(index){

	 		case USER:
	 			mShownFragment = new UserFragment();
		        b.putInt("index", index);
		    break;			 		
			case PUSH:
				mShownFragment = new PushFragment();
				b.putInt("index", index);
				break;
		 }
		 mShownFragment.setArguments(b);
	     return mShownFragment;
	    }
	
}
