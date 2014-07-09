
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
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.adapter.AdapterForBoardingPass;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.SQLException;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class FragmentBoardingPasses extends Fragment{
	HomeListFragment parent;
	EditText et_email,et_password;
	TextView tv_errorshow;
	Button bt_login;
	ArrayList<BoardingPass>list;
	Button btn_boarding_pass;
	String email,password;
	Context context;
	BoardingPass highlitedboardingpass;
	BoardingPassApplication appInstance;
	ListView lv_boarding_pass;
	TextView tv_from,tv_to,tv_month_inside_icon,tv_date_inside_icon,tv_seat_no,tv_flight_no,
	tv_start_time,tv_arrival_time;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance =(BoardingPassApplication) getActivity().getApplication();
		context=getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_past_boarding_passes,
				container, false);
		lv_boarding_pass=(ListView) v.findViewById(R.id.lv_boarding_pass);
		tv_from=(TextView) v.findViewById(R.id.tv_from);
		tv_to=(TextView) v.findViewById(R.id.tv_to);
		tv_month_inside_icon=(TextView) v.findViewById(R.id.tv_month_inside_icon);
		tv_date_inside_icon=(TextView) v.findViewById(R.id.tv_date_inside_icon);
		tv_seat_no=(TextView) v.findViewById(R.id.tv_seat_no);
		tv_flight_no=(TextView) v.findViewById(R.id.tv_flight_no);
		tv_start_time=(TextView) v.findViewById(R.id.tv_start_time);
		tv_arrival_time=(TextView) v.findViewById(R.id.tv_arrival_time);
		btn_boarding_pass=(Button) v.findViewById(R.id.btn_boarding_pass);
		btn_boarding_pass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(list.size()>0){
					parent.startUpCommingBoadingDetails(highlitedboardingpass);
				}
			}
		});
		list=new ArrayList<BoardingPass>();
		if(Constants.isOnline(getActivity())){
			if(appInstance.getUserCred().getEmail().equals("")){

				SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
				dbInstance.open();
				list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
				dbInstance.close();
			}
			else{
				AsyncaTaskApiCall retreive =new AsyncaTaskApiCall(FragmentBoardingPasses.this, getJsonObjet(), getActivity());
				retreive.execute();
			}
		}
		else{
			SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
			dbInstance.open();
			list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
			dbInstance.close();
			Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.txt_check_internet),
					Toast.LENGTH_SHORT).show();
		}

		setlist();

		return v;
	}
	public void lodedLodaedfromServer(ArrayList<BoardingPass> listfromserver){
		this.list=listfromserver;
		//07-09 20:45:46.108: E/response(5999): ab {"boarding_pass":[{"departure":"3 ","lastname":"HABCD","PNR":"YWX9ZS","firstname":"MR CKL        ","flight_no":"2551 ","julian_date":"175","version":"4","id":"20","travel_to_name":"Munich","seat":"024A","compartment_code":"M","travel_from_name":"Lviv","arrival":"9 ","carrier":"LH","stringform":"M1HABCD\/CKLMR         EYWX9ZS LWOMUCLH 2551 175M024A0008 355>2180OO3075BOS 022052227001 262202331497901  LH                     *30601001205","carrier_name":"Lufthansa","travel_to":"MUC","travel_from":"LWO"},{"departure":"3 ","lastname":"HAMID","PNR":"YWX9ZS","firstname":"MR FOT        ","flight_no":"2551 ","julian_date":"275","version":"4","id":"21","travel_to_name":"Munich","seat":"024A","compartment_code":"M","travel_from_name":"Lviv","arrival":"9 ","carrier":"LH","stringform":"M1HABCD\/CKLMR         EYWX9ZS LWOMUCLH 2551 175M024A0008 355>2180OO3075BOS 022052227001 262202331497901  LH                     *30601001205","carrier_name":"Lufthansa","travel_to":"MUC","travel_from":"LWO"}],"count":2,"success":"true"}

		setlist();
		Log.e("Tag", " k");
				SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
				dbInstance.open();
				dbInstance.insertOrUpdateBoardingPassList(list);
				list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
				dbInstance.close();
				setlist();

	}
	public void  setlist(){
		if(list.size()>0){
			AdapterForBoardingPass adapter=new AdapterForBoardingPass(getActivity(), list);
			lv_boarding_pass.setAdapter(adapter);
			setDetailsBoaredingpass(list.get(0));
			highlitedboardingpass=list.get(0);
		}
		lv_boarding_pass.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				setDetailsBoaredingpass(list.get(position));
				highlitedboardingpass=list.get(position);
			}
		});
	}
	public String getJsonObjet(){

		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token",appInstance.getUserCred().getToken());
			loginObj.put("boarding_pass","all");

			return loginObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public void setDetailsBoaredingpass(BoardingPass bpass){
		String date=Constants.getDayandYear(Integer.parseInt(bpass.getJulian_date()));
		String[] dateParts = date.split(":");
		String month=dateParts[1];
		String dateofmonth=dateParts[0];
		tv_from.setText(bpass.getTravel_from());
		tv_to.setText(bpass.getTravel_to());
		tv_month_inside_icon.setText(month);
		tv_date_inside_icon.setText(dateofmonth);
		
		tv_seat_no.setText(context.getResources().getString(R.string.txt_seat_nno)+
				"Seat "+bpass.getSeat());
		tv_flight_no.setText(context.getResources().getString(R.string.txt_flight_no)+
				" "+bpass.getFlight_no());

		tv_start_time.setText(bpass.getDeparture());
		tv_arrival_time.setText(bpass.getArrival());
	}
}

