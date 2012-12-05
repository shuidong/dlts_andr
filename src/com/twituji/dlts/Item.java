package com.twituji.dlts;

//Item.java
public class Item {
	// 道路名
	private String mTitle;
	// 交通状況
	private String mDescription;

	public Item() {
		mTitle = "";
		mDescription = "";
	}
	
	public Item(String title, String des) {
		mTitle = title;
		mDescription = des;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}
}

