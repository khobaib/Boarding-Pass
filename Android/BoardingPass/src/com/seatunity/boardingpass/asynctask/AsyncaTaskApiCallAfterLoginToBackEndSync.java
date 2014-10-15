package com.seatunity.boardingpass.asynctask;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.seatunity.apicall.JsonParser;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.networkstatetracker.NetworkStateReceiver;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.ServerResponse;

/**
 * The {@link AsyncTask} to handle the back-end data sending to the API
 * 
 * @author Sumon
 * 
 */
public class AsyncaTaskApiCallAfterLoginToBackEndSync extends AsyncTask<Void, Void, ServerResponse> {
	String body;
	BoardingPassApplication appInstance;
	String myaccounturl;
	JsonParser jsonParser;
	private static ProgressDialog pd;
	Context context;
	BoardingPass bpass;
	String addedurl;
	String receiverid;
	int requestType;
	boolean redundantLoginState = false;
	CallBackApiCall CaBLisenar;
	NetworkStateReceiver netstatelisenaer;
	NetworkStateReceiver deletelisenarfromnetstate;
	int count = 0;
	ArrayList<BoardingPass> listnotsynced;

	/**
	 * @param context
	 */
	public AsyncaTaskApiCallAfterLoginToBackEndSync(Context context) {
		this.context = context;
		SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
		dbInstance.open();
		ArrayList<BoardingPass> list = (ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
		dbInstance.close();
		listnotsynced = new ArrayList<BoardingPass>();
		for (BoardingPass Bpass : list) {
			if (Bpass.getId().equals("-1")) {
				listnotsynced.add(Bpass);
			}
		}

	}

	@Override
	protected ServerResponse doInBackground(Void... params) {
		ServerResponse response = null;

		if (netstatelisenaer != null) {

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

		if(result.getStatus()!=200){
			CaBLisenar.responseFailure(result);
			return;
		}
		if (netstatelisenaer != null) {
			netstatelisenaer.addBoardingPassonBackendSuccess(result.getjObj(), bpass);
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
					// BoardingPassList list =
					// BoardingPassList.getBoardingPassListObject(job);
					deletelisenarfromnetstate.updateDatabaseWithoutServernotification(bpass);
				} else {
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}