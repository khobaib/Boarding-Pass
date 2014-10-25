package com.seatunity.boardingpass.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.seatunity.boardingpass.ForgotPassActivity;
import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.networkstatetracker.SyncLocalDbtoBackend;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;

/**
 * This fragment holds a simple UI & API methods to log-in into the application.
 * 
 * @author Sumon
 * 
 */
@SuppressLint("NewApi")
public class FragmentLogin extends Fragment implements CallBackApiCall {
	
	private final String TAG=this.getClass().getSimpleName();
	EditText et_email, et_password;
	TextView tv_errorshow, tv_forgot_pass;
	Button bt_login;
	String email, password;
	BoardingPassApplication appInstance;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance = (BoardingPassApplication) getActivity().getApplication();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG,"onCreateView");
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.login, container, false);
		initview(v);
		return v;
	}

	/**
	 * Initializes the view for this fragment.
	 * 
	 * @param v
	 *            The {@link ViewGroup} inside which the view is to be
	 *            initialized.
	 */
	public void initview(ViewGroup v) {
		tv_errorshow = (TextView) v.findViewById(R.id.tv_errorshow);
		et_email = (EditText) v.findViewById(R.id.et_email);
		et_password = (EditText) v.findViewById(R.id.et_password);
		bt_login = (Button) v.findViewById(R.id.bt_login);
		tv_forgot_pass = (TextView) v.findViewById(R.id.tv_forgot_pass);
		et_password.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				et_password.setBackgroundResource(android.R.drawable.editbox_background_normal);
				tv_errorshow.setVisibility(View.GONE);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		et_email.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				et_email.setBackgroundResource(android.R.drawable.editbox_background_normal);
				tv_errorshow.setVisibility(View.GONE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		bt_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				email = et_email.getText().toString();
				password = et_password.getText().toString();
				if (Constants.isOnline(getActivity())) {
					if (!Constants.isValidEmail(email)) {
						Toast.makeText(getActivity(),
								getActivity().getResources().getString(R.string.txt_enter_valid_email),
								Toast.LENGTH_SHORT).show();
						et_email.setBackgroundResource(R.drawable.rounded_text_nofield);
					} else if (password.equals("")) {
						Toast.makeText(getActivity(),
								getActivity().getResources().getString(R.string.txt_enter_password), Toast.LENGTH_SHORT)
								.show();
						et_password.setBackgroundResource(R.drawable.rounded_text_nofield);

					} else {
						callloginApi();
					}
				} else {
					Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_check_internet),
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		tv_forgot_pass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), ForgotPassActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * Forms the calling JSON & then calls the log-in API
	 */
	public void callloginApi() {
		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("email", email);
			loginObj.put("password", password);
			String loginData = loginObj.toString();
			AsyncaTaskApiCall log_in_lisenar = new AsyncaTaskApiCall(FragmentLogin.this, loginData, getActivity(),
					"login", Constants.REQUEST_TYPE_POST);
			log_in_lisenar.execute();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calls the {@link #setUsercredential(String) } method with the passed
	 * {@link ServerResponse} object's JSON-formatted string.
	 * 
	 * @param serverResponse
	 */
	public void callBackFromApicall(ServerResponse serverResponse) {
		setUsercredential(serverResponse.getjObj().toString());
	}

	/**
	 * Saves the user's credentials in the {@link SharedPreferences} if the
	 * status of the passed {@link ServerResponse} objectis "true", otherwise
	 * sets the visibility of the error-message text-view {@link View#VISIBLE}.
	 * 
	 * @param result
	 *            The {@link ServerResponse} object's JSON-formatted string,
	 *            without the integer status-code
	 */
	public void setUsercredential(String result) {
		UserCred usercred = new UserCred();
		try {
			JSONObject job = new JSONObject(result);
			String status = job.getString("success");
			if (status.equals("true")) {
				usercred = UserCred.parseUserCred(job);
				usercred.setEmail(email);
				usercred.setPassword(password);
				appInstance.setUserCred(usercred);
				appInstance.setRememberMe(true);

				Intent intent = new Intent(getActivity(), MainActivity.class);
				intent.putExtra("select", 1);
				startActivity(intent);
				getActivity().finish();
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_login_success),
						Toast.LENGTH_SHORT).show();

			} else {
				tv_errorshow.setVisibility(View.VISIBLE);
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_login_failed),
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void responseOk(JSONObject job) {
		try {
			UserCred usercred = new UserCred();
			String status = job.getString("success");
			if (status.equals("true")) {
				usercred = UserCred.parseUserCred(job);
				usercred.setEmail(email);
				usercred.setPassword(password);
				appInstance.setUserCred(usercred);
				appInstance.setRememberMe(true);
				SyncLocalDbtoBackend synlocaldat = new SyncLocalDbtoBackend();
				synlocaldat.sendBoardingPasstoRemoteDB(getActivity());
				Intent intent = new Intent(getActivity(), MainActivity.class);
				intent.putExtra("select", 1);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				getActivity().finish();
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_login_success),
						Toast.LENGTH_SHORT).show();

			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void responseFailure(JSONObject job) {
		tv_errorshow.setVisibility(View.VISIBLE);
		Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_login_failed),
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void saveLoginCred(JSONObject job) {
	}

	@Override
	public void LoginFailed(JSONObject job) {
	}

	@Override
	public void responseFailure(ServerResponse response) {
		if (response.getStatus() != 200) {
			BoardingPassApplication.alert(getActivity(), "Internet connectivity is lost! Please retry the operation.");
		}
	}
}
