package com.seatunity.boardingpass.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.StatusEditActivity;
import com.seatunity.boardingpass.UpdateStatusDialog;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.model.NavDrawerItem;

/**
 * Adapter for the navigation-drawer
 * 
 * @author Sumon
 * 
 */
public class NavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<NavDrawerItem> items;
	BoardingPassApplication appInstance;
	Activity activity;

	public NavDrawerListAdapter(Activity activity,ArrayList<NavDrawerItem> list) {
		this.context = (Context)activity;
		this.appInstance = (BoardingPassApplication)activity.getApplication();
		this.items=list;
		this.activity = activity;

	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public NavDrawerItem getItem(int position) {
		return items.get(position);
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
			mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if(position==0)
				convertView = mInflater.inflate(R.layout.row_nav_menu_1, null);
			else
					convertView = mInflater.inflate(R.layout.row_nav_menu, null);
		
		}

		ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		TextView tvUserName = (TextView) convertView.findViewById(R.id.user_name);
		TextView tvEmail = (TextView) convertView.findViewById(R.id.email);
		
		ImageView img_edit_profile = (ImageView) convertView.findViewById(R.id.img_edit_profile);

		if (position == 0) {
			img_edit_profile.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.i("STATUS EDIT", "showing edit status");
					// DialogViewer.toastSimple(activity,
					// "showing edit status");
					//Intent intent = new Intent((Context) activity, UpdateStatusDialog.class);
					Intent intent = new Intent((Context) activity, StatusEditActivity.class);
					activity.startActivity(intent);
				}
			});
			if (appInstance.getUserCred().getImage_url().equals("")) {
				imgIcon.setImageResource(R.drawable.ic_contact_picture);
			} else {
				ImageLoader.getInstance().displayImage(appInstance.getUserCred().getImage_url(), imgIcon);

			}
			if (appInstance.getUserCred().getEmail().equals("")) {
				tvEmail.setText(context.getResources().getText(R.string.txt_email_addess));
			} else {
				tvEmail.setText(appInstance.getUserCred().getEmail());
			}
			if (appInstance.getUserCred().getFirstname().equals("")) {
				tvUserName.setText(context.getResources().getText(R.string.txt_user_name));
				tvEmail.setText("Status");
			} else {
				tvUserName.setText(appInstance.getUserCred().getFirstname());
				tvEmail.setText(appInstance.getUserCred().getStatus());
			}
		} else {
			img_edit_profile.setVisibility(View.GONE);
		
			tvEmail.setVisibility(View.GONE);

			tvUserName.setText(getItem(position).getTitle());
			imgIcon.setImageResource(getItem(position).getIcon());
		}
		return convertView;
	}

}
