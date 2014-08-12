package com.seatunity.boardingpass.adapter;

import com.seatunity.boardingpass.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyNavigationAdapter extends BaseAdapter {
	Context mContext = null;
	String[] mTitles;
	LayoutInflater mLayOutInflater = null;

	public MyNavigationAdapter(Context context, String[] titles) {
		this.mContext = context;
		this.mTitles = titles;
		mLayOutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mTitles.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = mLayOutInflater.inflate(R.layout.my_list_custom_row, parent,false);
		TextView rowName = (TextView) view.findViewById(R.id.item);
		rowName.setText(mTitles[position]);
		rowName.setTextColor(Color.RED);// yo

		if(position%2==0)
			rowName.setBackgroundColor(Color.RED);
		else
			rowName.setBackgroundColor(Color.BLUE);
		return view;
	}

}