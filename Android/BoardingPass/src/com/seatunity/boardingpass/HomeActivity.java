package com.seatunity.boardingpass;

import com.bugsense.trace.BugSenseHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

public class HomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(HomeActivity.this, "2b60c090");
        setContentView(R.layout.home);
       
    }
    @Override
    protected void onStop() {
        super.onStop();
        BugSenseHandler.closeSession(HomeActivity.this);
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onClickLogout(){
    	
    }
}
