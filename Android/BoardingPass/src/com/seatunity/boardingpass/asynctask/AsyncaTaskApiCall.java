package com.seatunity.boardingpass.asynctask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.seatunity.apicall.JsonParser;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.networkstatetracker.NetworkStateReceiver;
import com.seatunity.boardingpass.networkstatetracker.SyncLocalDbtoBackend;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassList;
import com.seatunity.model.ServerResponse;

/**
 * The mammoth {@link AsyncTask} to handle all the asynchronous operations inside
 * the app, esp. the user-driven operations.
 * 
 * @author Sumon
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AsyncaTaskApiCall extends AsyncTask<Void, Void, ServerResponse> {
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
	boolean redundantLoginState = false;
	CallBackApiCall CaBLisenar;
	SyncLocalDbtoBackend loginsuccesslisenar;
	NetworkStateReceiver deletelisenarfromnetstate;

	NetworkStateReceiver netstatelisenaer;

	/**
	 * @param CaBLisenar
	 * @param body
	 * @param context
	 * @param addedurl
	 * @param requestType
	 * @param redundantLoginState
	 */
	public AsyncaTaskApiCall(CallBackApiCall CaBLisenar, String body, Context context, String addedurl,
			int requestType, boolean redundantLoginState) {
		this.body = body;
		this.appInstance = appInstance;
		this.myaccounturl = myaccounturl;
		this.context = context;
		this.addedurl = addedurl;
		this.CaBLisenar = CaBLisenar;
		this.requestType = requestType;
		jsonParser = new JsonParser();
		this.redundantLoginState = redundantLoginState;
		pd = new ProgressDialog(context);
		pd.show();
		pd.setCancelable(true);
		pd.setContentView(R.layout.progress_content);

	}

	/**
	 * @param deletelisenarfromnetstate
	 * @param body
	 * @param context
	 * @param myaccounturl
	 * @param bpass
	 */
	public AsyncaTaskApiCall(NetworkStateReceiver deletelisenarfromnetstate, String body, Context context,
			String myaccounturl, BoardingPass bpass) {
		this.bpass = bpass;
		this.deletelisenarfromnetstate = deletelisenarfromnetstate;
		this.body = body;
		this.appInstance = appInstance;
		this.myaccounturl = myaccounturl;
		this.context = context;
		jsonParser = new JsonParser();

	}

	/**
	 * @param CaBLisenar
	 * @param body
	 * @param context
	 * @param addedurl
	 * @param requestType
	 */
	public AsyncaTaskApiCall(CallBackApiCall CaBLisenar, String body, Context context, String addedurl, int requestType) {
		this.body = body;
		this.appInstance = appInstance;
		this.myaccounturl = myaccounturl;
		this.context = context;
		this.addedurl = addedurl;
		this.CaBLisenar = CaBLisenar;
		this.requestType = requestType;
		jsonParser = new JsonParser();
		pd = new ProgressDialog(context);
		pd.show();
		pd.setContentView(R.layout.progress_content);
	}

	/**
	 * @param netstatelisenaer
	 * @param bpass
	 * @param body
	 * @param context
	 */
	public AsyncaTaskApiCall(NetworkStateReceiver netstatelisenaer, BoardingPass bpass, String body, Context context) {
		this.netstatelisenaer = netstatelisenaer;
		this.body = body;
		this.context = context;
		jsonParser = new JsonParser();
		this.bpass = bpass;

	}

	/**
	 * @param loginsuccesslisenar
	 * @param bpass
	 * @param body
	 * @param context
	 */
	public AsyncaTaskApiCall(SyncLocalDbtoBackend loginsuccesslisenar, BoardingPass bpass, String body, Context context) {
		this.loginsuccesslisenar = loginsuccesslisenar;
		this.body = body;
		this.context = context;
		jsonParser = new JsonParser();
		this.bpass = bpass;

	}

	@Override
	protected ServerResponse doInBackground(Void... params) {
		ServerResponse response = null;

		if (netstatelisenaer != null) {

			String url = Constants.baseurl + "newbp";
			response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null, body, null);
		}

		else if (loginsuccesslisenar != null) {

			String url = Constants.baseurl + "newbp";
			response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null, body, null);
		} else if (netstatelisenaer != null) {

			String url = Constants.baseurl + "newbp";
			response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null, body, null);
		} else if (deletelisenarfromnetstate != null) {

			String url = Constants.baseurl + myaccounturl;
			response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null, body, null);
		}

		else if (CaBLisenar != null) {

			String url = Constants.baseurl + addedurl;

			response = jsonParser.retrieveServerData(requestType, url, null, body, null);
		}
		return response;

	}

	@Override
	protected void onPostExecute(ServerResponse result) {
		super.onPostExecute(result);

		if (pd != null) {
			if (pd.isShowing()) {
				pd.cancel();
			}
		}

		if (netstatelisenaer != null) {
			netstatelisenaer.addBoardingPassonBackendSuccess(result.getjObj(), bpass);
		} else if (loginsuccesslisenar != null) {
			loginsuccesslisenar.addBoardingPassonBackendSuccess(result.getjObj(), bpass);
		} else if (CaBLisenar != null) {
			if (redundantLoginState) {
				JSONObject job = result.getjObj();
				try {
					if (job.getString("success").equals("true")) {
						CaBLisenar.saveLoginCred(job);

					} else {

						CaBLisenar.LoginFailed(job);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				JSONObject job = result.getjObj();
				try {
					if (job.getString("success").equals("true")) {
						CaBLisenar.responseOk(job);
					} else {

						CaBLisenar.responseFailure(job);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} else if (deletelisenarfromnetstate != null) {
			JSONObject job = result.getjObj();
			try {
				if (job.getString("success").equals("true")) {
					BoardingPassList list = BoardingPassList.getBoardingPassListObject(job);
					deletelisenarfromnetstate.updateDatabaseWithoutServernotification(bpass);
				} else {
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}