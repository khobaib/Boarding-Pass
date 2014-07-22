package com.seatunity.boardingpass.fragment;

import java.util.ArrayList;
import java.util.Stack;

import org.json.JSONException;
import org.json.JSONObject;

import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.SeatMate;
import com.seatunity.model.SeatMetList;

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
public class HomeListFragment extends TabFragment{
	protected Stack<Fragment> backEndStack;
	BoardingPassApplication appInstance;
	ArrayList<BoardingPass>list;
	int from;
	public HomeListFragment(int from){
		this.from=from;
	}
	public HomeListFragment(){
		this.from=from;
	}
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
		if(from==0){
			
			FragmentGetBoardingPasseFromBackend fragment = new FragmentGetBoardingPasseFromBackend();
				fragment.parent = this;
				backEndStack.push(fragment);
		}
		else{
			FragmentAbout newFragment = new FragmentAbout() ;
			newFragment.parent = this;
			backEndStack.push(newFragment);
		}




	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewParent parent = (ViewParent) container.getParent();
		View v = inflater.inflate(R.layout.fragment_tab3, container, false);
		return v;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB) public void onStart( ) {
		((MainActivity)getActivity()).mDrawerList.setItemChecked(0, true);
		((MainActivity)getActivity()).mDrawerList.setSelection(0);
		Fragment fragment;
		if(from==0){
			 fragment = backEndStack.pop();
		}
		else{
			 fragment = backEndStack.peek();
		}
		
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, fragment);
		fragmentTransaction.commitAllowingStateLoss();
		super.onStart();
	}
	public void startHomeFragment() {
		HomeFragment newFragment = new HomeFragment() ;
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}
	public void startFragmentSingleSeatmet(SeatMate seatmate,BoardingPass bpass) {
		Log.e("insideList", bpass.getTravel_from_name());
		FragmentSingleSeatMet newFragment = new FragmentSingleSeatMet(seatmate,bpass) ;
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}
	public void startFragmentTermsAndCondition(String title,String details) {
		FragmentTermsAndCondition newFragment = new FragmentTermsAndCondition( title, details) ;
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}
	
	public void startFragmentBoardingPasses(ArrayList<BoardingPass>list_greaterthan) {
		FragmentBoardingPasses newFragment = new FragmentBoardingPasses(list_greaterthan) ;
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}
	public void startFragmentAbout() {
		FragmentAbout newFragment = new FragmentAbout() ;
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}
	public void startUpCommingBoadingDetails(BoardingPass bpass) {
		Log.e("insideList", bpass.getTravel_from_name());
		FragmentUpcomingBoardingPassDetails newFragment = new FragmentUpcomingBoardingPassDetails(bpass) ;
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}
	public void startSeatmetList(SeatMetList seatmetlist,BoardingPass bpass) {
		Log.e("insideList3", bpass.getTravel_from_name());
		FragmentSeatMet newFragment = new FragmentSeatMet(seatmetlist,bpass) ;
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}

	public void startAddBoardingPassDuringLogin() {
		FragmentAddBoardingPassDuringLogin newFragment = new FragmentAddBoardingPassDuringLogin() ;
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}
	public void clearr(){
		backEndStack.pop();
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi") @Override
	public void onBackPressed() {
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
