package com.seatunity.boardingpass.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.seatunity.apicall.JsonParser;
import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.adapter.AdapterForBoardingPassFromAlert;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassListForSharedFlight;
import com.seatunity.model.SeatMate;
import com.seatunity.model.UserCred;

/**
 * Fragment containing the details of & opeation related to a single seat-mate.
 * 
 * @author Sumon
 * 
 */
@SuppressLint("NewApi")
public class FragmentSingleSeatMet extends Fragment implements CallBackApiCall {
	HomeListFragment parent;
	ImageView img_prof_pic;
	BoardingPassApplication appInstance;
	SeatMate seatmate;
	JsonParser jsonParser;
	BoardingPass bpass;
	ListView lv_sharedflight;
	TextView tv_uname, tv_profession, tv_seat, tv_shared_flight, tv_status, tv_live_in, tv_age, tv_class,
			tv_sothn_about;
	ListView lv_seat_met_list;
	Button btn_seatmate;
	String hint;
	EditText input;
	AlertDialog d;
	MainActivity landingActivity;
	Context context;
	String savedMessage;
	private int callfrom = 0;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance = (BoardingPassApplication) getActivity().getApplication();
		jsonParser = new JsonParser();
		context = getActivity();
	}

	public FragmentSingleSeatMet(SeatMate seatmate, BoardingPass bpass) {
		Log.e("insideList4", bpass.getTravel_from_name());
		this.seatmate = seatmate;
		this.bpass = bpass;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getActivity() != null) {
			landingActivity = (MainActivity) getActivity();
		}
		landingActivity.mTitle = seatmate.getName();
		landingActivity.getActionBar().setTitle(landingActivity.mTitle);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (getActivity() != null) {
			landingActivity = (MainActivity) getActivity();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_single_seatmate, container, false);
		tv_uname = (TextView) v.findViewById(R.id.tv_uname);
		tv_profession = (TextView) v.findViewById(R.id.tv_profession);
		tv_shared_flight = (TextView) v.findViewById(R.id.tv_shared_flight);
		tv_status = (TextView) v.findViewById(R.id.tv_status);
		tv_age = (TextView) v.findViewById(R.id.tv_age);
		tv_class = (TextView) v.findViewById(R.id.tv_class);
		tv_sothn_about = (TextView) v.findViewById(R.id.tv_sothn_about);
		tv_seat = (TextView) v.findViewById(R.id.tv_seat);
		tv_live_in = (TextView) v.findViewById(R.id.tv_live_in);
		btn_seatmate = (Button) v.findViewById(R.id.btn_seatmate);
		img_prof_pic = (ImageView) v.findViewById(R.id.img_prof_pic);
		tv_shared_flight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					JSONObject loginObj = new JSONObject();
					loginObj.put("token", appInstance.getUserCred().getToken());
					if (Constants.isOnline(getActivity())) {
						callfrom = 2;
						AsyncaTaskApiCall getSharedFlight = new AsyncaTaskApiCall(FragmentSingleSeatMet.this, loginObj
								.toString(), context, "sharedflight/" + seatmate.getId(), Constants.REQUEST_TYPE_POST);
						getSharedFlight.execute();
					} else {
						Toast.makeText(getActivity(),
								getActivity().getResources().getString(R.string.txt_check_internet), Toast.LENGTH_SHORT)
								.show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		btn_seatmate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showAlertDilog();
			}
		});
		SetView();
		return v;
	}

	/**
	 * Initiate the view with the seat-mate's data
	 */
	public void SetView() {
		tv_uname.setText(seatmate.getName());
		tv_profession.setText(seatmate.getProfession());
		tv_seat.setText(seatmate.getTravel_class() + ", " + Constants.removeingprecingZero(seatmate.getSeat()));
		tv_status.setText(seatmate.getStatus());
		tv_live_in.setText(getActivity().getResources().getString(R.string.txt_live_in) + seatmate.getLive_in());
		tv_age.setText(getActivity().getResources().getString(R.string.txt_age) + " " + seatmate.getAge());
		tv_class.setText(getActivity().getResources().getString(R.string.txt_prefer_to) + seatmate.getSeating_pref());
		tv_sothn_about.setText(seatmate.getSome_about_you());
		if ((seatmate.getImage_url() != null) && (!seatmate.getImage_url().equals(""))) {
			ImageLoader.getInstance().displayImage(seatmate.getImage_url(), img_prof_pic);
		}
	}

	/**
	 * @return A JSON formatted string containing only the app-token.
	 */
	public String getJsonObjet() {

		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token", appInstance.getUserCred().getToken());
			return loginObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Dialog to send mail to the seat-mate
	 */
	public void showAlertDilog() {
		hint = getActivity().getResources().getString(R.string.txt_message);
		input = new EditText(getActivity());
		d = new AlertDialog.Builder(getActivity()).setView(input)
				.setPositiveButton(getActivity().getResources().getString(R.string.txt_ok), null) // Set
				.setNegativeButton(getActivity().getResources().getString(R.string.txt_cancel), null).create();
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		TextView title = (TextView) customTitleView.findViewById(R.id.tv_title);
		title.setText(getActivity().getResources().getString(R.string.txt_send_email));
		d.setCustomTitle(customTitleView);

		d.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String value = input.getText().toString();

						if ((value == null) || (value.equals(""))) {
							Toast.makeText(getActivity(),
									getActivity().getResources().getString(R.string.txt_please_enter) + " " + hint,
									Toast.LENGTH_SHORT).show();
						} else {
							d.cancel();
							try {
								JSONObject loginObj = new JSONObject();
								loginObj.put("token", appInstance.getUserCred().getToken());
								loginObj.put("message", value);
								savedMessage = value;
								if (Constants.isOnline(getActivity())) {
									callfrom = 1;
									AsyncaTaskApiCall sendmessage = new AsyncaTaskApiCall(FragmentSingleSeatMet.this,
											loginObj.toString(), getActivity(), "messagemate/" + seatmate.getId(),
											Constants.REQUEST_TYPE_POST);
									sendmessage.execute();
								} else {
									Toast.makeText(getActivity(),
											getActivity().getResources().getString(R.string.txt_check_internet),
											Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					}
				});
			}
		});
		d.show();
	}

	/**
	 * Shows a dialog containing the shared-flights with the selected seat-mate.
	 * 
	 * @param item
	 */
	public void showAlertDilogToshowSharedFlight(ArrayList<BoardingPass> item) {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		TextView title = (TextView) customTitleView.findViewById(R.id.tv_title);
		title.setText(getActivity().getResources().getString(R.string.txt_shared_flight_title));
		builderSingle.setCustomTitle(customTitleView);
		final AdapterForBoardingPassFromAlert arrayAdapter = new AdapterForBoardingPassFromAlert(getActivity(), item);

		builderSingle.setNegativeButton(getActivity().getResources().getString(R.string.txt_cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builderSingle.show();
	}

	@Override
	public void responseOk(JSONObject job) {
		// TODO Auto-generated method stub
		try {
			if (job.getString("success").equals("true")) {
				if (callfrom == 1) {
					Toast.makeText(context, context.getResources().getString(R.string.txt_emailsent_success),
							Toast.LENGTH_SHORT).show();

				} else if (callfrom == 2) {
					BoardingPassListForSharedFlight list = BoardingPassListForSharedFlight
							.getBoardingPassListObject(job);
					if (list.getBoardingPassList().size() > 0) {
						showAlertDilogToshowSharedFlight(list.getBoardingPassList());
					} else {
						Toast.makeText(context, context.getResources().getString(R.string.txt_no_shared_flight),
								Toast.LENGTH_SHORT).show();
					}
				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void responseFailure(JSONObject job) {
		// TODO Auto-generated method stub
		try {
			JSONObject joberror = new JSONObject(job.getString("error"));
			String code = joberror.getString("code");
			if (code.equals("x05")) {
				JSONObject loginObj = new JSONObject();
				loginObj.put("email", appInstance.getUserCred().getEmail());
				loginObj.put("password", appInstance.getUserCred().getPassword());
				String loginData = loginObj.toString();
				AsyncaTaskApiCall log_in_lisenar = new AsyncaTaskApiCall(FragmentSingleSeatMet.this, loginData,
						context, "login", Constants.REQUEST_TYPE_POST, true);
				log_in_lisenar.execute();

			} else {
				Toast.makeText(context, job.getString("message"), Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void saveLoginCred(JSONObject job) {
		// TODO Auto-generated method stub
		try {
			UserCred userCred;
			String status = job.getString("success");
			if (status.equals("true")) {
				userCred = UserCred.parseUserCred(job);
				userCred.setEmail(appInstance.getUserCred().getEmail());
				userCred.setPassword(appInstance.getUserCred().getPassword());
				appInstance.setUserCred(userCred);
				appInstance.setRememberMe(true);
				if (callfrom == 1) {
					JSONObject loginObj = new JSONObject();
					loginObj.put("token", appInstance.getUserCred().getToken());
					loginObj.put("message", savedMessage);
					callfrom = 1;
					AsyncaTaskApiCall sendmessage = new AsyncaTaskApiCall(FragmentSingleSeatMet.this,
							loginObj.toString(), getActivity(), "messagemate/" + seatmate.getId(),
							Constants.REQUEST_TYPE_POST);
					sendmessage.execute();
				} else if (callfrom == 2) {
					callfrom = 2;
					JSONObject loginObj = new JSONObject();
					loginObj.put("token", appInstance.getUserCred().getToken());
					AsyncaTaskApiCall getSharedFlight = new AsyncaTaskApiCall(FragmentSingleSeatMet.this,
							loginObj.toString(), context, "sharedflight/" + seatmate.getId(),
							Constants.REQUEST_TYPE_POST);
					getSharedFlight.execute();
				}

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
			String code = joberror.getString("code");
			Constants.setAllFlagFalse();
			String message = joberror.getString("message");
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
