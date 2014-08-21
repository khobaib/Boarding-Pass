package com.seatunity.boardingpass.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;

/**
 * Adapter for the {@link BoardingPass} list
 * @author Sumon
 *
 */
@SuppressLint("InflateParams")
public class AdapterForBoardingPass extends BaseAdapter {
	String month;
	String day;
	private Context context;
	private ArrayList<BoardingPass> list;

	/**
	 * The only constructor
	 * @param context
	 * @param item
	 */
	public AdapterForBoardingPass(Context context, ArrayList<BoardingPass> item) {
		this.context = context;
		this.list = item;
		Collections.sort(list, new Comparator<BoardingPass>() {
			public int compare(BoardingPass o1, BoardingPass o2) {
				if (o1.getJulian_date() == o2.getJulian_date())
					return 0;
				return Integer.parseInt(o1.getJulian_date()) < Integer.parseInt(o2.getJulian_date()) ? -1 : 1;
			}
		});

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
	@SuppressWarnings("unused")
	private class ViewHolder {

		TextView tv_flight_to_from;
		TextView tv_cdg;
		TextView tv_jfk;
		TextView tv_flight_date;
		TextView tv_start_time;
		TextView tv_arrival_time;
		TextView tv_seat_no;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater;

		ViewHolder holder = new ViewHolder();
		Log.e("deletestate", "" + list.get(position).getDeletestate());
		String date = Constants.getDayandYear(Integer.parseInt(list.get(position).getJulian_date()));
		String[] dateParts = date.split(":");
		month = dateParts[1];
		day = dateParts[0];
		if (convertView == null) {
			mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.boarding_pass_inflate, null);

			holder.tv_flight_to_from = (TextView) convertView.findViewById(R.id.tv_flight_to_from);
			holder.tv_cdg = (TextView) convertView.findViewById(R.id.tv_cdg);
			holder.tv_jfk = (TextView) convertView.findViewById(R.id.tv_jfk);
			holder.tv_flight_date = (TextView) convertView.findViewById(R.id.tv_flight_date);
			holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time);
			// holder.tv_flight_date=(TextView)
			// convertView.findViewById(R.id.tv_flight_date);
			holder.tv_arrival_time = (TextView) convertView.findViewById(R.id.tv_arrival_time);
			holder.tv_seat_no = (TextView) convertView.findViewById(R.id.tv_seat_no);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (list.get(position).getTravel_from_name() == null) {
			list.get(position).setTravel_from_name("");
		}
		if (list.get(position).getTravel_to_name() == null) {
			list.get(position).setTravel_to_name("");
		}
		holder.tv_flight_to_from.setText(list.get(position).getTravel_from_name() + " - "
				+ list.get(position).getTravel_to_name());

		holder.tv_flight_date.setText(month + day + ", " + list.get(position).getFlight_no());
		// holder.tv_start_time.setText(list.get(position).getDeparture());
		holder.tv_cdg.setText(list.get(position).getTravel_from());
		holder.tv_jfk.setText(list.get(position).getTravel_to());

		// holder.tv_arrival_time.setText(""+list.get(position).getArrival());
		holder.tv_seat_no.setText(context.getResources().getString(R.string.txt_seat_nno) + " "
				+ Constants.removeingprecingZero(list.get(position).getSeat()));

		return convertView;
	}

}
