package com.seatunity.boardingpass.asynctask;

import org.json.JSONException;
import org.json.JSONObject;

import com.seatunity.apicall.JsonParser;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.fragment.FragmentAddBoardingPass;
import com.seatunity.boardingpass.fragment.FragmentLogin;
import com.seatunity.boardingpass.fragment.FragmentSignUp;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.Member;
import com.seatunity.model.ServerResponse;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;



 public class AsyncaTaskApiCall extends AsyncTask<Void, Void, ServerResponse> {
	 FragmentSignUp signuplisenar;
	 FragmentLogin loginlisenar;
	 FragmentAddBoardingPass bpassaddlisenar;
	 String body;
	 JsonParser jsonParser;
	 ProgressDialog pd;
	 Context context;
	 public AsyncaTaskApiCall(FragmentAddBoardingPass bpassaddlisenar,String body,Context context){
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
		else if(bpassaddlisenar!=null){
			String url = Constants.baseurl+"newbp";
			response =jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
					body, null);
		}
		return response;

	}

	@Override
	protected void onPostExecute(ServerResponse result) {
		super.onPostExecute(result);
		Log.e("responses", result.getjObj().toString());
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
		else if(bpassaddlisenar!=null){
			bpassaddlisenar.addBoardingPassonBackendSuccess(result.getjObj());
		}
		
	}
	
	
}