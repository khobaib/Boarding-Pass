
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
import com.seatunity.boardingpass.adapter.AdapterForBoardingPass;
import com.seatunity.boardingpass.adapter.AdapterForPastBoardingPass;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class FragmentPastBoardingPasses extends Fragment{
	PastBoardingPassListFragment parent;
	EditText et_email,et_password;
	TextView tv_errorshow;
	Button bt_login;
	ArrayList<BoardingPass>list;
	String email,password;
	BoardingPassApplication appInstance;
	ListView lv_boarding_past_pass;
//	TextView tv_from,tv_to,tv_month_inside_icon,tv_date_inside_icon,tv_seat_no,tv_flight_no,
//			tv_start_time,tv_arrival_time;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance =(BoardingPassApplication) getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_past_boarding_pases,
				container, false);
		
		Calendar c = Calendar.getInstance(); 
		int dayofyear = c.get(Calendar.DAY_OF_YEAR);
		Log.e("dayofyear", ""+dayofyear);
		lv_boarding_past_pass=(ListView) v.findViewById(R.id.lv_boarding_past_pass);

		SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
		dbInstance.open();
		list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
		dbInstance.close();
		ArrayList<BoardingPass> list_smaller=new ArrayList<BoardingPass>();
		//list.get(0).getJulian_date();
		for(int count=0;count<list.size();count++){
			int ju_date=Integer.parseInt(list.get(count).getJulian_date());
			if((ju_date<dayofyear)){
				list_smaller.add(list.get(count));
			}
			
		}
		if(list.size()>0){
			
		//	setDetailsBoaredingpass(list.get(0));
			AdapterForPastBoardingPass adapter=new AdapterForPastBoardingPass(getActivity(), list_smaller);
			lv_boarding_past_pass.setAdapter(adapter);
		}
		
//		lv_boarding_pass.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//					long arg3) {
//				// TODO Auto-generated method stub
//				//setDetailsBoaredingpass(list.get(position));
//			}
//		});
		return v;
	}

//	public void setDetailsBoaredingpass(BoardingPass bpass){
//		String date=Constants.getDayandYear(Integer.parseInt(bpass.getJulian_date()));
//    	String[] dateParts = date.split(":");
//		String month=dateParts[1];
//		String dateofmonth=dateParts[0];
//		tv_from.setText(bpass.getTravel_from());
//		tv_to.setText(bpass.getTravel_to());
//		tv_month_inside_icon.setText(month);
//		tv_date_inside_icon.setText(dateofmonth);
//		tv_seat_no.setText(getActivity().getResources().getString(R.string.txt_seat_nno)+
//        		" "+bpass.getSeat());
//		tv_flight_no.setText(getActivity().getResources().getString(R.string.txt_flight_no)+
//        		" "+bpass.getFlight_no());
//		tv_start_time.setText(bpass.getDeparture());
//		tv_arrival_time.setText(bpass.getArrival());
//	}
}

