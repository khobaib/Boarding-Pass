package com.seatunity.boardingpass;

import java.io.File;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;

import com.bugsense.trace.BugSenseHandler;
import com.seatunity.boardingpass.utilty.Constants;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class LandingActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(LandingActivity.this, "2b60c090");
		setContentView(R.layout.landing_screen);

	}

	public void signupWithFacebook(View view){

	}
	public void signupWithGoogle(View view){

	}
	public void signupWithLinkedin(View view){

	}
	public void signupWithEmail(View view){

	}

	@Override
	protected void onStop() {
		super.onStop();
		BugSenseHandler.closeSession(LandingActivity.this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
