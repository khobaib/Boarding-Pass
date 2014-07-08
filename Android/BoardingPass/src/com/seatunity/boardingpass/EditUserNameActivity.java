package com.seatunity.boardingpass;
import java.util.ArrayList;
import java.util.Calendar;

import com.bugsense.trace.BugSenseHandler;
import com.seatunity.boardingpass.utilty.Constants;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
 
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class EditUserNameActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(EditUserNameActivity.this, "2b60c090");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_user_name);
 
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
}
