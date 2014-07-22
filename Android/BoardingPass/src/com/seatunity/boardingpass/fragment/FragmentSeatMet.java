
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
import com.seatunity.boardingpass.adapter.AdapterBaseMaps;
import com.seatunity.boardingpass.adapter.AdapterForSeatmet;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassList;
import com.seatunity.model.SeatMate;
import com.seatunity.model.SeatMetList;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;

import android.app.ProgressDialog;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
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
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
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
public class FragmentSeatMet extends Fragment implements CallBackApiCall{
	HomeListFragment parent;
	ImageView img_add_boardingpass;
	TextView tv_add_boardingpass;
	BoardingPassApplication appInstance;
	SeatMetList seatmet_listobj;
	BoardingPass bpass;
	public String savedMessage;
	public String Savedurl;
	ArrayList<String> itemList;
	TextView tv_from,tv_to,tv_month_inside_icon,tv_date_inside_icon,tv_seat_no,tv_flight_no,
	tv_start_time,tv_arrival_time,tv_jfk,tv_cdg,tv_message;
	ListView lv_seat_met_list;
	int selectedposition=0;
	Context context;
	public int callfrom=0;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance =(BoardingPassApplication) getActivity().getApplication();
		context=getActivity();
	}
	public FragmentSeatMet(SeatMetList seatmet_listobj,BoardingPass bpass){
		Log.e("insideList4", bpass.getTravel_from_name());
		this.seatmet_listobj=seatmet_listobj;
		this.bpass=bpass;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_seat_mate,
				container, false);
		tv_message=(TextView) v.findViewById(R.id.tv_message);
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
		setListView(0);
		selectedposition=0;
		setDetailsBoaredingpass();
		
		
		int upId = Resources.getSystem().getIdentifier("up", "id", "android");
		if (upId > 0) {
			Toast.makeText(getActivity(), "Working", 2000).show();
		    ImageView up = (ImageView) getActivity().findViewById(upId);
		    up.setImageResource(R.drawable.ic_action_previous_item);
		}
		else{
			Toast.makeText(getActivity(), " Not Working", 2000).show();
		}

		return v;
	}
	public void setListView(int i){
		AdapterForSeatmet adapter;
		
		if(i==0){
			ArrayList<SeatMate> seatmatelist_all=new ArrayList<SeatMate>(seatmet_listobj.getAllSeatmateList());
			if(seatmatelist_all.size()>0){
				lv_seat_met_list.setVisibility(View.VISIBLE);
				tv_message.setVisibility(View.GONE);
				adapter=new AdapterForSeatmet(appInstance.getUserCred().getToken(),FragmentSeatMet.this,getActivity(), seatmet_listobj.getAllSeatmateList());
				lv_seat_met_list.setAdapter(adapter);
			}
			else{
				lv_seat_met_list.setVisibility(View.GONE);
				tv_message.setVisibility(View.VISIBLE);
				tv_message.setText(getActivity().getResources().getString(R.string.txt_noseatmate_isfound)+" "+itemList.get(i)
						+" "+getActivity().getResources().getString(R.string.txt_is_found));
			}
			
		}
		else if(i==1){
			ArrayList<SeatMate> seatmatelist_firstclass=new ArrayList<SeatMate>();
			for(int k=0;k<seatmet_listobj.getAllSeatmateList().size();k++){
				if(seatmet_listobj.getAllSeatmateList().get(k).getTravel_class().equals("First Class")){
					seatmatelist_firstclass.add(seatmet_listobj.getAllSeatmateList().get(k));
				}
			}
			if(seatmatelist_firstclass.size()>0){
				lv_seat_met_list.setVisibility(View.VISIBLE);
				tv_message.setVisibility(View.GONE);
				adapter=new AdapterForSeatmet(appInstance.getUserCred().getToken(),FragmentSeatMet.this,getActivity(), seatmatelist_firstclass);
				lv_seat_met_list.setAdapter(adapter);
			}
			else{
				lv_seat_met_list.setVisibility(View.GONE);
				tv_message.setVisibility(View.VISIBLE);
				tv_message.setText(getActivity().getResources().getString(R.string.txt_noseatmate_isfound)+" "+itemList.get(i)
						+" "+getActivity().getResources().getString(R.string.txt_is_found));
			}
			
		}
		else if(i==2){

			ArrayList<SeatMate> seatmatelist_business_class=new ArrayList<SeatMate>();
			for(int k=0;k<seatmet_listobj.getAllSeatmateList().size();k++){
				if(seatmet_listobj.getAllSeatmateList().get(k).getTravel_class().equals("Business Class")){
					seatmatelist_business_class.add(seatmet_listobj.getAllSeatmateList().get(k));
				}
			}
			if(seatmatelist_business_class.size()>0){
				lv_seat_met_list.setVisibility(View.VISIBLE);
				tv_message.setVisibility(View.GONE);
				adapter=new AdapterForSeatmet(appInstance.getUserCred().getToken(),FragmentSeatMet.this,getActivity(), seatmatelist_business_class);
				lv_seat_met_list.setAdapter(adapter);
			}
			else{
				lv_seat_met_list.setVisibility(View.GONE);
				tv_message.setVisibility(View.VISIBLE);
				tv_message.setText(getActivity().getResources().getString(R.string.txt_noseatmate_isfound)+" "+itemList.get(i)
						+" "+getActivity().getResources().getString(R.string.txt_is_found));
			}
			
		}
		else if(i==3){
			ArrayList<SeatMate> seatmatelist_economy_calss=new ArrayList<SeatMate>();
			for(int k=0;k<seatmet_listobj.getAllSeatmateList().size();k++){
				if((seatmet_listobj.getAllSeatmateList().get(k).getTravel_class().equals("Economy Class"))||
						(seatmet_listobj.getAllSeatmateList().get(k).getTravel_class().equals("Premium Economy"))){
					seatmatelist_economy_calss.add(seatmet_listobj.getAllSeatmateList().get(k));
				}
			}
			if(seatmatelist_economy_calss.size()>0){
				lv_seat_met_list.setVisibility(View.VISIBLE);
				tv_message.setVisibility(View.GONE);
				adapter=new AdapterForSeatmet(appInstance.getUserCred().getToken(),FragmentSeatMet.this,getActivity(),seatmatelist_economy_calss);
				lv_seat_met_list.setAdapter(adapter);
			}
			else{
				lv_seat_met_list.setVisibility(View.GONE);
				tv_message.setVisibility(View.VISIBLE);
				tv_message.setText(getActivity().getResources().getString(R.string.txt_noseatmate_isfound)+" "+itemList.get(i)
						+" "+getActivity().getResources().getString(R.string.txt_is_found));
			}
			
		}
		
		lv_seat_met_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				parent.startFragmentSingleSeatmet(seatmet_listobj.getAllSeatmateList().get(position), bpass);
			}
		});
	}
	public void setActionBarNavigation(boolean show){
		final ActionBar actionBar = getActivity().getActionBar();
		OnNavigationListener imll=new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				setListView(itemPosition);
				selectedposition=itemPosition;
				return false;
			}
		};
		String[] class_list = getActivity().getResources().getStringArray(R.array.seat_class); 
		 itemList =new ArrayList<String>(Arrays.asList(class_list));
		 AdapterBaseMaps adapter=new AdapterBaseMaps(getActivity(), itemList);
		ArrayAdapter<String> aAdpt = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, itemList);
		if(show){
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			actionBar.setListNavigationCallbacks(adapter,imll);
		}
		else{
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
			actionBar.setListNavigationCallbacks(null,null);
		}

	}


	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		setActionBarNavigation(false);
		((MainActivity)getActivity()).refreash_menu.setVisible(false);

	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setActionBarNavigation(true);
		((MainActivity)getActivity()).refreash_menu.setVisible(true);
		((MainActivity)getActivity()).refreash_menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if((Constants.isOnline(getActivity()))&&(!appInstance.getUserCred().getEmail().equals(""))){
					
					callSeatmet();
				}
				else{
					sowAlertMessage();
				}
				return false;
				
			}
		});
	}
	public void callSeatmet(){
		callfrom=2;
		String extendedurl="seatmatelist/"+bpass.getCarrier()+"/"+bpass.getFlight_no()+"/"
				+bpass.getJulian_date();
		extendedurl=extendedurl.replace(" ", "");
		AsyncaTaskApiCall get_list =new AsyncaTaskApiCall(FragmentSeatMet.this, getJsonObjet(), getActivity(),
				extendedurl,Constants.REQUEST_TYPE_POST);
		get_list.execute();
	}

	public void sowAlertMessage(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder
		.setMessage(getResources().getString(R.string.txt_seatmet_message_only_online))
		.setCancelable(false)
		.setPositiveButton(getResources().getString(R.string.txt_ok),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});
		
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
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

	@Override
	public void responseOk(JSONObject job) {
		try {
			if(job.getString("success").equals("true")){
				//boarding_passes
				if(callfrom==1){
					Toast.makeText(context, context.getResources().getString(R.string.txt_emailsent_success),
							Toast.LENGTH_SHORT).show();
					
				}
				else if(callfrom==2){
					SeatMetList  seatmet_listlist=SeatMetList.getSeatmetListObj(job);
					this.seatmet_listobj=seatmet_listlist;
					if(seatmet_listobj.getAllSeatmateList().size()>0){
						setListView(selectedposition);
					}
					else{
						Toast.makeText(context, context.getResources().getString(R.string.txt_getseatmate_failure),
								Toast.LENGTH_SHORT).show();
					}
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
				AsyncaTaskApiCall log_in_lisenar =new AsyncaTaskApiCall(FragmentSeatMet.this, loginData, 
						context,"login",Constants.REQUEST_TYPE_POST,true);
				log_in_lisenar.execute();

			}
			else{
					Toast.makeText(context, job.getString("message"),
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
		// TODO Auto-generated method stub
		try {
			UserCred userCred;
			String status=job.getString("success");
			if(status.equals("true")){
				userCred=UserCred.parseUserCred(job);
				userCred.setEmail(appInstance.getUserCred().getEmail());
				userCred.setPassword(appInstance.getUserCred().getPassword());
				appInstance.setUserCred(userCred);
				appInstance.setRememberMe(true);
				if(callfrom==1){
					JSONObject loginObj = new JSONObject();
					loginObj.put("token", appInstance.getUserCred().getToken());
					loginObj.put("message", savedMessage);
					callfrom=1;
					AsyncaTaskApiCall sendmessage =new AsyncaTaskApiCall(FragmentSeatMet.this, loginObj.toString(),context,
							Savedurl,Constants.REQUEST_TYPE_POST);
					sendmessage.execute();
				}
				else if(callfrom==2){
					callfrom=2;
					String extendedurl="seatmatelist/"+bpass.getCarrier()+"/"+bpass.getFlight_no()+"/"
							+bpass.getJulian_date();
					extendedurl=extendedurl.replace(" ", "");
					AsyncaTaskApiCall get_list =new AsyncaTaskApiCall(FragmentSeatMet.this, getJsonObjet(),context,
							extendedurl,Constants.REQUEST_TYPE_POST);
					get_list.execute();
				}
				
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}

