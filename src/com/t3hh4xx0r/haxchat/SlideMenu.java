package com.t3hh4xx0r.haxchat;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.t3hh4xx0r.haxchat.activities.ChatMainActivity;
import com.t3hh4xx0r.haxchat.activities.ChatPrivateActivity;

public class SlideMenu extends LinearLayout {
	
    private static final int LOW_DPI_STATUS_BAR_HEIGHT = 19;
    private static final int MEDIUM_DPI_STATUS_BAR_HEIGHT = 25;
    private static final int HIGH_DPI_STATUS_BAR_HEIGHT = 38;
    private static final int XHIGH_DPI_STATUS_BAR_HEIGHT = 50;
    private static final int TV_DPI_STATUS_BAR_HEIGHT = 34;


	public static class SlideMenuItem {
		public Drawable icon;
		public String label;
	}
	
	// a simple adapter
	public static class SlideMenuAdapter extends ArrayAdapter<SlideMenuItem> {
		Activity act;
		SlideMenuItem[] items;
		Typeface itemFont;
		
		class MenuItemHolder {
			public TextView label;
			public ImageView icon;
		}
		
		public SlideMenuAdapter(Activity act, SlideMenuItem[] items, Typeface itemFont) {
			super(act, R.id.menu_label, items);
			this.act = act;
			this.items = items;
			this.itemFont = itemFont;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = act.getLayoutInflater();
				rowView = inflater.inflate(R.layout.slidemenu_listitem, null);
				MenuItemHolder viewHolder = new MenuItemHolder();
				viewHolder.label = (TextView) rowView.findViewById(R.id.menu_label);
				if(itemFont != null) 
					viewHolder.label.setTypeface(itemFont);
				viewHolder.icon = (ImageView) rowView.findViewById(R.id.menu_icon);
				rowView.setTag(viewHolder);
			}

			MenuItemHolder holder = (MenuItemHolder) rowView.getTag();
			String s = items[position].label;
			holder.label.setText(s);
			holder.icon.setImageDrawable(items[position].icon);

