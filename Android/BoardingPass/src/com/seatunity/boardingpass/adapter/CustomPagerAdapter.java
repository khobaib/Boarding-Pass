package com.seatunity.boardingpass.adapter;

import com.seatunity.boardingpass.fragment.FragmentUpgradePager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CustomPagerAdapter extends FragmentPagerAdapter {

	public CustomPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		Bundle bundle=new Bundle();
		bundle.putInt("position", index);
		FragmentUpgradePager fragmentUpgradePager=new FragmentUpgradePager();
		fragmentUpgradePager.setArguments(bundle);
		return fragmentUpgradePager;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 5;
	}

}