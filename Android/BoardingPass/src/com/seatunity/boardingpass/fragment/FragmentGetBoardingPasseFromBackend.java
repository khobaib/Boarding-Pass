package com.seatunity.boardingpass.fragment;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassList;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;

/**
 * This fragment retrieves boarding-pass list from the server if the device is
 * connected to the internet, otherwise it loads the boarding-passes from the
 * local-db. Finally, it shows the list.
 * 
 * @author Sumon
 * 
 */
@SuppressLint("NewApi")
public class FragmentGetBoardingPasseFromBackend extends Fragment implements CallBackApiCall {

	private final String TAG = this.getClass().getSimpleName();

	public HomeListFragment parent;
	// private EditText et_email, et_password;
	// private TextView tv_errorshow;
	// private Button bt_login;
	private AsyncaTaskApiCall apiCaller;
	private ArrayList<BoardingPass> futureBoardingPassList;
	// private Button btn_boarding_pass, btn_seatmate;
	// private String email, password;
	private Context context;
	// private BoardingPass highlitedboardingpass;
	private BoardingPassApplication appInstance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		// Log.e("again", "onCreate");
		appInstance = (BoardingPassApplication) getActivity().getApplication();
		if (parent == null) {
			parent = Constants.parent;
		}

		SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
		dbInstance.open();
		futureBoardingPassList = (ArrayList<BoardingPass>) dbInstance.retrieveFutureBoardingPassList();
		dbInstance.close();
		if (Constants.isOnline(getActivity())) {
			Log.d(TAG, "User remembered: " + appInstance.isRememberMe());
			if (!appInstance.isRememberMe()) {
				int sz = futureBoardingPassList.size();
				if (sz < 1) {
					Log.e(TAG, "futureBoardingPassList.size() = " + sz + ", so starting HomeFragment");
					parent.startHomeFragment();
				} else {
					Log.i(TAG, "futureBoardingPassList.size()=" + sz + ", so starting FragmentBoardingPasses");
					parent.startFragmentBoardingPasses();
				}
			}
			// User is registered, so try to retrieve his/her bpasses from
			// server
			else {
				apiCaller = new AsyncaTaskApiCall(this, getJsonObjet(), getActivity(), "bplist",
						Constants.REQUEST_TYPE_POST);
				apiCaller.execute();
			}
		} else {
			Log.e(TAG, "Net connection is disabled");
			

			if (appInstance.isRememberMe()) {
				Log.e(TAG, "appInstance.isRememberMe()=true");
				if (futureBoardingPassList.size() < 1) {
					parent.startAddBoardingPassDuringLogin();
					Log.e(TAG, "list_greaterthan.size() < 1");
				} else {
					Log.e(TAG, "list_greaterthan.size() >= 1");
					parent.startFragmentBoardingPasses();
				}
			} else {
				Log.e(TAG, "appInstance.isRememberMe()=false");
				if (futureBoardingPassList.size() < 1) {
					Log.e(TAG, "list_greaterthan.size() < 1");
					parent.startHomeFragment();
				} else {
					Log.e(TAG, "list_greaterthan.size() >= 1");
					parent.startFragmentBoardingPasses();
				}
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getActivity() != null) {
			context = getActivity();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_get_bpass_from_backend, container, false);
		Log.i(TAG, "onCreateView");
		return v;
	}

