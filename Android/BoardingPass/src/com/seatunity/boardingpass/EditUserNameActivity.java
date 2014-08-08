package com.seatunity.boardingpass;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.bugsense.trace.BugSenseHandler;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.fragment.FragmentLogin;
import com.seatunity.boardingpass.fragment.FragmentSeatMet;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.UserCred;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class EditUserNameActivity extends Activity implements CallBackApiCall{
	EditText et_uname,et_email,et_status;
	String username,email,status;
	BoardingPassApplication appInstance;
	UserCred userCred;
	Drawable back;
	JSONObject contentbodyremeber;
	EditUserNameActivity lisenar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(EditUserNameActivity.this, "2b60c090");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		appInstance =(BoardingPassApplication)getApplication();
		userCred=appInstance.getUserCred();
		setContentView(R.layout.edit_user_name);
		lisenar=this;
		et_uname=(EditText) findViewById(R.id.et_uname);
		et_email=(EditText) findViewById(R.id.et_email);
		et_status=(EditText) findViewById(R.id.et_status);
		back=et_status.getBackground();

		editTextControl();
		setView();
	}
	public void canCel(View view){
		finish();
	}
	public void editTextControl(){
		et_uname.addTextChangedListener(new TextWatcher() {          
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
				et_uname.setBackgroundDrawable(back);



			}                       
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub                          
			}                       
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub                          

			}
		});
		et_email.addTextChangedListener(new TextWatcher() {          
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
				et_email.setBackgroundDrawable(back);
			}                       
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub                          
			}                       
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub                          

			}
		});
		et_status.addTextChangedListener(new TextWatcher() {          
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
				et_status.setBackgroundDrawable(back);
			}                       
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub                          
			}                       
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub                          

			}
		});
	}
	public void setView(){
		et_uname.setText(appInstance.getUserCred().getFirstname());
		et_email.setText(appInstance.getUserCred().getEmail());
		et_status.setText(appInstance.getUserCred().getStatus());

	}
	@Override
	protected void onStop() {
		super.onStop();
		BugSenseHandler.closeSession(EditUserNameActivity.this);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void Cancel(View view){
		finish();
	}
	public void ChangeStatus(View view){
		username=et_uname.getText().toString();
		email=et_email.getText().toString();
		status=et_status.getText().toString();
		if(Constants.isOnline(EditUserNameActivity.this)){
			if(!status.equals("")){
				if(Constants.isValidEmail(email)){
					if(!username.equals("")){
						changeStatus();
					}
					else{
						et_uname.setBackgroundResource(R.drawable.rounded_text_nofield);
						Toast.makeText(EditUserNameActivity.this, getResources().getString(R.string.txt_please_enter_username),
								Toast.LENGTH_SHORT).show();
					}
				}
				else{
					et_email.setBackgroundResource(R.drawable.rounded_text_nofield);
					Toast.makeText(EditUserNameActivity.this, getResources().getString(R.string.txt_enter_valid_email),
							Toast.LENGTH_SHORT).show();
				}
			}
			else{
				et_status.setBackgroundResource(R.drawable.rounded_text_nofield);
				Toast.makeText(EditUserNameActivity.this, getResources().getString(R.string.txt_enter_status),
						Toast.LENGTH_SHORT).show();

			}
		}
		else{
			Toast.makeText(EditUserNameActivity.this, getResources().getString(R.string.txt_please_check_internet),
					Toast.LENGTH_SHORT).show();
		}
		
	}

	public void changeStatus(){
		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token", appInstance.getUserCred().getToken());
			loginObj.put("password",  appInstance.getUserCred().getPassword());
			loginObj.put("language", appInstance.getUserCred().getLanguage());
			loginObj.put("firstname", username);
			loginObj.put("lastname", appInstance.getUserCred().getLastname());
			loginObj.put("gender", appInstance.getUserCred().getGender());
			loginObj.put("live_in", appInstance.getUserCred().getLive_in());
			loginObj.put("age", appInstance.getUserCred().getAge());
			loginObj.put("profession", appInstance.getUserCred().getProfession());
			loginObj.put("seating_pref", appInstance.getUserCred().getSeating_pref());
			loginObj.put("some_about_you", appInstance.getUserCred().getSomethinAbout());
			loginObj.put("status", status);
			loginObj.put("image_name", "");
			loginObj.put("image_type", "");
			loginObj.put("image_content", "");
			
			AsyncaTaskApiCall edit_uname =new AsyncaTaskApiCall(EditUserNameActivity.this,loginObj.toString() , EditUserNameActivity.this,
					"reg_update",Constants.REQUEST_TYPE_POST);
			edit_uname.execute();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void responseOk(JSONObject job) {
		// TODO Auto-generated method stub
		try {
			if(job.getString("success").equals("true")){
				Toast.makeText(EditUserNameActivity.this,getResources().getString(R.string.txt_update_success),
						Toast.LENGTH_SHORT).show();
				userCred.setStatus(status);
				userCred.setFirstname(username);
				appInstance.setUserCred(userCred);
				finish();
			}
			else{


			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void responseFailure(JSONObject job) {
		
		try {
			JSONObject joberror=new JSONObject(job.getString("error"));
			String code =joberror.getString("code");
			if(code.equals("x05")){
//				String message=joberror.getString("message");
//				Toast.makeText(EditUserNameActivity.this, message,Toast.LENGTH_SHORT).show();
				JSONObject loginObj = new JSONObject();
				loginObj.put("email", appInstance.getUserCred().getEmail());
				loginObj.put("password", appInstance.getUserCred().getPassword());
				String loginData = loginObj.toString();
				AsyncaTaskApiCall log_in_lisenar =new AsyncaTaskApiCall(EditUserNameActivity.this, loginData, 
						EditUserNameActivity.this,"login",Constants.REQUEST_TYPE_POST,true);
				log_in_lisenar.execute();

			}
			else{
				String message=joberror.getString("message");
				Toast.makeText(EditUserNameActivity.this, getResources().getString(R.string.txt_update_failed),
						Toast.LENGTH_SHORT).show();
			}
			
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Override
	public void saveLoginCred(JSONObject job) {
		// TODO Auto-generated method stub
		try {
			String status=job.getString("success");
			if(status.equals("true")){
				userCred=userCred.parseUserCred(job);
				userCred.setEmail(appInstance.getUserCred().getEmail());
				userCred.setPassword(appInstance.getUserCred().getPassword());
				Log.e("tagged before",  appInstance.getUserCred().getToken());
				appInstance.setUserCred(userCred);
				Log.e("tagged after",  appInstance.getUserCred().getToken());
				Log.e("tagged actuual", userCred.getToken());
				appInstance.setRememberMe(true);
				JSONObject loginObj = new JSONObject();
				loginObj.put("token", appInstance.getUserCred().getToken());
				loginObj.put("password",  appInstance.getUserCred().getPassword());
				loginObj.put("language", appInstance.getUserCred().getLanguage());
				loginObj.put("firstname", username);
				loginObj.put("lastname", appInstance.getUserCred().getLastname());
				loginObj.put("gender", appInstance.getUserCred().getGender());
				loginObj.put("live_in", appInstance.getUserCred().getLive_in());
				loginObj.put("age", appInstance.getUserCred().getAge());
				loginObj.put("profession", appInstance.getUserCred().getProfession());
				loginObj.put("seating_pref", appInstance.getUserCred().getSeating_pref());
				loginObj.put("some_about_you", appInstance.getUserCred().getSomethinAbout());
				loginObj.put("status", status);
				loginObj.put("image_name", "");
				loginObj.put("image_type", "");
				loginObj.put("image_content", "");
				AsyncaTaskApiCall edit_uname =new AsyncaTaskApiCall(EditUserNameActivity.this,loginObj.toString() , 
						EditUserNameActivity.this,	"reg_update",Constants.REQUEST_TYPE_POST);
				edit_uname.execute();
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void LoginFailed(JSONObject job) {
		// TODO Auto-generated method stub
		try {
			JSONObject joberror=new JSONObject(job.getString("error"));
			String code =joberror.getString("code");

				String message=joberror.getString("message");
				Toast.makeText(EditUserNameActivity.this, message,
						Toast.LENGTH_SHORT).show();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
