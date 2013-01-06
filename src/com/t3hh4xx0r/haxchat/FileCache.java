package com.t3hh4xx0r.haxchat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"HaxChat");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
        
        Log.d("CACHE DIR IS", cacheDir.toString());
    }
    
    public Bitmap getBitmap(String userName, boolean small){
        String filename = small ? String.valueOf(userName.hashCode()) : String.valueOf(userName.hashCode())+"_large";
        File f = new File(cacheDir, filename);
        Bitmap b = decodeFile(f);
        if (b != null) {
        	return b;
        } else {
        	return null;
        }
        
    }
    
    public void putBitmap(Bitmap b, String uName, boolean small) {
    	if (small) {  
    		putScaledBitmap(b, uName);    		
    	} else {
    		putNonScaledBitmap(b, uName);
    	}
    }
    
    private void putNonScaledBitmap(Bitmap b, String uName) {
		try {
	        String filename = "/"+uName+"-"+System.currentTimeMillis()+"_large";
	        
	        File file = new File(cacheDir, filename);
	        FileOutputStream fOut = new FileOutputStream(file);
	        b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
	        fOut.flush();
	        fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	      		
	}

	private void putScaledBitmap(Bitmap b, String uName) {
		try {
			int h = 100; 
			int w = 100;  
			Bitmap scaled = Bitmap.createScaledBitmap(b, h, w, true);
	        String filename = "/"+uName+"-"+System.currentTimeMillis();
	        
	        File file = new File(cacheDir, filename);
	        FileOutputStream fOut = new FileOutputStream(file);
	        scaled.compress(Bitmap.CompressFormat.PNG, 100, fOut);
	        fOut.flush();
	        fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	      		
	}

	private Bitmap decodeFile(File f){
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}