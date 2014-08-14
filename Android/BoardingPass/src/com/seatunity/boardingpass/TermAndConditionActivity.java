package com.seatunity.boardingpass;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;

import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.model.UserCred;

/**
 * Shows static text on the "Terms & Condition" about using this application.
 * 
 * @author Sumon
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class TermAndConditionActivity extends Activity {
	EditText et_uname, et_email, et_status;
	String username, email, status;
	BoardingPassApplication appInstance;
	UserCred userCred;
	Drawable back;
	JSONObject contentbodyremeber;
	EditUserNameActivity lisenar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		appInstance = (BoardingPassApplication) getApplication();
		setContentView(R.layout.terms_privacy_policy);

	}
}
