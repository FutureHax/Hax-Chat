package com.t3hh4xx0r.haxchat;

import java.util.ArrayList;

public class ExpandListGroup {
 
	private String Name;
	private ArrayList<ExpandListChild> Items;
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		this.Name = name;
	}
	public ArrayList<ExpandListChild> getItems() {
		return Items;
	}
	public void setItems(ArrayList<ExpandListChild> Items) {
		this.Items = Items;
	}
	
	
}

