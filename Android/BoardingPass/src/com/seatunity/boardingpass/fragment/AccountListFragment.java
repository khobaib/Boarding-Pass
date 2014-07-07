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
public class AccountListFragment extends TabFragment{
	protected Stack<Fragment> backEndStack;
	BoardingPassApplication appInstance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Log.e("testting", "AccountListFragment");
		appInstance =(BoardingPassApplication)getActivity().getApplication();

		backEndStack = new Stack<Fragment>();

		String email=appInstance.getUserCred().getEmail();
		//	Fragment fragment=new FragmentMyAccount();
		//	fragment.parent = this;
		//	backEndStack.push(fragment);
		if(appInstance.getUserCred().getEmail().equals("")){
			FragmentReminder fragment = new FragmentReminder();
			fragment.parent = this;
			backEndStack.push(fragment);

		}
		else{
			FragmentMyAccount fragment = new FragmentMyAccount();
			fragment.parent = this;
			backEndStack.push(fragment);

		}
		((MainActivity)getActivity()).mDrawerList.setItemChecked(1, true);
		((MainActivity)getActivity()).mDrawerList.setSelection(1);




	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("testting", "AccountListFragmentononCreateView");
		ViewParent parent = (ViewParent) container.getParent();
		View v = inflater.inflate(R.layout.fragment_tab3, container, false);
		return v;
	}

	public void onStart( ) {
		//	Constants.GOTABFROMWRITETOPIC=2;
	//	Log.e("testting", "AccountListFragmentonStart");

		((MainActivity)getActivity()).mDrawerList.setItemChecked(1, true);
		((MainActivity)getActivity()).mDrawerList.setSelection(1);
		Fragment fragment = backEndStack.peek();
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, fragment);
		fragmentTransaction.commitAllowingStateLoss();
		super.onStart();
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
				FragmentManager fragmentManager = getActivity().getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction.replace(R.id.tab3Content, frg).commitAllowingStateLoss();
			}

		}
	}
}