	/**
	 * @return A JSON-formatted string to update the log-in credential in the
	 *         server.
	 */
	public String getJsonObjet() {
		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token", appInstance.getUserCred().getToken());
			loginObj.put("boarding_pass", "all");
			return loginObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Calls the API to save the passed boarding-pass object in server.
	 * 
	 * @param bpass
	 */
	@SuppressWarnings("unused")
	private void saveBoardingPasstoServer(BoardingPass bpass) {
		Log.d(TAG, "saveBoardingPasstoServer : inside");
		// needtoReusedBoardingPass = bpass;
		String bpassdata = "";
		bpassdata = getJsonOfBoardingPass(bpass);
		AsyncaTaskApiCall upBPass = new AsyncaTaskApiCall(this, bpassdata, getActivity(), "newbp",
				Constants.REQUEST_TYPE_POST);
		upBPass.execute();
	}

	/**
	 * Forms a JSON-string to send boarding-pass data to save at the server.
	 * 
	 * @param bpass
	 *            The boarding-pass to save in the server.
	 * @return
	 */
	private String getJsonOfBoardingPass(BoardingPass bpass) {
		JSONObject bPassObj = new JSONObject();
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		try {
			bPassObj.put("token", appInstance.getUserCred().getToken());
			bPassObj.put("version", "1");
			bPassObj.put("stringform", bpass.getStringform());
			bPassObj.put("firstname", bpass.getFirstname());
			bPassObj.put("lastname", bpass.getLastname());
			bPassObj.put("PNR", bpass.getPNR());
			bPassObj.put("travel_from", bpass.getTravel_from());
			bPassObj.put("travel_to", bpass.getTravel_to());
			bPassObj.put("carrier", bpass.getCarrier());
			bPassObj.put("flight_no", bpass.getFlight_no());
			bPassObj.put("julian_date", bpass.getJulian_date().trim());
			bPassObj.put("compartment_code", bpass.getCompartment_code());
			bPassObj.put("seat", bpass.getSeat());
			bPassObj.put("departure", bpass.getDeparture());
			bPassObj.put("arrival", bpass.getArrival());
			bPassObj.put("year", year);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bPassObj.toString();
	}

	private ArrayList<BoardingPass> isolateFutureBPass(ArrayList<BoardingPass> allBPassList) {
		Calendar c = Calendar.getInstance();
		int dayofyear = c.get(Calendar.DAY_OF_YEAR);
		ArrayList<BoardingPass> fbpList = new ArrayList<BoardingPass>();
		for (int i = 0; i < allBPassList.size(); i++) {
			Log.i(TAG, "Traveler name: " + allBPassList.get(i).getTravel_from_name());
			int ju_date = Integer.parseInt(allBPassList.get(i).getJulian_date().trim());
			if ((ju_date >= dayofyear) && (!allBPassList.get(i).getDeletestate())) {
				fbpList.add(allBPassList.get(i));
			}
		}
		return fbpList;
	}

	@Override
	public void responseOk(JSONObject job) {
		ArrayList<BoardingPass> allBoardingPassList;
		try {
			if (job.getString("success").equals("true")) {
				allBoardingPassList = BoardingPassList.getBoardingPassListObject(job).getBoardingPassList();
				SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
				Log.i(TAG, "Server  has retrieved BPass list ... Now saving BPass in local DB.");
				dbInstance.open();
				for (int i = 0; i < allBoardingPassList.size(); i++) {
					Log.d(TAG, "Inserting: " + i + " : BPass For Travel From: "
							+ allBoardingPassList.get(i).getTravel_from_name());
					dbInstance.insertOrUpdateBoardingPass(allBoardingPassList.get(i));
				}
				// isolate future b-pass from all b-pass
				ArrayList<BoardingPass> fbpList = isolateFutureBPass(allBoardingPassList);
				if (fbpList.size() < futureBoardingPassList.size()) {
					// TO_DO upload the unsynched local boarding passes to
					// server
					Log.e(TAG, "Upload the UNSYNCHED local boarding passes to server");
					// Log.e(TAG,
					// "UNSYNCHED B.PASS in LOCAL DB !!!!!!\nSynchronizing with remote ...");
					// for (BoardingPass boardingPass : futureBoardingPassList)
					// {
					// if (!fbpList.contains(boardingPass)) {
					// saveBoardingPasstoServer(boardingPass);
					// }
					// }
				} else {
					futureBoardingPassList = (ArrayList<BoardingPass>) dbInstance.retrieveFutureBoardingPassList();
				}
				Log.i(TAG, "Got B-pass list as: " + futureBoardingPassList.size());
				if (futureBoardingPassList.size() < 1) {
					// TODO Look for past list
					ArrayList<BoardingPass> pBpList = (ArrayList<BoardingPass>) dbInstance
							.retrievePastBoardingPassList();
					if (pBpList.size() > 0 && BoardingPassApplication.getHookCallMode()){
						parent.startPastBPassFragmment();
						BoardingPassApplication.setHookCallMode(false);
					}
					else
						parent.startAddBoardingPassDuringLogin();
				} else {
					parent.startFragmentBoardingPasses();
				}
				dbInstance.close();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void responseFailure(JSONObject job) {
		Log.e(TAG, "Response failure: " + job.toString());
		try {
			JSONObject joberror = new JSONObject(job.getString("error"));
			String code = joberror.getString("code");
			if (code.equals("x05")) {
				JSONObject loginObj = new JSONObject();
				loginObj.put("email", appInstance.getUserCred().getEmail());
				loginObj.put("password", appInstance.getUserCred().getPassword());
				String loginData = loginObj.toString();
				Log.i(TAG, "Log in re-submission");
				AsyncaTaskApiCall log_in_lisenar = new AsyncaTaskApiCall(FragmentGetBoardingPasseFromBackend.this,
						loginData, context, "login", Constants.REQUEST_TYPE_POST, true);
				log_in_lisenar.execute();
			} else {
				Toast.makeText(getActivity(), job.getString("message"), Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveLoginCred(JSONObject job) {
		try {
			UserCred userCred;
			String status = job.getString("success");
			if (status.equals("true")) {
				userCred = UserCred.parseUserCred(job);
				userCred.setEmail(appInstance.getUserCred().getEmail());
				userCred.setPassword(appInstance.getUserCred().getPassword());
				appInstance.setUserCred(userCred);
				appInstance.setRememberMe(true);
				// callfrom = 1;
				apiCaller = new AsyncaTaskApiCall(FragmentGetBoardingPasseFromBackend.this, getJsonObjet(), getActivity(),
						"bplist", Constants.REQUEST_TYPE_POST);
				apiCaller.execute();
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
			JSONObject joberror = new JSONObject(job.getString("error"));
			// String code =joberror.getString("code");
			Constants.setAllFlagFalse();
			String message = joberror.getString("message");
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void responseFailure(ServerResponse response) {
		if (response.getStatus() != 200) {
			BoardingPassApplication.alert(getActivity(), "Internet connectivity is lost! Please retry the operation.");
		}
	}
}
