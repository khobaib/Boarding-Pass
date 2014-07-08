package com.seatunity.boardingpass;

import java.util.ArrayList;

import com.bugsense.trace.BugSenseHandler;
import com.seatunity.boardingpass.fragment.FragmentLogin;
import com.seatunity.boardingpass.fragment.FragmentSignUp;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.model.UserCred;
import com.viewpagerindicator.TabPageIndicator;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
 
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class AcountActivity extends FragmentActivity {
	public ViewPager pager;
	public TabPageIndicator indicator;
	FragmentPagerAdapter adapter;
	
	BoardingPassApplication appInstance;
	private static final String[] CONTENT = new String[] {"LOGIN","REGISTER"};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(AcountActivity.this, "2b60c090");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.acount);
       
        String chooser= getIntent().getExtras().getString("to");
        pager = (ViewPager) findViewById(R.id.pager);
		indicator = (TabPageIndicator)findViewById(R.id.indicator);
		adapter = new PostRetreiveAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		if(chooser.equals("0")){
			indicator.setViewPager(pager,0);
		}
		else{
			indicator.setViewPager(pager,1);

		}
 
       
    }
    @Override
    protected void onStop() {
        super.onStop();
        BugSenseHandler.closeSession(AcountActivity.this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    class PostRetreiveAdapter extends FragmentPagerAdapter {
		@Override
		public Parcelable saveState() {
			return super.saveState();
		}
		public PostRetreiveAdapter(android.support.v4.app.FragmentManager fragmentManager) {
			super(fragmentManager);
		}
		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			if (position == 0) {
				FragmentLogin newfrag=new FragmentLogin();
				
				return newfrag;
			} else if (position == 1) {
				FragmentSignUp newfrag=new FragmentSignUp();
				return newfrag;
			} else
				return null;

		}
		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}
		@Override
		public int getCount() {
			Log.e("count", ""+CONTENT.length);
			return CONTENT.length;
		}
	}
}

