
package com.seatunity.boardingpass.fragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.seatunity.boardingpass.HomeActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.adapter.AdapterBaseMaps;
import com.seatunity.boardingpass.adapter.AdapterForBoardingPass;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassList;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
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
public class FragmentBoardingPasses extends Fragment implements CallBackApiCall{
	HomeListFragment parent;
	EditText et_email,et_password;
	TextView tv_errorshow;
	Button bt_login;
	AsyncaTaskApiCall retreive;
	ArrayList<BoardingPass>list;
	ArrayList<BoardingPass>list_greaterthan;
	Button btn_boarding_pass;
	String email,password;
	Context context;
	BoardingPass highlitedboardingpass;
	BoardingPassApplication appInstance;
	ListView lv_boarding_pass;
	TextView tv_from,tv_to,tv_month_inside_icon,tv_date_inside_icon,tv_seat_no,tv_flight_no,
	tv_start_time,tv_arrival_time,tv_cdg,tv_jfk;

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
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_boarding_passes,
				container, false);
		lv_boarding_pass=(ListView) v.findViewById(R.id.lv_boarding_pass);
		tv_cdg=(TextView) v.findViewById(R.id.tv_cdg);
		tv_jfk=(TextView) v.findViewById(R.id.tv_jfk);
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
				//CallBackApiCall CaBLisenar,String body,Context context,String addedurl,int requestType
			 retreive =new AsyncaTaskApiCall(this, getJsonObjet(), getActivity(),"bplist",Constants.REQUEST_TYPE_POST);
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
//	public void lodedLodaedfromServer(ArrayList<BoardingPass> listfromserver){
//		this.list=listfromserver;
//		setlist();
//		Log.e("Tag", " k");
//		SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
//		dbInstance.open();
//		for(int i=0;i<list.size();i++){
//			Log.e("testing", ""+i+"  "+list.get(i).getTravel_from_name());
//			dbInstance.insertOrUpdateBoardingPass(list.get(i));
//		}
//
//		list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
//		dbInstance.close();
//		setlist();
//		if(list.size()<1){
//			parent.startAddBoardingPassDuringLogin();
//		}
//		
//
//	}
	public void  setlist(){
		Calendar c = Calendar.getInstance(); 
		int dayofyear = c.get(Calendar.DAY_OF_YEAR);
		 list_greaterthan=new ArrayList<BoardingPass>();
		//list_greaterthan.clear();
		for (int i=0;i<list.size();i++){
			Log.e("test", "t "+list.get(i).getTravel_from_name());
			int ju_date=Integer.parseInt(list.get(i).getJulian_date());
			if((ju_date>=dayofyear)){
				 list_greaterthan.add(list.get(i));
				
			}
			if(list.get(i).getDeletestate()){
				list.remove(i);
			}
		}
		if(list_greaterthan.size()>0){
			AdapterForBoardingPass adapter=new AdapterForBoardingPass(getActivity(), list_greaterthan);
			lv_boarding_pass.setAdapter(adapter);
			setDetailsBoaredingpass(list_greaterthan.get(0));
			highlitedboardingpass=list_greaterthan.get(0);
		}
		lv_boarding_pass.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				setDetailsBoaredingpass(list_greaterthan.get(position));
				highlitedboardingpass=list_greaterthan.get(position);
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
		tv_cdg.setText(bpass.getTravel_from());
		tv_jfk.setText(bpass.getTravel_to());
		tv_from.setText(bpass.getTravel_from_name());
		tv_to.setText(bpass.getTravel_to_name());
		tv_month_inside_icon.setText(month);
		tv_date_inside_icon.setText(dateofmonth);
		tv_seat_no.setText(context.getResources().getString(R.string.txt_seat_nno)+
				"Seat "+bpass.getSeat());
		tv_flight_no.setText(context.getResources().getString(R.string.txt_flight_no)+
				" "+bpass.getFlight_no());
		tv_start_time.setText(bpass.getDeparture());
		tv_arrival_time.setText(bpass.getArrival());
	}

	@Override
	public void responseOk(JSONObject job) {
		// TODO Auto-generated method stub
		try {
			if(job.getString("success").equals("true")){
				this.list=BoardingPassList.getBoardingPassListObject(job).getBoardingPassList();
				//this.list=listfromserver;
				setlist();
				Log.e("Tag", " k");
				SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
				dbInstance.open();
				for(int i=0;i<list.size();i++){
					Log.e("testing", ""+i+"  "+list.get(i).getTravel_from_name());
					dbInstance.insertOrUpdateBoardingPass(list.get(i));
				}
		
				list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
				dbInstance.close();
				setlist();
				if(list.size()<1){
					parent.startAddBoardingPassDuringLogin();
				}
			}
			else{
				
				Toast.makeText(context, job.getString("message"),
						Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	@Override
	public void responseFailure(JSONObject job) {
		// TODO Auto-generated method stub
		try {
			Toast.makeText(context, job.getString("message"),
					Toast.LENGTH_SHORT).show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void saveLoginCred(JSONObject job) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void LoginFailed(JSONObject job) {
		// TODO Auto-generated method stub
		
	}
}

