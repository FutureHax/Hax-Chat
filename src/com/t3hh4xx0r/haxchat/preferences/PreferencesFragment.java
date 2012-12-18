package com.t3hh4xx0r.haxchat.preferences;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.ListView;

import com.t3hh4xx0r.haxchat.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PreferencesFragment extends PreferenceFragment {
	
	    SharedPreferences sharedPrefs;
	    SharedPreferences.Editor editor;
	    
	    boolean mDualPane;
		int mCurCheckPosition = 0;
	    
		@Override
		public void onActivityCreated (Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);
			// Check to see if we have a frame in which to embed the details
	        // fragment directly in the containing UI.
	        View detailsFrame = getActivity().findViewById(R.id.details);
	        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
	       
	        //getView().findViewById(android.R.id.list);
	        if (mDualPane) {
	            // In dual-pane mode, the list view highlights the selected item.
	        	((ListView)getView().findViewById(android.R.id.list)).setChoiceMode(ListView.CHOICE_MODE_SINGLE);	          
	        	// Make sure our UI is in the correct state.
	           showDetails(mCurCheckPosition);
	        }else{
	        	((ListView)getView().findViewById(android.R.id.list)).setChoiceMode(ListView.CHOICE_MODE_NONE);
	        }
	        	      
		}
		
		

		/**
	     * Helper function to show the details of a selected item, either by
	     * displaying a fragment in-place in the current UI, or starting a
	     * whole new activity in which it is displayed.
	     */
	    void showDetails(int index) {
	        mCurCheckPosition = index;

	        if (mDualPane) {
	        	boolean needReplace = false;
	        	Fragment details = getFragmentManager().findFragmentById(R.id.details);

	            FragmentTransaction ft = getFragmentManager().beginTransaction();


	            switch(index){
	            case DetailsFragmentManager.PUSH:		    			
    	            if (!(details instanceof PushFragment)) {
    	                // Make new fragment to show this selection.
    	                details =  DetailsFragmentManager.newInstance(index);
    	                needReplace = true;
    	            }
	    		break;	   
	            case DetailsFragmentManager.USER:		    			
    	            if (!(details instanceof UserFragment)) {
    	                // Make new fragment to show this selection.
    	                details =  DetailsFragmentManager.newInstance(index);
    	                needReplace = true;
    	            }
	    		break;	   
	            }
	          
                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
	            if(needReplace){
	            	ft.replace(R.id.details, details);
                	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                	ft.commit();
                }

	        } else {
	        	Intent intent = new Intent();
	        	intent.putExtra("index", index);
		        intent.setClass(getActivity(), SingleFragmentActivity.class);                
		        startActivity(intent);
	         }
	    }
	    
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.preferences);
	   }
	   
	   @Override
	   public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {			
			String key = preference.getKey();	
			if(key.equals("push")){
				showDetails(DetailsFragmentManager.PUSH);
			} else if (key.equals("user")) {
				showDetails(DetailsFragmentManager.USER);
			}
		    return false;
		}   
}

