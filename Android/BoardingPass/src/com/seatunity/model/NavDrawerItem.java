package com.seatunity.model;

/**
 * Model of a navigation-drawer item.
 * 
 * @author Sumon
 * 
 */
public class NavDrawerItem {

	private String title;
	private int icon;
	private String count = "0";
	/** Flag to set visiblity of the counter */
	private boolean isCounterVisible = false;

	public NavDrawerItem() {
	}

	/**
	 * Constructor leaving {@code isCounterVisible} flag to false & count="0"
	 * 
	 * @param title
	 * @param icon
	 */
	public NavDrawerItem(String title, int icon) {
		this.title = title;
		this.icon = icon;
	}

	/**The most-explicit constructor.
	 * NB: Counter is still unused.
	 * 
	 * @param title
	 * @param icon
	 * @param isCounterVisible true if the counter is to show, false otherwise.
	 * @param count
	 */
	public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count) {
		this.title = title;
		this.icon = icon;
		this.isCounterVisible = isCounterVisible;
		this.count = count;
	}

	public String getTitle() {
		return this.title;
	}

	public int getIcon() {
		return this.icon;
	}

	public String getCount() {
		return this.count;
	}

	public boolean getCounterVisibility() {
		return this.isCounterVisible;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

//	public void setCount(String count) {
//		this.count = count;
//	}
//
//	public void setCounterVisibility(boolean isCounterVisible) {
//		this.isCounterVisible = isCounterVisible;
//	}
}
