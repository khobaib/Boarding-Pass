package com.seatunity.boardingpass;

import com.seatunity.boardingpass.adapter.CustomPagerAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;

public class PremiumAccountActivity extends FragmentActivity{
	private ViewPager viewPager;
	private CustomPagerAdapter customPagerAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_premium_account);
		viewPager=(ViewPager)findViewById(R.id.pager); 
		customPagerAdapter=new CustomPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(customPagerAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				
				viewPager.setCurrentItem(position);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
