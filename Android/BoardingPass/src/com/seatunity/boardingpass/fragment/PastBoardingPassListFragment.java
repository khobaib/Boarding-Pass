package com.seatunity.boardingpass.fragment;

import java.util.ArrayList;
import java.util.Stack;
import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.model.BoardingPass;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewParent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi")
public class PastBoardingPassListFragment extends TabFragment{
	protected Stack<Fragment> backEndStack;
	BoardingPassApplication appInstance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance =(BoardingPassApplication)getActivity().getApplication();

		backEndStack = new Stack<Fragment>();
		SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
		dbInstance.open();
		ArrayList<BoardingPass> list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
		
		dbInstance.close();
		String email=appInstance.getUserCred().getEmail();
		
			FragmentPastBoardingPasses fragment = new FragmentPastBoardingPasses();
			fragment.parent = this;
			backEndStack.push(fragment);
		
		
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewParent parent = (ViewParent) container.getParent();
//		if (parent instanceof View) {
//			((TextView) ((View) parent).findViewById(R.id.welcome_title))
//			.setText(this.getTag());
//		}
		View v = inflater.inflate(R.layout.fragment_tab3, container, false);
		return v;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB) public void onStart( ) {
	//	Constants.GOTABFROMWRITETOPIC=2;

		Fragment fragment = backEndStack.peek();
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, fragment);
		fragmentTransaction.commitAllowingStateLoss();
		super.onStart();
	}

	public void startAddBoardingPass() {
//		FragmentAddBoardingPass newFragment = new FragmentAddBoardingPass() ;
//		newFragment.parent = this;
//		FragmentManager fragmentManager = getActivity().getFragmentManager();
//		FragmentTransaction fragmentTransaction = fragmentManager
//				.beginTransaction();
//		fragmentTransaction.replace(R.id.tab3Content, newFragment);
//		fragmentTransaction.addToBackStack(null);
//		backEndStack.push(newFragment);
//		fragmentTransaction.commitAllowingStateLoss();
	}
	public void clearr(){
		backEndStack.pop();
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi") @Override
	public void onBackPressed() {
		//((HomeActivity) getActivity()).mTabHost.setCurrentTab(Constants.GOTABFROMWRITETOPIC);
		if (backEndStack.size()==1) {
			((MainActivity) getActivity()).close();
		}
		else {
			if (backEndStack.size()==1) {
				((MainActivity) getActivity()).close();
			} else {
				backEndStack.pop();
				Fragment frg = backEndStack.peek();
				Log.d("1", "4");
				FragmentManager fragmentManager = getActivity().getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction.replace(R.id.tab3Content, frg).commitAllowingStateLoss();
			}

		}
	}
}
