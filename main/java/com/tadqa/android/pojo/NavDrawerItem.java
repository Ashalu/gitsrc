package com.tadqa.android.pojo;

public class NavDrawerItem {
	
	private String title;
	private String count = "0";
	// boolean to set visiblity of the counter
	private boolean isCounterVisible = false;


	public NavDrawerItem(String title){
		this.title = title;
	}

	
	public String getTitle(){
		return this.title;
	}
	
//	public int getIcon(){
//		return this.icon;
//	}
	
	public String getCount(){
		return this.count;
	}
	
	public boolean getCounterVisibility(){
		return this.isCounterVisible;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
//	public void setIcon(int icon){
//		this.icon = icon;
//	}

}
