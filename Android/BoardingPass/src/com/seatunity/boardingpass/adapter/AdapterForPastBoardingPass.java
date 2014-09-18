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
 * Adapter to show the past boarding-pass list.
 * 
 * @author Sumon
 * 
 */
public class AdapterForPastBoardingPass extends BaseAdapter {
	String month;
	String day;
	private Context context;
	private ArrayList<BoardingPass> list;

	/**
	 * The only constructor
	 * 
	 * @param context
	 * @param item
	 */
	public AdapterForPastBoardingPass(Context context, ArrayList<BoardingPass> item) {
		this.context = context;
		this.list = item;
		Collections.sort(list, new Comparator<BoardingPass>() {
			public int compare(BoardingPass o1, BoardingPass o2) {
				if (o1.getJulian_date().trim() == o2.getJulian_date().trim())
					return 0;
				return Integer.parseInt(o1.getJulian_date().trim()) < Integer.parseInt(o2.getJulian_date().trim()) ? -1
						: 1;
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
	private class ViewHolder {

		TextView tv_from;
		TextView tv_cdg;
		TextView tv_jfk;
		TextView tv_to;
		// TextView tv_start_time;
		// TextView tv_arrival_time;
		TextView tv_seat_no;
		TextView tv_flight_no;
		TextView tv_month_inside_icon;
		TextView tv_date_inside_icon;

	}

	@SuppressLint("InflateParams")
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
			convertView = mInflater.inflate(R.layout.past_boardingpass_inflate, null);

			// holder.tv_flight_to_from=(TextView)
			// convertView.findViewById(R.id.tv_flight_to_from);
			holder.tv_cdg = (TextView) convertView.findViewById(R.id.tv_cdg);
			holder.tv_jfk = (TextView) convertView.findViewById(R.id.tv_jfk);
			// holder.tv_flight_date=(TextView)
			// convertView.findViewById(R.id.tv_flight_date);
			// holder.tv_start_time=(TextView)
			// convertView.findViewById(R.id.tv_start_time);
			// // holder.tv_flight_date=(TextView)
			// convertView.findViewById(R.id.tv_flight_date);
			// holder.tv_arrival_time=(TextView)
			// convertView.findViewById(R.id.tv_arrival_time);
			holder.tv_seat_no = (TextView) convertView.findViewById(R.id.tv_seat_no);
			holder.tv_from = (TextView) convertView.findViewById(R.id.tv_from);
			holder.tv_to = (TextView) convertView.findViewById(R.id.tv_to);
			holder.tv_flight_no = (TextView) convertView.findViewById(R.id.tv_flight_no);
			holder.tv_month_inside_icon = (TextView) convertView.findViewById(R.id.tv_month_inside_icon);
			holder.tv_date_inside_icon = (TextView) convertView.findViewById(R.id.tv_date_inside_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// holder.tv_flight_to_from.setText(list.get(position).getTravel_from()+" - "+
		// list.get(position).getTravel_to());
		// holder.tv_flight_date.setText(month+day);
		// holder.tv_start_time.setText(list.get(position).getDeparture());
		// holder.tv_arrival_time.setText(""+list.get(position).getArrival());
		holder.tv_month_inside_icon.setText(month);
		holder.tv_date_inside_icon.setText(day);
		holder.tv_jfk.setText(list.get(position).getTravel_to());
		holder.tv_cdg.setText(list.get(position).getTravel_from());

		holder.tv_seat_no.setText(context.getResources().getString(R.string.txt_seat_nno) + " "
				+ Constants.removeingprecingZero(list.get(position).getSeat()));
		if (list.get(position).getTravel_from_name() != null) {
			holder.tv_from.setText(list.get(position).getTravel_from_name());
		}
		if (list.get(position).getTravel_to_name() != null) {
			holder.tv_to.setText(list.get(position).getTravel_to_name());
		}

		holder.tv_flight_no.setText(context.getResources().getString(R.string.txt_flight_no) + " "
				+ list.get(position).getCarrier() + list.get(position).getFlight_no());

		return convertView;
	}

}
