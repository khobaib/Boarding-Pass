package com.seatunity.boardingpass.asynctask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

import com.seatunity.apicall.JsonParser;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.networkstatetracker.NetworkStateReceiver;
import com.seatunity.boardingpass.networkstatetracker.SyncLocalDbtoBackend;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.ServerResponse;

/**
 * The mammoth {@link AsyncTask} to handle all the asynchronous operations
 * inside the app, esp. the user-driven operations.
 * 
 * @author Sumon
 * 
 */
@SuppressLint("InflateParams")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AsyncaTaskApiCall extends AsyncTask<Void, Void, ServerResponse> {
	private String body;
	// private BoardingPassApplication appInstance;
	private String myaccounturl;
	private JsonParser jsonParser;
	private static Dialog waitDialog;
	// Context context;
	private BoardingPass bpass;
	private String addedurl;
	// private String receiverid;
	private int requestType;
	private boolean redundantLoginState = false;
	private CallBackApiCall cbListenar;
	private SyncLocalDbtoBackend localDbSyncer;
	private NetworkStateReceiver deletelisenarfromnetstate;

	private NetworkStateReceiver netstatelisenaer;

	/**
	 * @param CaBLisenar
	 * @param body
	 * @param context
	 * @param addedurl
	 * @param requestType
	 * @param redundantLoginState
	 */
	public AsyncaTaskApiCall(CallBackApiCall CaBLisenar, String body,
			Context context, String addedurl, int requestType,
			boolean redundantLoginState) {
		this.body = body;
		// this.appInstance = appInstance;
		// this.myaccounturl = myaccounturl;
		// this.context = context;
		this.addedurl = addedurl;
		this.cbListenar = CaBLisenar;
		this.requestType = requestType;
		jsonParser = new JsonParser();
		this.redundantLoginState = redundantLoginState;
		// try {
		// if (pd == null)
		// pd = new ProgressDialog(context);
		// if (!pd.isShowing())
		// pd.show();
		// pd.setCancelable(true);
		// pd.setContentView(R.layout.progress_content);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	/**
	 * @param deletelisenarfromnetstate
	 * @param body
	 * @param context
	 * @param myaccounturl
	 * @param bpass
	 */
	public AsyncaTaskApiCall(NetworkStateReceiver deletelisenarfromnetstate,
			String body, Context context, String myaccounturl,
			BoardingPass bpass) {
		this.bpass = bpass;
		this.deletelisenarfromnetstate = deletelisenarfromnetstate;
		this.body = body;
		// this.appInstance = appInstance;
		this.myaccounturl = myaccounturl;
		// this.context = context;
		jsonParser = new JsonParser();
		try {
			if (waitDialog != null && waitDialog.isShowing())
				waitDialog.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param CaBLisenar
	 * @param body
	 * @param activity
	 * @param addedurl
	 * @param requestType
	 */
	public AsyncaTaskApiCall(CallBackApiCall CaBLisenar, String body,
			Activity activity, String addedurl, int requestType) {
		this.body = body;
		// this.appInstance = appInstance;
		// this.myaccounturl = myaccounturl;
		// this.context = context;
		this.addedurl = addedurl;
		this.cbListenar = CaBLisenar;
		this.requestType = requestType;
		jsonParser = new JsonParser();
		showDialog(activity);
	}

	/**
	 * @param activity
	 */
	private void showDialog(Activity activity) {
		try {
			if (waitDialog == null) {
				waitDialog = new Dialog(activity,
						android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
			}
			waitDialog.setContentView(R.layout.progress_content);
			
			Window window = waitDialog.getWindow();
			window.setLayout(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			window.setGravity(Gravity.CENTER);

			// The below code is EXTRA - to dim the parent view by 70% :D
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.dimAmount = 0.7f;
			lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(lp);
			
			if (waitDialog.isShowing())
				return;
			if (Looper.getMainLooper() == Looper.myLooper()
					&& !activity.isFinishing()) {
				waitDialog.show();
			} else
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!waitDialog.isShowing())
							waitDialog.show();
					}
				});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param netstatelisenaer
	 * @param bpass
	 * @param body
	 * @param context
	 */
	public AsyncaTaskApiCall(NetworkStateReceiver netstatelisenaer,
			BoardingPass bpass, String body, Context context) {
		this.netstatelisenaer = netstatelisenaer;
		this.body = body;
		// this.context = context;
		jsonParser = new JsonParser();
		this.bpass = bpass;

	}

	/**
	 * @param localDbSyncListener
	 * @param bpass
	 * @param body
	 * @param context
	 */
	public AsyncaTaskApiCall(SyncLocalDbtoBackend localDbSyncListener,
			BoardingPass bpass, String body, Context context) {
		this.localDbSyncer = localDbSyncListener;
		this.body = body;
		// this.context = context;
		jsonParser = new JsonParser();
		this.bpass = bpass;

	}

	@Override
	protected ServerResponse doInBackground(Void... params) {
		ServerResponse response = null;

		if (netstatelisenaer != null) {
			String url = Constants.baseurl + "newbp";
			response = jsonParser.retrieveServerData(
					Constants.REQUEST_TYPE_POST, url, null, body, null);
		} else if (localDbSyncer != null) {
			String url = Constants.baseurl + "newbp";
			response = jsonParser.retrieveServerData(
					Constants.REQUEST_TYPE_POST, url, null, body, null);
		} else if (netstatelisenaer != null) {
			String url = Constants.baseurl + "newbp";
			response = jsonParser.retrieveServerData(
					Constants.REQUEST_TYPE_POST, url, null, body, null);
		} else if (deletelisenarfromnetstate != null) {
			String url = Constants.baseurl + myaccounturl;
			response = jsonParser.retrieveServerData(
					Constants.REQUEST_TYPE_POST, url, null, body, null);
		} else if (cbListenar != null) {
			String url = Constants.baseurl + addedurl;
			response = jsonParser.retrieveServerData(requestType, url, null,
					body, null);
		}
		return response;

	}

	@Override
	protected void onPostExecute(ServerResponse result) {
		super.onPostExecute(result);

		try {
			if (waitDialog != null && waitDialog.isShowing()) {
				waitDialog.cancel();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result.getStatus() != 200) {
			cbListenar.responseFailure(result);
			return;
		}
		if (netstatelisenaer != null) {
			netstatelisenaer.addBoardingPassonBackendSuccess(result.getjObj(),
					bpass);
		} else if (localDbSyncer != null) {
			localDbSyncer.addBoardingPassonBackendSuccess(result.getjObj(),
					bpass);
		} else if (cbListenar != null) {
			if (redundantLoginState) {
				JSONObject job = result.getjObj();
				try {
					if (job.getString("success").equals("true")) {
						cbListenar.saveLoginCred(job);
					} else {
						cbListenar.LoginFailed(job);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				JSONObject job = result.getjObj();
				try {
					if (job.getString("success").equals("true")) {
						cbListenar.responseOk(job);
					} else {
						cbListenar.responseFailure(job);
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
					deletelisenarfromnetstate
							.updateDatabaseWithoutServernotification(bpass);
				} else {
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}