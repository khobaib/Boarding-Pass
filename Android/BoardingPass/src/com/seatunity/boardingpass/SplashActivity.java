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

import com.bugsense.trace.BugSenseHandler;
import com.seatunity.boardingpass.utilty.Constants;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class SplashActivity extends Activity {
	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(SplashActivity.this, "2b60c090");
		setContentView(R.layout.splash);

		File directory = Constants.APP_DIRECTORY;
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}, 1000);
	}

	@Override
	protected void onStop() {
		super.onStop();
		BugSenseHandler.closeSession(SplashActivity.this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
