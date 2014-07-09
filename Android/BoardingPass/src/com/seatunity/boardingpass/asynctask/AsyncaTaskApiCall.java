package com.seatunity.boardingpass.asynctask;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.seatunity.apicall.JsonParser;
import com.seatunity.boardingpass.EditUserNameActivity;
import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.PasswordChangeActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.fragment.FragmentBoardingPasses;
import com.seatunity.boardingpass.fragment.FragmentLogin;
import com.seatunity.boardingpass.fragment.FragmentMyAccount;
import com.seatunity.boardingpass.fragment.FragmentSignUp;
import com.seatunity.boardingpass.networkstatetracker.NetworkStateReceiver;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassList;
import com.seatunity.model.Member;
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
Log.e("response", "ab "+result.getjObj().toString());
		UserCred ucrCred=new UserCred("", "", "", "", "", "", "", "", "", "", "", "", "", "");
		if( pd!=null){ 
			if(pd.isShowing()){
				pd.cancel();
			}
			//07-07 21:37:59.678: E/ImageLoader(28304): 

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

	}


}