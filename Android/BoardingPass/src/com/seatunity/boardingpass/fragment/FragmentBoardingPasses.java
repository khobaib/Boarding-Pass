package com.seatunity.boardingpass.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.adapter.AdapterForBoardingPass;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.SeatMetList;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;

/**
 * This fragment loads & shows the saved boarding-passes from the local-DB.
 * 
 * @author Sumon
 * 
 */
@SuppressLint("NewApi")
public class FragmentBoardingPasses extends Fragment implements CallBackApiCall {

	private final String TAG = this.getClass().getSimpleName();

	public HomeListFragment parent;
	// private EditText et_email, et_password;
	// private TextView tv_errorshow;
	// private Button bt_login;
	// private AsyncaTaskApiCall retreive;
	private ArrayList<BoardingPass> futureBPList;
	// private Button btn_boarding_pass, btn_seatmate;
	// private String email, password;
	// private Context context;
	private BoardingPass highlitedboardingpass;
	private BoardingPassApplication appInstance;
	private ListView lv_boarding_pass;

	private TextView tv_from, tv_to, tv_month_inside_icon, tv_date_inside_icon, tv_cdg, tv_jfk, tv_seat_no,
			tv_flight_no;
	private MainActivity landingActivity;

	// tv_start_time, tv_arrival_time;

