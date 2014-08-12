
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
import com.seatunity.boardingpass.PasswordChangeActivity;
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
import com.seatunity.model.SeatMetList;
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
public class FragmentGetBoardingPasseFromBackend extends Fragment implements CallBackApiCall{
	HomeListFragment parent;
	EditText et_email,et_password;
	TextView tv_errorshow;
	Button bt_login;
	AsyncaTaskApiCall retreive;
	ArrayList<BoardingPass>list;
	ArrayList<BoardingPass>list_greaterthan;
	Button btn_boarding_pass,btn_seatmate;
	String email,password;
	Context context;
	BoardingPass highlitedboardingpass;
	BoardingPassApplication appInstance;
	ListView lv_boarding_pass;
	TextView tv_from,tv_to,tv_month_inside_icon,tv_date_inside_icon,tv_seat_no,tv_flight_no,
	tv_start_time,tv_arrival_time,tv_cdg,tv_jfk;
	int callfrom=0;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=getActivity();
		//Log.e("again", "onCreate");
		appInstance =(BoardingPassApplication) getActivity().getApplication();
		if(parent==null){
			parent=Constants.parent;
		}
		
		if(Constants.isOnline(getActivity())){
			//Log.e("Test", "")
			Log.e("Test", ""+appInstance.isRememberMe());
			if(!appInstance.isRememberMe()){
				Log.e("Test", ""+appInstance.isRememberMe());
				SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
				dbInstance.open();
				list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
				dbInstance.close();
				Calendar c = Calendar.getInstance(); 
				int dayofyear = c.get(Calendar.DAY_OF_YEAR);
				list_greaterthan=new ArrayList<BoardingPass>();
				for (int i=0;i<list.size();i++){
					Log.e("test", "t "+list.get(i).getTravel_from_name());
					int ju_date=Integer.parseInt(list.get(i).getJulian_date());
					if((ju_date>=dayofyear)&&(!list.get(i).getDeletestate())){
						list_greaterthan.add(list.get(i));
					}
					
				}
				if(list_greaterthan.size()<1){
					Log.e("tagg", "as "+parent);
					Log.e("Test", "1");
					parent.startHomeFragment();
				}
				else{
					Log.e("Test", "2");
					parent.startFragmentBoardingPasses(list_greaterthan);
				}
			}
			else{
				//CallBackApiCall CaBLisenar,String body,Context context,String addedurl,int requestType
				Log.e("Test", "3");
				callfrom=1;
				retreive =new AsyncaTaskApiCall(this, getJsonObjet(), getActivity(),"bplist",Constants.REQUEST_TYPE_POST);
				retreive.execute();
			}
		}
		else{
			Log.e("Test", "4");
			SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
			dbInstance.open();
			list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
			dbInstance.close();
			Calendar c = Calendar.getInstance(); 
			int dayofyear = c.get(Calendar.DAY_OF_YEAR);
			list_greaterthan=new ArrayList<BoardingPass>();
			for (int i=0;i<list.size();i++){
				Log.e("test", "t "+list.get(i).getTravel_from_name());
				int ju_date=Integer.parseInt(list.get(i).getJulian_date());
				if((ju_date>=dayofyear)&&(!list.get(i).getDeletestate())){
					list_greaterthan.add(list.get(i));

				}
				
			}
			
			if(!appInstance.isRememberMe()){
				Log.e("Test", "5");
				if(list_greaterthan.size()<1){
					parent.startAddBoardingPassDuringLogin();
					Log.e("Test", "6");
				}
				else{
					Log.e("Test", "7");
					parent.startFragmentBoardingPasses(list_greaterthan);
				}
				
			}
			else{
				Log.e("Test", "8");
				if(list_greaterthan.size()<1){
					Log.e("Test", "9");
					parent.startHomeFragment();
				}
				else{
					Log.e("Test", "10");
					parent.startFragmentBoardingPasses(list_greaterthan);
				}
			}
			
			
		}
		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if(getActivity()!=null){
			context=getActivity();
		}
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_get_bpass_from_backend,
				container, false);
		return v;
	}
	public String getJsonObjet(){
		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token",appInstance.getUserCred().getToken());
			loginObj.put("boarding_pass","all");
			return loginObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void responseOk(JSONObject job) {
		try {
			if(job.getString("success").equals("true")){

				this.list=BoardingPassList.getBoardingPassListObject(job).getBoardingPassList();
				SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
				Log.e("db", dbInstance+" ab");
				dbInstance.open();
				for(int i=0;i<list.size();i++){
					Log.e("testing", ""+i+"  "+list.get(i).getTravel_from_name());
					dbInstance.insertOrUpdateBoardingPass(list.get(i));
				}
				list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
				dbInstance.close();
				
				Calendar c = Calendar.getInstance(); 
				int dayofyear = c.get(Calendar.DAY_OF_YEAR);
				list_greaterthan=new ArrayList<BoardingPass>();
				for (int i=0;i<list.size();i++){
					Log.e("test", "t "+list.get(i).getTravel_from_name());
					int ju_date=Integer.parseInt(list.get(i).getJulian_date());
					if((ju_date>=dayofyear)&&(!list.get(i).getDeletestate())){
						list_greaterthan.add(list.get(i));

					}
					
				}
				if(list_greaterthan.size()<1){
					parent.startAddBoardingPassDuringLogin();
				}
				else{
					parent.startFragmentBoardingPasses(list_greaterthan);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	@Override
	public void responseFailure(JSONObject job) {
		try {
			JSONObject joberror=new JSONObject(job.getString("error"));
			String code =joberror.getString("code");
			if(code.equals("x05")){
				JSONObject loginObj = new JSONObject();
				loginObj.put("email", appInstance.getUserCred().getEmail());
				loginObj.put("password", appInstance.getUserCred().getPassword());
				String loginData = loginObj.toString();
				Log.e("tagged ", "msg  "+getActivity());
				AsyncaTaskApiCall log_in_lisenar =new AsyncaTaskApiCall(FragmentGetBoardingPasseFromBackend.this, loginData, 
						context,"login",Constants.REQUEST_TYPE_POST,true);
				log_in_lisenar.execute();
			}
			else{
				Toast.makeText(getActivity(), job.getString("message"),
						Toast.LENGTH_SHORT).show();
			} 
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void saveLoginCred(JSONObject job) {
		try {
			UserCred userCred;
			String status=job.getString("success");
			if(status.equals("true")){
				userCred=UserCred.parseUserCred(job);
				userCred.setEmail(appInstance.getUserCred().getEmail());
				userCred.setPassword(appInstance.getUserCred().getPassword());
				appInstance.setUserCred(userCred);
				appInstance.setRememberMe(true);
					callfrom=1;
					retreive =new AsyncaTaskApiCall(FragmentGetBoardingPasseFromBackend.this, getJsonObjet(), context,"bplist",Constants.REQUEST_TYPE_POST);
					retreive.execute();
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void LoginFailed(JSONObject job) {
		try {
			JSONObject joberror=new JSONObject(job.getString("error"));
			String code =joberror.getString("code");
			Constants.setAllFlagFalse();
			String message=joberror.getString("message");
			Toast.makeText(context, message,
					Toast.LENGTH_SHORT).show();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

