
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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.seatunity.apicall.JsonParser;
import com.seatunity.boardingpass.HomeActivity;
import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.adapter.AdapterForBoardingPass;
import com.seatunity.boardingpass.adapter.AdapterForBoardingPassFromAlert;
import com.seatunity.boardingpass.adapter.AdapterForSeatmet;
import com.seatunity.boardingpass.alert.LisAlertDialog;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassList;
import com.seatunity.model.BoardingPassListForSharedFlight;
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
public class FragmentSingleSeatMet extends Fragment{
	HomeListFragment parent;
	ImageView img_prof_pic;
	BoardingPassApplication appInstance;
	SeatMate seatmate;
	JsonParser jsonParser;
	BoardingPass bpass;
	ListView lv_sharedflight;
	TextView tv_uname,tv_profession,tv_seat,tv_shared_flight,tv_status,tv_live_in,tv_age,tv_class,tv_sothn_about;
	ListView lv_seat_met_list;
	Button btn_seatmate;
	String hint;
	 EditText input ;
	 AlertDialog d ;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance =(BoardingPassApplication) getActivity().getApplication();
		jsonParser=new JsonParser();
	}
	public FragmentSingleSeatMet(SeatMate seatmate,BoardingPass bpass){
		Log.e("insideList4", bpass.getTravel_from_name());
		this.seatmate=seatmate;
		this.bpass=bpass;
	}
	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_single_seatmate,
				container, false);
		tv_uname=(TextView) v.findViewById(R.id.tv_uname);
		tv_profession=(TextView) v.findViewById(R.id.tv_profession);
		tv_shared_flight=(TextView) v.findViewById(R.id.tv_shared_flight);
		tv_status=(TextView) v.findViewById(R.id.tv_status);
		tv_age=(TextView) v.findViewById(R.id.tv_age);
		tv_class=(TextView) v.findViewById(R.id.tv_class);
		tv_sothn_about=(TextView) v.findViewById(R.id.tv_sothn_about);
		tv_seat=(TextView) v.findViewById(R.id.tv_seat);
		tv_live_in=(TextView) v.findViewById(R.id.tv_live_in);
		btn_seatmate=(Button) v.findViewById(R.id.btn_seatmate);
		img_prof_pic=(ImageView) v.findViewById(R.id.img_prof_pic);
		tv_shared_flight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					JSONObject loginObj = new JSONObject();
					loginObj.put("token", appInstance.getUserCred().getToken());
					if(Constants.isOnline(getActivity())){
						new AsyncSharedFlight(loginObj.toString()).execute();
					}
					else{
						Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.
								txt_check_internet), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btn_seatmate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showAlertDilog();
			}
		});
		SetView();
		return v;
	}
	
	public void SetView(){
		tv_uname.setText(seatmate.getName());
		tv_profession.setText(seatmate.getProfession());
		tv_seat.setText(seatmate.getTravel_class()+", "+seatmate.getSeat());
		tv_status.setText(seatmate.getStatus());
		tv_live_in.setText(getActivity().getResources().getString(R.string.txt_live_in)+seatmate.getLive_in());
		tv_age.setText(getActivity().getResources().getString(R.string.txt_age)+seatmate.getName());
		tv_class.setText(getActivity().getResources().getString(R.string.txt_prefer_to)+seatmate.getSeating_pref());
		tv_sothn_about.setText(seatmate.getSome_about_you());
		if((seatmate.getImage_url()!=null)&&(!seatmate.getImage_url().equals(""))){
			ImageLoader.getInstance().displayImage(seatmate.getImage_url(), img_prof_pic);
		}
	}
	public String getJsonObjet(){

		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token",appInstance.getUserCred().getToken());
			return loginObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	private class AsyncSharedFlight extends AsyncTask<Void, Void, ServerResponse> {
		ProgressDialog pd;
		String loginData;
		public AsyncSharedFlight(String loginData){
			this.loginData=loginData;
			pd=ProgressDialog.show(getActivity(),  getActivity().getResources().getString(R.string.app_name),
					getActivity().getResources().getString(R.string.txt_please_wait), true);
		}
		@Override
		protected ServerResponse doInBackground(Void... params) {
				String url = Constants.baseurl+"sharedflight/"+seatmate.getId();
				ServerResponse response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
						loginData, null);
				return response;
		}

		@Override
		protected void onPostExecute(ServerResponse result) {
			super.onPostExecute(result);
			Log.e("Sending", result.getjObj().toString());
			if(pd.isShowing()&&(pd!=null)){
				pd.dismiss();
			}
			JSONObject job=result.getjObj();
			try {
				if(job.getString("success").equals("true")){
					BoardingPassListForSharedFlight  list=BoardingPassListForSharedFlight.getBoardingPassListObject(job);
					if(list.getBoardingPassList().size()>0){
						showAlertDilogToshowSharedFlight(list.getBoardingPassList());
					}
					else{
						Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_no_shared_flight),
								Toast.LENGTH_SHORT).show();
					}
				}
				else{
					Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_failed_to_get_seatmet),
							Toast.LENGTH_SHORT).show();
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class AsyncTaskSendMessage extends AsyncTask<Void, Void, ServerResponse> {
		ProgressDialog pd;
		String loginData;
		public AsyncTaskSendMessage(String loginData){
			this.loginData=loginData;
			pd=ProgressDialog.show(getActivity(),  getActivity().getResources().getString(R.string.app_name),
					getActivity().getResources().getString(R.string.txt_please_wait), true);
		}
		@Override
		protected ServerResponse doInBackground(Void... params) {
				String url = Constants.baseurl+"messagemate/"+seatmate.getId();
				ServerResponse response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
						loginData, null);
				return response;
		}

		@Override
		protected void onPostExecute(ServerResponse result) {
			super.onPostExecute(result);
			Log.e("Sending", result.getjObj().toString());
			if(pd.isShowing()&&(pd!=null)){
				pd.dismiss();
			}
			JSONObject job=result.getjObj();
			try {
					if(job.getString("success").equals("true")){
						Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_emailsent_success),
								Toast.LENGTH_SHORT).show();
					}
					else{
						Toast.makeText(getActivity(), job.getString("message"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
	}
	public void Successmessagesent(){
	}
	
	public void showAlertDilog( ){
		 hint=getActivity().getResources().getString(R.string.txt_message);
		  input = new EditText(getActivity());
		 d = new AlertDialog.Builder(getActivity())
		.setView(input)
		.setTitle(getActivity().getResources().getString(R.string.txt_send_email))
		.setPositiveButton(getActivity().getResources().getString(R.string.txt_ok), null) //Set to null. We override the onclick
		.setNegativeButton(getActivity().getResources().getString(R.string.txt_cancel), null)
		.create();

		d.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String value = input.getText().toString();

						if((value==null)||(value.equals(""))){
							Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.
									txt_please_enter)+" "+hint, Toast.LENGTH_SHORT).show();
						}
						else{
							d.cancel();
							try {
								JSONObject loginObj = new JSONObject();
								loginObj.put("token", appInstance.getUserCred().getToken());
								loginObj.put("message", value);
								if(Constants.isOnline(getActivity())){
									new AsyncTaskSendMessage(loginObj.toString()).execute();
								}
								else{
									Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.
											txt_check_internet), Toast.LENGTH_SHORT).show();
								}
								
//								AsyncaTaskApiCall sendmessage=new AsyncaTaskApiCall(lisenar, loginObj.toString(), context, list.get(position).getId());
//								sendmessage.execute();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
				});
			}
		});
		d.show();
	}

	public void showAlertDilogToshowSharedFlight(ArrayList<BoardingPass> item){
//		 hint=getActivity().getResources().getString(R.string.txt_message);
//		 AdapterForBoardingPass adapter=new AdapterForBoardingPass(getActivity(), item);
//		 lv_sharedflight = new ListView(getActivity());
//		 lv_sharedflight.setAdapter(adapter);
//		
//		 d = new AlertDialog.Builder(getActivity())
//		.setView(input)
//		.setTitle(getActivity().getResources().getString(R.string.txt_send_email))
//		.setNegativeButton(getActivity().getResources().getString(R.string.txt_cancel), null)
//		.create();
//		 lv_sharedflight.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//				d.cancel();
//				
//			}
//		});
//		d.setOnShowListener(new DialogInterface.OnShowListener() {
//
//			@Override
//			public void onShow(DialogInterface dialog) {
//
//				Button b = d.getButton(AlertDialog.BUTTON_NEGATIVE);
//				b.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View view) {
//							d.cancel();
//					}
//				});
//			}
//		});
//		d.show();
		
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                getActivity());
         builderSingle.setTitle(getActivity().getResources().getString(R.string.txt_shared_flight_title));
         
        final  AdapterForBoardingPassFromAlert arrayAdapter=new AdapterForBoardingPassFromAlert(getActivity(), item);
       
        builderSingle.setNegativeButton(getActivity().getResources().getString(R.string.txt_cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	dialog.dismiss();
                    }
                });
        builderSingle.show();
        
//        LisAlertDialog dialog=new LisAlertDialog(getActivity(), item);
//        dialog.show_alert();
	}


	
}

