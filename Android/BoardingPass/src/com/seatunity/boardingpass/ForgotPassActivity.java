package com.seatunity.boardingpass;
import java.util.ArrayList;
import java.util.Calendar;
import org.json.JSONException;
import org.json.JSONObject;
import com.bugsense.trace.BugSenseHandler;
import com.google.zxing.oned.rss.FinderPattern;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.fragment.FragmentMyAccount;
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
import android.content.res.TypedArray;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class ForgotPassActivity extends Activity implements CallBackApiCall {
	EditText et_email;
	Button btn_send;
	String email;
	BoardingPassApplication appInstance;
	UserCred userCred;
	Drawable back;
	ForgotPassActivity forlisenar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		BugSenseHandler.initAndStartSession(ForgotPassActivity.this, "2b60c090");
		appInstance =(BoardingPassApplication)getApplication();
		userCred=appInstance.getUserCred();
		setContentView(R.layout.forgotpass);
		forlisenar=this;
		et_email=(EditText) findViewById(R.id.et_email);
		btn_send=(Button) findViewById(R.id.btn_send);
		et_email.setHintTextColor(Color.parseColor("#ffffff"));
		et_email.addTextChangedListener(new TextWatcher() {          
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
				et_email.setBackgroundResource(R.drawable.rounded_email_forgot_pass);
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
	@Override
	protected void onStop() {
		super.onStop();
		BugSenseHandler.closeSession(ForgotPassActivity.this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void Cancel(View view){
		finish();
	}
	public void ForgotPassBtnClicked(View view){
		email=et_email.getText().toString();
		if(Constants.isOnline(ForgotPassActivity.this)){
			if(Constants.isValidEmail(email)){
				SendForgotPassRequest();
			}
			else{
				et_email.setBackgroundResource(R.drawable.rounded_text_nofield);
				Toast.makeText(ForgotPassActivity.this, getResources().getString(R.string.txt_enter_valid_email),
						Toast.LENGTH_SHORT).show();
			}
		}
		else{
			Toast.makeText(ForgotPassActivity.this, getResources().getString(R.string.txt_please_check_internet),
					Toast.LENGTH_SHORT).show();
		}

	}

	public void SendForgotPassRequest(){
		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("forgotpass", "true");
			loginObj.put("email", email);
			String forgotobject=loginObj.toString();
			String url = "passreset/en";
			AsyncaTaskApiCall forgot_pass =new AsyncaTaskApiCall(ForgotPassActivity.this, loginObj.toString(), ForgotPassActivity.this,
					url,Constants.REQUEST_TYPE_POST);
			forgot_pass.execute();
			
//			AsyncaTaskApiCall logoutcall=new AsyncaTaskApiCall(forlisenar, forgotobject, ForgotPassActivity.this);
//			logoutcall.execute();
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
				Toast.makeText(ForgotPassActivity.this,getResources().getString(R.string.txt_emailsen_success),
						Toast.LENGTH_SHORT).show();
				finish();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void responseFailure(JSONObject job) {
		// TODO Auto-generated method stub
		Toast.makeText(ForgotPassActivity.this,getResources().getString(R.string.txt_emailsen_failure),
				Toast.LENGTH_SHORT).show();
	}
	@Override
	public void saveLoginCred(JSONObject job) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void LoginFailed(JSONObject job) {
		// TODO Auto-generated method stub
		
	}
	
}
