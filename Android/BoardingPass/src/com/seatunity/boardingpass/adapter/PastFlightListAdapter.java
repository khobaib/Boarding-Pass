package com.seatunity.boardingpass.adapter;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.StatusEditActivity;
import com.seatunity.boardingpass.UpdateStatusDialog;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.NavDrawerItem;

/**
 * Adapter for the navigation-drawer
 * 
 * @author Sumon
 * 
 */
public class PastFlightListAdapter extends BaseAdapter {

	private Context context;
	private List<BoardingPass> items;
	BoardingPassApplication appInstance;
	Activity activity;

	public PastFlightListAdapter(Activity activity,List<BoardingPass> list) {
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
	public BoardingPass getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		TextView tvDate,tvName,tvFromCode,tvFrom,tvToCode,tvTo,tvSeat,tvFlight;
		Button btnSeatMate,btnIdealSeatMate;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater=(LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);	;
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.row_boarding_pass, null);
			holder = new ViewHolder();
			holder.tvName=(TextView)convertView.findViewById(R.id.tv_name);
			holder.tvDate=(TextView)convertView.findViewById(R.id.tv_date);
			holder.tvFlight=(TextView)convertView.findViewById(R.id.tv_flight);
			holder.tvSeat=(TextView)convertView.findViewById(R.id.tv_seat);
			holder.tvFrom=(TextView)convertView.findViewById(R.id.tv_from);
			holder.tvFromCode=(TextView)convertView.findViewById(R.id.tv_from_code);
			holder.tvToCode=(TextView)convertView.findViewById(R.id.tv_to_code);
			holder.tvTo=(TextView)convertView.findViewById(R.id.tv_to);
			convertView.setTag(holder);			
			
		}
		else
			holder = (ViewHolder) convertView.getTag();
		
		BoardingPass boardingPass=getItem(position);
		
		holder.tvName.setText(boardingPass.getFirstname());
		holder.tvDate.setText(boardingPass.getDeparture());
		holder.tvFromCode.setText(boardingPass.getTravel_from());
		return convertView;
	}

}
