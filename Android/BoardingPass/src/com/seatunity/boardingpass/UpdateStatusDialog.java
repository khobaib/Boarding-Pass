package com.seatunity.boardingpass;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;

/**
 * This activity provides a simple UI & an API calling methodology to change the
 * name of the user.
 * 
 * @author Touhid
 * 
 */
public class UpdateStatusDialog extends Activity implements CallBackApiCall {

	private EditText etStatus;
	private String username, status;
	private BoardingPassApplication appInstance;
	private UserCred userCred;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(UpdateStatusDialog.this, "2b60c090");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		appInstance = (BoardingPassApplication) getApplication();
		userCred = appInstance.getUserCred();
		username = userCred.getFirstname();

		setContentView(R.layout.edit_status);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		etStatus = (EditText) findViewById(R.id.etStatusUpdate);
		etStatus.setText(userCred.getStatus());
		findViewById(R.id.btnApplyStatus).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeStatus(v);
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		BugSenseHandler.closeSession(UpdateStatusDialog.this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Called when the "Apply" button is clicked in the UI.
	 * 
	 * @param view
	 */
	private void changeStatus(View view) {
		status = etStatus.getText().toString();
		if (Constants.isOnline(UpdateStatusDialog.this)) {
			if (!status.equals("")) {
				callChangeStatusAPI();
			} else {
				etStatus.setBackgroundResource(R.drawable.rounded_text_nofield);
				Toast.makeText(UpdateStatusDialog.this, getResources().getString(R.string.txt_enter_status),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(UpdateStatusDialog.this, getResources().getString(R.string.txt_please_check_internet),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Forms the JSON-content string & calls the AsyncTask for the name-change
	 * API.
	 */
	private void callChangeStatusAPI() {
		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token", userCred.getToken());
			loginObj.put("password", userCred.getPassword());
			loginObj.put("language", userCred.getLanguage());
			loginObj.put("firstname", username);
			loginObj.put("lastname", userCred.getLastname());
			loginObj.put("gender", userCred.getGender());
			loginObj.put("live_in", userCred.getLive_in());
			loginObj.put("age", userCred.getAge());
			loginObj.put("profession", userCred.getProfession());
			loginObj.put("seating_pref", userCred.getSeating_pref());
			loginObj.put("some_about_you", userCred.getSomethinAbout());
			loginObj.put("status", status);
			loginObj.put("image_name", "");
			loginObj.put("image_type", "");
			loginObj.put("image_content", "");

			AsyncaTaskApiCall editStatus = new AsyncaTaskApiCall(UpdateStatusDialog.this, loginObj.toString(),
					UpdateStatusDialog.this, "reg_update", Constants.REQUEST_TYPE_POST);
			editStatus.execute();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void responseOk(JSONObject job) {
		try {
			if (job.getString("success").equals("true")) {
				Toast.makeText(UpdateStatusDialog.this, getResources().getString(R.string.txt_update_success),
						Toast.LENGTH_SHORT).show();
				userCred.setStatus(status);
				userCred.setFirstname(username);
				appInstance.setUserCred(userCred);
				finish();
			} else {

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void responseFailure(JSONObject job) {

		try {
			JSONObject joberror = new JSONObject(job.getString("error"));
			String code = joberror.getString("code");
			if (code.equals("x05")) {
				// String message=joberror.getString("message");
				// Toast.makeText(EditUserNameActivity.this,
				// message,Toast.LENGTH_SHORT).show();
				JSONObject loginObj = new JSONObject();
				loginObj.put("email", userCred.getEmail());
				loginObj.put("password", userCred.getPassword());
				String loginData = loginObj.toString();
				AsyncaTaskApiCall log_in_lisenar = new AsyncaTaskApiCall(UpdateStatusDialog.this, loginData,
						UpdateStatusDialog.this, "login", Constants.REQUEST_TYPE_POST, true);
				log_in_lisenar.execute();

			} else {
				// String message = joberror.getString("message");
				Toast.makeText(UpdateStatusDialog.this, getResources().getString(R.string.txt_update_failed),
						Toast.LENGTH_SHORT).show();
			}

		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveLoginCred(JSONObject job) {
		try {
			String status = job.getString("success");
			if (status.equals("true")) {
				userCred = UserCred.parseUserCred(job);
				userCred.setEmail(userCred.getEmail());
				userCred.setPassword(userCred.getPassword());
				Log.e("tagged before", userCred.getToken());
				appInstance.setUserCred(userCred);
				Log.e("tagged after", userCred.getToken());
				Log.e("tagged actuual", userCred.getToken());
				appInstance.setRememberMe(true);
				JSONObject loginObj = new JSONObject();
				loginObj.put("token", userCred.getToken());
				loginObj.put("password", userCred.getPassword());
				loginObj.put("language", userCred.getLanguage());
				loginObj.put("firstname", username);
				loginObj.put("lastname", userCred.getLastname());
				loginObj.put("gender", userCred.getGender());
				loginObj.put("live_in", userCred.getLive_in());
				loginObj.put("age", userCred.getAge());
				loginObj.put("profession", userCred.getProfession());
				loginObj.put("seating_pref", userCred.getSeating_pref());
				loginObj.put("some_about_you", userCred.getSomethinAbout());
				loginObj.put("status", status);
				loginObj.put("image_name", "");
				loginObj.put("image_type", "");
				loginObj.put("image_content", "");
				AsyncaTaskApiCall edit_uname = new AsyncaTaskApiCall(UpdateStatusDialog.this, loginObj.toString(),
						UpdateStatusDialog.this, "reg_update", Constants.REQUEST_TYPE_POST);
				edit_uname.execute();
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
			// String code = joberror.getString("code");

			String message = joberror.getString("message");
			Toast.makeText(UpdateStatusDialog.this, message, Toast.LENGTH_SHORT).show();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void responseFailure(ServerResponse response) {
		if (response.getStatus() != 200) {
			BoardingPassApplication.alert(UpdateStatusDialog.this,
					"Internet connectivity is lost! Please retry the operation.");
		}
	}

}
