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
	FragmentSignUp signuplisenar;
	FragmentLogin loginlisenar;
	MainActivity bpassaddlisenar;
	FragmentMyAccount myaccountlisenar;
	PasswordChangeActivity passwordchangelisenar;
	String body;
	BoardingPassApplication appInstance;
	EditUserNameActivity usernameLisenar;
	String myaccounturl;
	JsonParser jsonParser;
	ProgressDialog pd;
	Context context;
	BoardingPass bpass;
	FragmentBoardingPasses retreivelisenar;
	NetworkStateReceiver netstatelisenaer;
	FragmentUpcomingBoardingPassDetails deletelisenar;
	NetworkStateReceiver deletelisenarfromnetstate;
	ForgotPassActivity forgotlisenar;
	FragmentUpcomingBoardingPassDetails seatmetlisenar;
	FragmentSeatMet sendmessgae;
	String receiverid;

//	FragmentSeatMet singlebpasslisenar;
//	public AsyncaTaskApiCall(FragmentSeatMet singlebpasslisenar,String body,Context context){
//		this.singlebpasslisenar=singlebpasslisenar;
//		this.body=body;
//		this.context=context;
//		jsonParser=new JsonParser();
//		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
//				context.getResources().getString(R.string.txt_please_wait), true);
//	}
	public AsyncaTaskApiCall(FragmentSeatMet sendmessgae,String body,Context context,String receiverid){
		this.sendmessgae=sendmessgae;
		this.body=body;
		this.context=context;
		this.receiverid=receiverid;
		jsonParser=new JsonParser();
		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
				context.getResources().getString(R.string.txt_please_wait), true);
	}
	public AsyncaTaskApiCall(FragmentUpcomingBoardingPassDetails seatmetlisenar,String body,Context context,BoardingPass bpass){
		this.bpass=bpass;
		this.seatmetlisenar=seatmetlisenar;
		this.body=body;
		this.context=context;
		jsonParser=new JsonParser();
		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
				context.getResources().getString(R.string.txt_please_wait), true);
	}
	public AsyncaTaskApiCall(ForgotPassActivity forgotlisenar,String body,Context context){
		this.forgotlisenar=forgotlisenar;
		this.body=body;
		this.context=context;
		jsonParser=new JsonParser();
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
	public AsyncaTaskApiCall(FragmentUpcomingBoardingPassDetails deletelisenar,String body,Context context, 
			String myaccounturl){
		this.deletelisenar=deletelisenar;
		this.body=body;
		this.appInstance=appInstance;
		this.myaccounturl=myaccounturl;
		this.context=context;
		jsonParser=new JsonParser();
		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
				context.getResources().getString(R.string.txt_please_wait), true);

	}
	
	
	public AsyncaTaskApiCall(FragmentBoardingPasses retreivelisenar,String body,Context context){
		this.body=body;
		this.appInstance=appInstance;
		this.myaccounturl=myaccounturl;
		this.context=context;
		this.retreivelisenar=retreivelisenar;
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
//		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
//				context.getResources().getString(R.string.txt_please_wait), true);

	}
	public AsyncaTaskApiCall( BoardingPassApplication appInstance,EditUserNameActivity usernameLisenar,String body,Context context, 
			String myaccounturl){
		this.usernameLisenar=usernameLisenar;
		this.body=body;
		this.appInstance=appInstance;
		this.myaccounturl=myaccounturl;
		this.context=context;
		jsonParser=new JsonParser();
		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
				context.getResources().getString(R.string.txt_please_wait), true);

	}
	public AsyncaTaskApiCall( BoardingPassApplication appInstance,PasswordChangeActivity passwordchangelisenar,String body,Context context, 
			String myaccounturl){
		this.passwordchangelisenar=passwordchangelisenar;
		this.body=body;
		this.appInstance=appInstance;
		this.myaccounturl=myaccounturl;
		this.context=context;
		jsonParser=new JsonParser();
		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
				context.getResources().getString(R.string.txt_please_wait), true);

	}
	public AsyncaTaskApiCall( BoardingPassApplication appInstance,FragmentMyAccount myaccountlisenar,String body,Context context, 
			String myaccounturl){
		this.myaccountlisenar=myaccountlisenar;
		this.body=body;
		this.appInstance=appInstance;
		this.myaccounturl=myaccounturl;
		this.context=context;
		jsonParser=new JsonParser();
		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
				context.getResources().getString(R.string.txt_please_wait), true);

	}
	public AsyncaTaskApiCall(MainActivity bpassaddlisenar,String body,Context context){
		this.bpassaddlisenar=bpassaddlisenar;
		this.body=body;
		this.context=context;
		jsonParser=new JsonParser();
		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
				context.getResources().getString(R.string.txt_please_wait), true);

	}
	public AsyncaTaskApiCall(FragmentSignUp signuplisenar,String body,Context context){
		this.signuplisenar=signuplisenar;
		this.body=body;
		this.context=context;
		jsonParser=new JsonParser();
		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
				context.getResources().getString(R.string.txt_please_wait), true);

	}
	public AsyncaTaskApiCall(FragmentLogin loginlisenar,String body,Context context){
		this.loginlisenar=loginlisenar;
		this.body=body;
		this.context=context;
		jsonParser=new JsonParser();
		pd=ProgressDialog.show(context,  context.getResources().getString(R.string.app_name),
				context.getResources().getString(R.string.txt_please_wait), true);
	}
	@Override
	protected ServerResponse doInBackground(Void... params) {
		ServerResponse response=null;
		if(signuplisenar!=null){
			String url = Constants.baseurl+"reg";
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
					body, null);
		}
		else if(seatmetlisenar!=null){
			String url = Constants.baseurl+"seatmatelist/"+bpass.getCarrier()+"/"+bpass.getFlight_no()+"/"
					+bpass.getJulian_date();
			url=url.replace(" ", "");
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
					body, null);
		}
		//singlebpasslisenar
		else if(sendmessgae!=null){
			//sendmessgae
			String url = Constants.baseurl+"messagemate/"+this.receiverid;;
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
					body, null);
		}
		else if(forgotlisenar!=null){
			String url = Constants.baseurl+"passreset/en";
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
					body, null);
		}
		else if(loginlisenar!=null){
			String url = Constants.baseurl+"login";
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
					body, null);
		}
		else if(netstatelisenaer!=null){
			
			String url = Constants.baseurl+"newbp";
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
					body, null);
		}
		else if(netstatelisenaer!=null){
			
			String url = Constants.baseurl+"newbp";
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
					body, null);
		}
		else if(bpassaddlisenar!=null){
		
			String url = Constants.baseurl+"newbp";
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
					body, null);
		}
		else if(myaccountlisenar!=null){

			String url = Constants.baseurl+myaccounturl;
			if(myaccounturl.equals("reg")){
				response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url, null,
						body, null);
			}
			else{
				response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
						body, null);
			}

		}
		//07-10 21:16:50.479: W/System.err(14670): java.io.FileNotFoundException: 

		else if(passwordchangelisenar!=null){

			String url = Constants.baseurl+myaccounturl;
			
				response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url, null,
						body, null);
		}
		
		else if(usernameLisenar!=null){

			String url = Constants.baseurl+myaccounturl;
			
				response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url, null,
						body, null);
		}
		else if(deletelisenar!=null){

			String url = Constants.baseurl+myaccounturl;
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
						body, null);
		}
		else if(deletelisenarfromnetstate!=null){

			String url = Constants.baseurl+myaccounturl;
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
						body, null);
		}
		
		
		else if(retreivelisenar!=null){

			String url = Constants.baseurl+"bplist";
			
				response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
						body, null);
		}
		return response;

	}

	@Override
	protected void onPostExecute(ServerResponse result) {
		super.onPostExecute(result);
		UserCred ucrCred=new UserCred("", "", "", "", "", "", "", "", "", "", "", "", "", "");
		if( pd!=null){ 
			if(pd.isShowing()){
				pd.cancel();
			}
		}
		if(signuplisenar!=null){
			signuplisenar.callBackFromApicall(result);
		}
		else if(loginlisenar!=null){
			loginlisenar.callBackFromApicall(result);
		}
		else if(netstatelisenaer!=null){
			netstatelisenaer.addBoardingPassonBackendSuccess(result.getjObj(),bpass);
		}
		else if(bpassaddlisenar!=null){
			bpassaddlisenar.addBoardingPassonBackendSuccess(result.getjObj());
		}
		else if((myaccountlisenar!=null)&&(myaccounturl.equals("logout"))){
			JSONObject job=result.getjObj();
			try {
				if(job.get("success").equals("true")){
					myaccountlisenar.getActivity().finish();
					appInstance.setUserCred(ucrCred);
					SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
					dbInstance.open();
					dbInstance.droptableBoardingPassDbManager();
					dbInstance.createtableBoardingPassDbManager();
					dbInstance.close();
					Toast.makeText(context, context.getResources().getString(R.string.txt_logout_success),
							Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(context, context.getResources().getString(R.string.txt_logout_failed),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else if((myaccountlisenar!=null)&&(myaccounturl.equals("reg"))){
			JSONObject job=result.getjObj();
			try {
				if(job.getString("success").equals("true")){
					appInstance.setUserCred(ucrCred);
					
					String imageurl=job.getString("image_url");
					myaccountlisenar.successfullyUpdateYourProfile(imageurl);
				}
				else{
					Constants.photo=null;
					Toast.makeText(context, context.getResources().getString(R.string.txt_update_failed),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		//usernameLisenar
		else if(passwordchangelisenar!=null){
			JSONObject job=result.getjObj();
			try {
				if(job.getString("success").equals("true")){
					appInstance.setUserCred(ucrCred);
					Toast.makeText(context, context.getResources().getString(R.string.txt_update_success),
							Toast.LENGTH_SHORT).show();
					passwordchangelisenar.SuccessUpdateProfile();
				}
				else{
					
					Toast.makeText(context, context.getResources().getString(R.string.txt_update_failed),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else if(retreivelisenar!=null){
			JSONObject job=result.getjObj();
			try {
				if(job.getString("success").equals("true")){
					BoardingPassList  list=BoardingPassList.getBoardingPassListObject(job);
					retreivelisenar.lodedLodaedfromServer(list.getBoardingPassList());
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
		else if(deletelisenar!=null){
			JSONObject job=result.getjObj();
			try {
				if(job.getString("success").equals("true")){
					BoardingPassList  list=BoardingPassList.getBoardingPassListObject(job);
					deletelisenar.updateDatabaseWithoutServernotification(1);
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
		
		//deletelisenarfromnetstate
		else if(usernameLisenar!=null){
			JSONObject job=result.getjObj();
			try {
				if(job.getString("success").equals("true")){
					appInstance.setUserCred(ucrCred);
					Toast.makeText(context, context.getResources().getString(R.string.txt_update_success),
							Toast.LENGTH_SHORT).show();
					usernameLisenar.SuccessUpdateProfile();
				}
				else{
					
					Toast.makeText(context, context.getResources().getString(R.string.txt_update_failed),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		//seatmetlisenar
		
		else if(seatmetlisenar!=null){
			JSONObject job=result.getjObj();
		try {
				if(job.getString("success").equals("true")){
					
					
					SeatMetList  seatmet_listlist=SeatMetList.getSeatmetListObj(job);
					seatmetlisenar.callBackSeatmetList(seatmet_listlist);
				}
				else{
					Toast.makeText(context, context.getResources().getString(R.string.txt_getseatmate_failure),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else if(forgotlisenar!=null){
			JSONObject job=result.getjObj();
		try {
				if(job.getString("success").equals("true")){
					Toast.makeText(context, context.getResources().getString(R.string.txt_emailsen_success),
							Toast.LENGTH_SHORT).show();
					forgotlisenar.SuccessGetNewPass();
				}
				else{
					Toast.makeText(context, context.getResources().getString(R.string.txt_emailsen_failure),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else if(sendmessgae!=null){
			JSONObject job=result.getjObj();
		try {
				if(job.getString("success").equals("true")){
					Toast.makeText(context, context.getResources().getString(R.string.txt_emailsent_success),
							Toast.LENGTH_SHORT).show();
					sendmessgae.Successmessagesent();
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
		
	}


}