package com.seatunity.boardingpass.adapter;
import java.util.ArrayList;
import java.util.Arrays;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.seatunity.boardingpass.EditUserNameActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Adapter for the navigation-drawer
 * 
 * @author Sumon
 *
 */
public class NavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> item;
	BoardingPassApplication appInstance;
	Activity activity;

	/**
	 * The only constructor
	 * @param context
	 * @param appInstance
	 */
	public NavDrawerListAdapter(Activity activity,Context context,BoardingPassApplication appInstance){
		this.context = context;
		this.appInstance=appInstance;

		String[] class_list = context.getResources().getStringArray(R.array.nav_item);
		item = new ArrayList<String>(Arrays.asList(class_list));

		this.activity=activity;

	}

	@Override
	public int getCount() {
		return item.size();
	}

	@Override
	public Object getItem(int position) {       
		return item.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater;
		if (convertView == null) {
			mInflater = (LayoutInflater)
					context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
		}

		ImageView img_item_icon = (ImageView) convertView.findViewById(R.id.img_item_icon);
		TextView tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);
		tv_item_name.setText(item.get(position));

		switch (position) {
		case 0:
			img_item_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.home_pressed));
			//img_item_icon.setBackgroundResource(R.drawable.home_presses);
			break;
		case 1:
			img_item_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.account_pressed));

			break;
		case 2:
			img_item_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.pastflight_pressed));

			break;
		case 3:
			img_item_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.seattingpref_pressed));
			break;
		case 4:
			img_item_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.premiar_pressed));
			

			break;

		default:
			break;
		}
		return convertView;
	}

}
