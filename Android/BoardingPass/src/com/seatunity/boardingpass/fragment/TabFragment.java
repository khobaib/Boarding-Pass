package com.seatunity.boardingpass.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;

import com.seatunity.boardingpass.MainActivity;

/**
 * A dummy fragment containing only an abstract method to implement the back-button
 * pressed action.
 * 
 * @author Sumon
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public abstract class TabFragment extends Fragment {
	@Override
	public void onResume() {
		((MainActivity) getActivity()).activeFragment = this;
		super.onResume();
	}

	public abstract void onBackPressed();
}
