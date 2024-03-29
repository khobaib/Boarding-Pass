package com.seatunity.boardingpass.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.seatunity.boardingpass.R;
import com.seatunity.model.UserCred;

/**
 * Adapter for the list of the settings menu
 * 
 * @author Sumon
 * 
 */
public class AdapterForSettings extends BaseAdapter {
	String month;
	String day;
	private Context context;
	private ArrayList<String> list;
	UserCred userCred;

	/**
	 * The only construcotr
	 * 
	 * @param context
	 *            working context
	 * @param list
	 *            the list to show in the view
	 * @param userCred
	 *            The {@link UserCred} object from the saved-preferences to show
	 *            user-values in the view.
	 */
	public AdapterForSettings(Context context, ArrayList<String> list, UserCred userCred) {
		this.context = context;
		this.list = list;
		this.userCred = userCred;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Class holding all the views of a single object in the list.
	 * 
	 * @author Sumon
	 * 
	 */
	private class ViewHolder {
		TextView tv_seeting_criteria;
		TextView tv_info;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater;
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.setting_inflate, null);

			holder.tv_seeting_criteria = (TextView) convertView.findViewById(R.id.tv_seeting_criteria);
			holder.tv_info = (TextView) convertView.findViewById(R.id.tv_info);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == 0) {
			holder.tv_info.setText(userCred.getLive_in());
		} else if (position == 1) {
			holder.tv_info.setText(userCred.getAge());
		} else if (position == 2) {
			holder.tv_info.setText(userCred.getGender());
		} else if (position == 3) {
			holder.tv_info.setText(userCred.getProfession());
		} else if (position == 4) {
			holder.tv_info.setText(userCred.getSeating_pref());
		} else if (position == 5) {
			holder.tv_info.setText(userCred.getSomethinAbout());
		} else if (position == 5) {
			holder.tv_info.setText(userCred.getSomethinAbout());
		} else {
			holder.tv_info.setText("");
		}

		holder.tv_seeting_criteria.setText(list.get(position));
		return convertView;
	}

}
