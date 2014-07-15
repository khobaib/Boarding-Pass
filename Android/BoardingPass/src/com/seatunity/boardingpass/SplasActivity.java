package com.seatunity.boardingpass;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.bugsense.trace.BugSenseHandler;
import com.google.zxing.BarcodeFormat;
import com.seatunity.boardingpass.utilty.Constants;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
 
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class SplasActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(SplasActivity.this, "2b60c090");
        setContentView(R.layout.splash);
        
        File directory = Constants.APP_DIRECTORY;
        Handler handler=new Handler();
         handler.postDelayed(new Runnable() {
        	@Override
			public void run() {
				Intent intent=new Intent(SplasActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				}
		},4000);
    }
    @Override
    protected void onStop() {
        super.onStop();
        BugSenseHandler.closeSession(SplasActivity.this);
    }

 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
