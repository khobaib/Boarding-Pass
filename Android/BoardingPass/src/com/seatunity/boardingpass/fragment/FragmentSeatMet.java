
package com.seatunity.boardingpass.fragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.seatunity.boardingpass.HomeActivity;
import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.adapter.AdapterForSeatmet;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassList;
import com.seatunity.model.SeatMetList;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class FragmentSeatMet extends Fragment{
	HomeListFragment parent;
	ImageView img_add_boardingpass;
    TextView tv_add_boardingpass;
	BoardingPassApplication appInstance;
	SeatMetList seatmet_listobj;
	BoardingPass bpass;
	TextView tv_from,tv_to,tv_month_inside_icon,tv_date_inside_icon,tv_seat_no,tv_flight_no,
	tv_start_time,tv_arrival_time,tv_jfk,tv_cdg;
	ListView lv_seat_met_list;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance =(BoardingPassApplication) getActivity().getApplication();
	}
	public FragmentSeatMet(SeatMetList seatmet_listobj,BoardingPass bpass){
		Log.e("insideList4", bpass.getTravel_from_name());
		this.seatmet_listobj=seatmet_listobj;
		this.bpass=bpass;
	}
	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_seat_mate,
				container, false);
		tv_from=(TextView) v.findViewById(R.id.tv_from);
		tv_to=(TextView) v.findViewById(R.id.tv_to);
		tv_month_inside_icon=(TextView) v.findViewById(R.id.tv_month_inside_icon);
		tv_date_inside_icon=(TextView) v.findViewById(R.id.tv_date_inside_icon);
		tv_seat_no=(TextView) v.findViewById(R.id.tv_seat_no);
		tv_flight_no=(TextView) v.findViewById(R.id.tv_flight_no);
		tv_start_time=(TextView) v.findViewById(R.id.tv_start_time);
		tv_arrival_time=(TextView) v.findViewById(R.id.tv_arrival_time);
		tv_cdg=(TextView) v.findViewById(R.id.tv_cdg);
		tv_jfk=(TextView) v.findViewById(R.id.tv_jfk);
		lv_seat_met_list=(ListView) v.findViewById(R.id.lv_seat_met_list);
		AdapterForSeatmet adapter=new AdapterForSeatmet(appInstance.getUserCred().getToken(),FragmentSeatMet.this,getActivity(), seatmet_listobj.getBoardingPassList());
		lv_seat_met_list.setAdapter(adapter);
		lv_seat_met_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				parent.startFragmentSingleSeatmet(seatmet_listobj.getBoardingPassList().get(position), bpass);
				
			}
		});
		setDetailsBoaredingpass();
		return v;
	}
	public String getJsonObjet(){

		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token",appInstance.getUserCred().getToken());
			return loginObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public void setDetailsBoaredingpass(){
		String date=Constants.getDayandYear(Integer.parseInt(bpass.getJulian_date()));
		String[] dateParts = date.split(":");
		String month=dateParts[1];
		String dateofmonth=dateParts[0];
		tv_from.setText(bpass.getTravel_from());
		tv_to.setText(bpass.getTravel_to());
		tv_month_inside_icon.setText(month);
		tv_date_inside_icon.setText(dateofmonth);
		tv_seat_no.setText(getActivity().getResources().getString(R.string.txt_seat_nno)+
				"Seat "+bpass.getSeat());
		tv_flight_no.setText(getActivity().getResources().getString(R.string.txt_flight_no)+
				" "+bpass.getFlight_no());
		tv_start_time.setText(bpass.getDeparture());
		tv_arrival_time.setText(bpass.getArrival());
		Log.e("test", "a "+bpass.getTravel_from_name());
		tv_cdg.setText(bpass.getTravel_from_name());
		tv_jfk.setText(bpass.getTravel_to_name());
	}
	
	public void Successmessagesent(){
	}

	
}