	// /**
	// * Constructor intializing the boarding-pass list as passed in the param.
	// *
	// * @param list_greaterthan
	// * The boarding-pass list to show in the list-view.
	// */
	// public FragmentBoardingPasses(ArrayList<BoardingPass> list_greaterthan) {
	// this.list_greaterthan = list_greaterthan;
	// }
	//
	// /**
	// * Empty constructor doing nothing special inside.
	// */
	// public FragmentBoardingPasses() {
	// }
	/**
	 * Constructor intializing the boarding-pass list as passed in the param.
	 * 
	 */
	public static FragmentBoardingPasses newInstance() {
		return new FragmentBoardingPasses();
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance = (BoardingPassApplication) getActivity().getApplication();
		// context = getActivity();
		landingActivity = (MainActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// futureBPList = (ArrayList<BoardingPass>)
		// this.getArguments().getSerializable("bplist");
		Log.i(TAG, "onCreateView");
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_boarding_passes, container, false);
		lv_boarding_pass = (ListView) v.findViewById(R.id.lv_boarding_pass);
		tv_cdg = (TextView) v.findViewById(R.id.tv_cdg);
		tv_jfk = (TextView) v.findViewById(R.id.tv_jfk);
		tv_from = (TextView) v.findViewById(R.id.tv_from);
		tv_to = (TextView) v.findViewById(R.id.tv_to);
		tv_month_inside_icon = (TextView) v.findViewById(R.id.tv_month_inside_icon);
		tv_date_inside_icon = (TextView) v.findViewById(R.id.tv_date_inside_icon);
		tv_seat_no = (TextView) v.findViewById(R.id.tv_seat_no);
		tv_flight_no = (TextView) v.findViewById(R.id.tv_flight_no);
		// tv_start_time = (TextView) v.findViewById(R.id.tv_start_time);
		// tv_arrival_time = (TextView) v.findViewById(R.id.tv_arrival_time);
		v.findViewById(R.id.btn_seatmate).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((Constants.isOnline(getActivity())) && (!appInstance.getUserCred().getEmail().equals(""))) {
					callSeatmet();
				} else {
					showAlertMessage();
				}
			}
		});
		v.findViewById(R.id.btn_boarding_pass).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (futureBPList.size() > 0) {
					parent.startUpCommingBoardingDetails(highlitedboardingpass);
				}
			}
		});
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
		dbInstance.open();
		futureBPList = (ArrayList<BoardingPass>) dbInstance.retrieveFutureBoardingPassList();
		dbInstance.close();
		setListViewWithSizeChecks();
		landingActivity.mTitle = landingActivity.getResources().getString(R.string.app_name_seatunity);
		landingActivity.getActionBar().setTitle(landingActivity.mTitle);
		// ArrayList<BoardingPass> list = (ArrayList<BoardingPass>)
		// dbInstance.retrieveBoardingPassList();
		// dbInstance.close();
		// Calendar c = Calendar.getInstance();
		// int dayofyear = c.get(Calendar.DAY_OF_YEAR);
		// futureBPList = new ArrayList<BoardingPass>();
		// futureBPList.clear();
		//
		// for (int i = 0; i < list.size(); i++) {
		// Log.e("test", "t " + list.get(i).getTravel_from_name());
		// int ju_date = Integer.parseInt(list.get(i).getJulian_date().trim());
		//
		// if ((ju_date >= dayofyear) && (!list.get(i).getDeletestate())) {
		// futureBPList.add(list.get(i));
		// }
		// }
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("bpass", highlitedboardingpass);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			highlitedboardingpass = (BoardingPass) savedInstanceState.getSerializable("bpass");
			setDetailsBoaredingpass(highlitedboardingpass);
		}
	}

	/**
	 * Load & show the details of the seat-mates related to the gloabal variable
	 * - {@code highlitedboardingpass}.
	 */
	public void callSeatmet() {
		String extendedurl = "seatmatelist/" + highlitedboardingpass.getCarrier() + "/"
				+ highlitedboardingpass.getFlight_no() + "/" + highlitedboardingpass.getJulian_date();
		extendedurl = extendedurl.replace(" ", "");
		AsyncaTaskApiCall get_list = new AsyncaTaskApiCall(this, getJsonObjet(), getActivity(), extendedurl,
				Constants.REQUEST_TYPE_POST);
		get_list.execute();
	}

	/**
	 * Shows an alert when the seat-mate data show is prompted but there's no
	 * internet.
	 */
	public void showAlertMessage() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setMessage(getResources().getString(R.string.txt_seatmet_message_only_online))
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	/**
	 * Initiates the boarding-pass list.
	 */
	public void setListViewWithSizeChecks() {
		if (futureBPList != null) {
			if (futureBPList.size() > 0) {
				AdapterForBoardingPass adapter = new AdapterForBoardingPass(getActivity(), futureBPList);
				lv_boarding_pass.setAdapter(adapter);
			} else {
				if (appInstance.isRememberMe()) {
					parent.backEndStack.pop();
					parent.startAddBoardingPassDuringLogin();
				} else {
					parent.backEndStack.pop();
					parent.startHomeFragment();
				}
			}
		}

		lv_boarding_pass.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentView, View v, int position, long id) {
				highlitedboardingpass = futureBPList.get(position);
				setDetailsBoaredingpass(highlitedboardingpass);
			}
		});
		if (highlitedboardingpass == null) {
			highlitedboardingpass = futureBPList.get(0);
		}
		setDetailsBoaredingpass(highlitedboardingpass);
	}

	/**
	 * @return a JSON-formatted string to pass to the seat-mate API.
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
	 * Sets the top view of the page with the data of passed boarding-pass.
	 * 
	 * @param bpass
	 *            The data of whih are to be shown in details.
	 */
	public void setDetailsBoaredingpass(BoardingPass bpass) {
		String date = Constants.getDayandYear(Integer.parseInt(bpass.getJulian_date().trim()));
		String[] dateParts = date.split(":");
		String month = dateParts[1];
		String dateofmonth = dateParts[0];
		tv_cdg.setText(bpass.getTravel_from());
		tv_jfk.setText(bpass.getTravel_to());
		tv_from.setText(bpass.getTravel_from_name());
		tv_to.setText(bpass.getTravel_to_name());
		tv_month_inside_icon.setText(month);
		tv_date_inside_icon.setText(dateofmonth);

		tv_seat_no.setText(landingActivity.getResources().getString(R.string.txt_seat_nno) + " "
				+ Constants.removeingprecingZero(bpass.getSeat()));
		tv_flight_no.setText(landingActivity.getResources().getString(R.string.txt_flight_no) + " "
				+ bpass.getCarrier() + bpass.getFlight_no());
	}

	private void showNoSeatmateDialog() {
		// Log.d(TAG, "Sign up OK dialog");
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_dilog);
		Button btnOK = (Button) dialog.findViewById(R.id.ok);
		TextView tv = (TextView) dialog.findViewById(R.id.tv_success);
		tv.setText("No seatmate is found with this boarding-pass!");
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Log.d(TAG, "Sign up OK dialog : on-click");
				dialog.dismiss();
				if (dialog.isShowing())
					dialog.cancel();
			}
		});
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.CENTER;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);
		dialog.show();
	}

	@Override
	public void responseOk(JSONObject job) {
		try {
			if (job.getString("success").equals("true")) {

				SeatMetList seatmet_listlist = SeatMetList.getSeatmetListObj(job);
				if (seatmet_listlist.getAllSeatmateList().size() > 0) {
					parent.startSeatmetList(seatmet_listlist, highlitedboardingpass);
				} else {
					showNoSeatmateDialog();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void responseFailure(JSONObject job) {
		try {
			Toast.makeText(getActivity(), job.getString("message"), Toast.LENGTH_SHORT).show();
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
				String extendedurl = "seatmatelist/" + highlitedboardingpass.getCarrier() + "/"
						+ highlitedboardingpass.getFlight_no() + "/" + highlitedboardingpass.getJulian_date();
				extendedurl = extendedurl.replace(" ", "");
				AsyncaTaskApiCall get_list = new AsyncaTaskApiCall(FragmentBoardingPasses.this, getJsonObjet(),
						landingActivity, extendedurl, Constants.REQUEST_TYPE_POST);
				get_list.execute();

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
			Toast.makeText(landingActivity, message, Toast.LENGTH_SHORT).show();
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
