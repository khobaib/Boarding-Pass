package com.seatunity.boardingpass.adapter;

import java.util.ArrayList;

import com.seatunity.boardingpass.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @deprecated Never used
 *
 */
@SuppressLint({ "ViewHolder", "InflateParams" })
public class AdapterBaseMaps extends BaseAdapter {

	Context context;
	//int layoutResourceId;
	ArrayList<String> data;
	LayoutInflater inflater;

	/**
	 * @param context
	 * @param data
	 */
	public AdapterBaseMaps(Context context,
			ArrayList<String> data) {
		// super(a, textViewResourceId, data);
		this.data = data;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		//this.layoutResourceId = textViewResourceId;

	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View actionBarView = inflater.inflate(R.layout.ab_main_view, null);
		TextView title = (TextView) actionBarView
				.findViewById(R.id.ab_basemaps_title);
		TextView subtitle = (TextView) actionBarView
				.findViewById(R.id.ab_basemaps_subtitle);
		title.setText(context.getResources().getString(R.string.txt_seatmate));
		subtitle.setText(data.get(position));
		return actionBarView;

	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
	    View actionBarDropDownView = inflater.inflate(
	            R.layout.ab_dropdown_view, null);
	    TextView dropDownTitle = (TextView) actionBarDropDownView
	            .findViewById(R.id.ab_basemaps_dropdown_title);

	    dropDownTitle.setText(data.get(position));

	    return actionBarDropDownView;

	}


	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}