package com.seatunity.boardingpass.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seatunity.boardingpass.AcountActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;

/**
 * When any unregistered user prompts to show own-profile, this static fragment
 * reminds him/her about registering into the app.
 * 
 * @author Sumon
 * 
 */
@SuppressLint("NewApi")
public class FragmentReminder extends Fragment {
	
	private final String TAG=this.getClass().getSimpleName();

	BoardingPassApplication appInstance;
	AccountListFragment parent;
	TextView tv_login, tv_signup;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance = (BoardingPassApplication) getActivity().getApplication();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG,"onCreateView");
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_reminder, container, false);
		tv_login = (TextView) v.findViewById(R.id.tv_login);
		tv_signup = (TextView) v.findViewById(R.id.tv_signup);
		tv_signup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), AcountActivity.class);
				intent.putExtra("to", "1");
				startActivity(intent);
				// getActivity().finish();
			}
		});
		tv_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), AcountActivity.class);
				intent.putExtra("to", "0");
				startActivity(intent);
				// getActivity().finish();
			}
		});

		return v;
	}

}
