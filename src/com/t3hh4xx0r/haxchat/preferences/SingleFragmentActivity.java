package com.t3hh4xx0r.haxchat.preferences;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;


public class SingleFragmentActivity extends Activity {

	

    private static final String TAG = SingleFragmentActivity.class.getSimpleName();

	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent t = getIntent();
        Bundle b = t.getExtras();
    
        int index = b.getInt("index");

        Fragment container = DetailsFragmentManager.newInstance(index); 
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(android.R.id.content, container);
        fragmentTransaction.commit(); 
    }    	
}