			return rowView;
		}
	}
	
	private boolean menuShown = false;
	private int statusHeight = -1;
	private static View menu;
	private static ViewGroup content;
	private static FrameLayout parent;
	private static int menuSize;
	private Activity act;
	private Typeface font;
	View searchET;
	int toggleId;
	private TranslateAnimation slideRightAnim;
	private TranslateAnimation slideMenuLeftAnim;
	private TranslateAnimation slideContentLeftAnim;
	
	private ArrayList<SlideMenuItem> menuItemList;
	
	/**
	 * Constructor used by the inflation apparatus.
	 * To be able to use the SlideMenu, call the {@link #init init()} method.
	 * @param context
	 */
	public SlideMenu(Context context) {
		super(context);
	}
	
	/**
	 * Constructor used by the inflation apparatus.
	 * To be able to use the SlideMenu, call the {@link #init init()} method.
	 * @param attrs
	 */
	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	/** 
	 * If inflated from XML, initializes the SlideMenu.
	 * @param act The calling activity.
	 * @param menuResource Menu resource identifier, can be 0 for an empty SlideMenu.
	 * @param cb Callback to be invoked on menu item click.
	 * @param slideDuration Slide in/out duration in milliseconds.
	 */
	public void init(Context act) {
		if (this.isInEditMode()) {
			return;
		}
		
		this.act = (Activity) act;
		this.toggleId = android.R.id.home;
	
		// set size
	    Display display = this.act.getWindowManager().getDefaultDisplay();
	    DisplayMetrics displayMetrics = new DisplayMetrics();
	    display.getMetrics(displayMetrics);

	    menuSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, isHuge(this.act) ?  400 : 260, act.getResources().getDisplayMetrics());
		
		// create animations accordingly
		slideRightAnim = new TranslateAnimation(-menuSize, 0, 0, 0);
		slideRightAnim.setDuration(222);
		slideRightAnim.setFillAfter(true);
		slideMenuLeftAnim = new TranslateAnimation(0, -menuSize, 0, 0);
		slideMenuLeftAnim.setDuration(222);
		slideMenuLeftAnim.setFillAfter(true);
		slideContentLeftAnim = new TranslateAnimation(menuSize, 0, 0, 0);
		slideContentLeftAnim.setDuration(222);
		slideContentLeftAnim.setFillAfter(true);
		
	}
	
	/**
	 * Optionally sets the font for the menu items.
	 * @param f A font.
	 */
	public void setFont(Typeface f) {
		font = f;
	}	

	/**
	 * Slide the menu in.
	 */
	public void show() {
		this.show(true);
	}

	/**
	 * Set the menu to shown status without displaying any slide animation. 
	 */
	public void setAsShown() {
		this.show(false);
	}
	
	public void toggle() {
		if (isMenuShown()) {
			this.hide();
		} else {
			this.show();
		}
	}
	private void show(boolean animate) {
		/*
		 *  We have to adopt to status bar height in most cases,
		 *  but not if there is a support actionbar!
		 */
		setupMenu();

		try {
			Method getSupportActionBar = act.getClass().getMethod("getSupportActionBar", (Class[])null);
			Object sab = getSupportActionBar.invoke(act, (Object[])null);
			sab.toString(); // check for null

			if (android.os.Build.VERSION.SDK_INT >= 11) {
				// over api level 11? add the margin
				getStatusbarHeight();
			}
		} catch(Exception es) {
			// there is no support action bar!
			getStatusbarHeight();
		}
		
		// modify content layout params
		try {
			content = ((LinearLayout) act.findViewById(android.R.id.content).getParent());
		}
		catch(ClassCastException e) {
			/*
			 * When there is no title bar (android:theme="@android:style/Theme.NoTitleBar"),
			 * the android.R.id.content FrameLayout is directly attached to the DecorView,
			 * without the intermediate LinearLayout that holds the titlebar plus content.
			 */
			content = (FrameLayout) act.findViewById(android.R.id.content);
		}
		FrameLayout.LayoutParams parm = new FrameLayout.LayoutParams(-1, -1, 3);
		parm.setMargins(menuSize, 0, -menuSize, 0);
		content.setLayoutParams(parm);
		
		// animation for smooth slide-out
		if(animate)
			content.startAnimation(slideRightAnim);
		
		// add the slide menu to parent
		parent = (FrameLayout) content.getParent();
		LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		menu = inflater.inflate(R.layout.slidemenu, null);
		FrameLayout.LayoutParams lays = new FrameLayout.LayoutParams(-1, -1, 3);
		lays.setMargins(0, statusHeight, 0, 0);
		menu.setLayoutParams(lays);
		parent.addView(menu, parent.getChildCount()-1);

		
		// connect the menu's listview
		ListView list = (ListView) act.findViewById(R.id.menu_listview);
		SlideMenuItem[] items = menuItemList.toArray(new SlideMenuItem[menuItemList.size()]);
		final SlideMenuAdapter adap = new SlideMenuAdapter(act, items, font);
		list.setAdapter(adap);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startSubActivity(position, view.getContext());
			}
		});
		if(animate) {
			menu.startAnimation(slideRightAnim);
		}

		menuShown = true;
	}

	public void hide() {
		content.startAnimation(slideContentLeftAnim);
		menu.startAnimation(slideMenuLeftAnim);
		parent.removeView(menu);
		FrameLayout.LayoutParams parm = (FrameLayout.LayoutParams) content.getLayoutParams();
		parm.setMargins(0, 0, 0, 0);
		content.setLayoutParams(parm);

		menuShown = false;
	}
	
	public void getStatusbarHeight() {
		if(statusHeight == -1) {
			DisplayMetrics displayMetrics = new DisplayMetrics();
	    	((WindowManager) this.act.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

	    	if (isHuge(this.act)) {
	    		return;
	    	}
	    	
	    	switch (displayMetrics.densityDpi) {
	    	    case DisplayMetrics.DENSITY_HIGH:
	    	    	statusHeight = HIGH_DPI_STATUS_BAR_HEIGHT;
	    	        break;
	    	    case DisplayMetrics.DENSITY_MEDIUM:
	    	    	statusHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
	    	        break;
	    	    case DisplayMetrics.DENSITY_LOW:
	    	    	statusHeight = LOW_DPI_STATUS_BAR_HEIGHT;
	    	        break;
	    	    case DisplayMetrics.DENSITY_TV:
	    	    	statusHeight = TV_DPI_STATUS_BAR_HEIGHT;
	    	    	break;
	    	    case DisplayMetrics.DENSITY_XHIGH:
	    	    	statusHeight = XHIGH_DPI_STATUS_BAR_HEIGHT;
	    	    	break;
	    	    default:
	    	    	statusHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
	    	}
		}
	}
	
	static boolean isHuge(Activity a) {
	    Display display = a.getWindowManager().getDefaultDisplay();
	    DisplayMetrics displayMetrics = new DisplayMetrics();
	    display.getMetrics(displayMetrics);

	    int width = displayMetrics.widthPixels / displayMetrics.densityDpi;
	    int height = displayMetrics.heightPixels / displayMetrics.densityDpi;

	    double screenDiagonal = Math.sqrt( width * width + height * height );
	    return (screenDiagonal >= 8 );
	}
		
	private void setupMenu() {
		menuItemList = new ArrayList<SlideMenuItem>();
		SlideMenuItem pubItem = new SlideMenuItem();
		pubItem.icon = act.getResources().getDrawable(R.drawable.ic_launcher);
		pubItem.label = "Public Chat";
		menuItemList.add(pubItem);
		
		ArrayList<String> privateList = new ArrayList<String>();
		DBAdapter db = new DBAdapter(act);
		db.open();
		Cursor c = db.getPrivateChatList();
		while (c.moveToNext()) {
			if (privateList.contains(c.getString(c.getColumnIndex("sender")))) {				
				continue;
			}
			privateList.add(c.getString(c.getColumnIndex("sender")));
			
			SlideMenuItem item = new SlideMenuItem();
			item.icon = act.getResources().getDrawable(R.drawable.ic_launcher);
			item.label = c.getString(c.getColumnIndex("sender"));
			menuItemList.add(item);
		}
		db.close();
	}
	
	public boolean isMenuShown() {
		return menuShown;
	}	
	
	protected void startSubActivity(int position, Context cz) {
		Intent i;
		if (position != 0) {
			i = new Intent(cz, ChatPrivateActivity.class);
			i.putExtra("user", menuItemList.get(position).label);
		} else {
			i = new Intent(cz, ChatMainActivity.class);
		}
		cz.startActivity(i);
		((Activity) cz).finish();
	}
	
}
