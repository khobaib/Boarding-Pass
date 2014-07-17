package com.seatunity.boardingpass.asynctask;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.seatunity.apicall.JsonParser;
import com.seatunity.boardingpass.EditUserNameActivity;
import com.seatunity.boardingpass.ForgotPassActivity;
import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.PasswordChangeActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.R.id;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.fragment.FragmentBoardingPasses;
import com.seatunity.boardingpass.fragment.FragmentLogin;
import com.seatunity.boardingpass.fragment.FragmentMyAccount;
import com.seatunity.boardingpass.fragment.FragmentSeatMet;
import com.seatunity.boardingpass.fragment.FragmentSignUp;
import com.seatunity.boardingpass.fragment.FragmentUpcomingBoardingPassDetails;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.networkstatetracker.NetworkStateReceiver;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassList;
import com.seatunity.model.Member;
import com.seatunity.model.SeatMetList;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
@TargetApi(Build.VERSION_CODES.HONEYCOMB) public class AsyncaTaskApiCall extends AsyncTask<Void, Void, ServerResponse> {
	String body;
	BoardingPassApplication appInstance;
	String myaccounturl;
	JsonParser jsonParser;
	ProgressDialog pd;
	Context context;
	BoardingPass bpass;
	String addedurl;
	String receiverid;
	int requestType;
	boolean redundantLoginState=false;
	CallBackApiCall CaBLisenar;
	NetworkStateReceiver netstatelisenaer;
	NetworkStateReceiver deletelisenarfromnetstate;
	public AsyncaTaskApiCall(CallBackApiCall CaBLisenar,String body,Context context,String addedurl,int requestType,
			boolean redundantLoginState){
		this.body=body;
		this.appInstance=appInstance;
		this.myaccounturl=myaccounturl;
		this.context=context;
		this.addedurl=addedurl;
		this.CaBLisenar=CaBLisenar;
		this.requestType=requestType;
		jsonParser=new JsonParser();
		this.redundantLoginState=redundantLoginState;
		
		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
				context.getResources().getString(R.string.txt_please_wait), true);

	}
	public AsyncaTaskApiCall(NetworkStateReceiver deletelisenarfromnetstate,String body,Context context, 
			String myaccounturl,BoardingPass bpass){
		this.bpass=bpass;
		this.deletelisenarfromnetstate=deletelisenarfromnetstate;
		this.body=body;
		this.appInstance=appInstance;
		this.myaccounturl=myaccounturl;
		this.context=context;
		jsonParser=new JsonParser();
		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
				context.getResources().getString(R.string.txt_please_wait), true);

	}

	public AsyncaTaskApiCall(CallBackApiCall CaBLisenar,String body,Context context,String addedurl,int requestType){
		this.body=body;
		this.appInstance=appInstance;
		this.myaccounturl=myaccounturl;
		this.context=context;
		this.addedurl=addedurl;
		this.CaBLisenar=CaBLisenar;
		this.requestType=requestType;
		jsonParser=new JsonParser();
		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
				context.getResources().getString(R.string.txt_please_wait), true);

	}
	public AsyncaTaskApiCall(NetworkStateReceiver netstatelisenaer,BoardingPass bpass,String body,Context context){
		this.netstatelisenaer=netstatelisenaer;
		this.body=body;
		this.context=context;
		jsonParser=new JsonParser();
		this.bpass=bpass;

	}
	@Override
	protected ServerResponse doInBackground(Void... params) {
		ServerResponse response=null;

		 if(netstatelisenaer!=null){
			
			String url = Constants.baseurl+"newbp";
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
					body, null);
		}
		else if(netstatelisenaer!=null){
			
			String url = Constants.baseurl+"newbp";
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
					body, null);
		}
		else if(deletelisenarfromnetstate!=null){

			String url = Constants.baseurl+myaccounturl;
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
						body, null);
		}
		
		
		else if(CaBLisenar!=null){

			String url = Constants.baseurl+addedurl;
			
				response =jsonParser.retrieveServerData(requestType, url, null,
						body, null);
		}
		return response;

	}

	@Override
	protected void onPostExecute(ServerResponse result) {
		super.onPostExecute(result);
		
		if( pd!=null){ 
			if(pd.isShowing()){
				pd.cancel();
			}
		}
		
		 if(netstatelisenaer!=null){
			netstatelisenaer.addBoardingPassonBackendSuccess(result.getjObj(),bpass);
		}
		 else if(CaBLisenar!=null){
			 if(redundantLoginState){
				 JSONObject job=result.getjObj();
					try {
						if(job.getString("success").equals("true")){
							CaBLisenar.saveLoginCred(job);
							
						}
						else{
							
							CaBLisenar.LoginFailed(job);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						
						e.printStackTrace();
					} 
			 }
			 else{
				 JSONObject job=result.getjObj();
					try {
						if(job.getString("success").equals("true")){
								CaBLisenar.responseOk(job);
							
						}
						else{
							
							CaBLisenar.responseFailure(job);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						
						e.printStackTrace();
					} 
			 }
			

		}
		else if(deletelisenarfromnetstate!=null){
			JSONObject job=result.getjObj();
			try {
				if(job.getString("success").equals("true")){
					BoardingPassList  list=BoardingPassList.getBoardingPassListObject(job);
					deletelisenarfromnetstate.updateDatabaseWithoutServernotification(bpass);
				}
				else{
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}