package com.seatunity.boardingpass.fragment;

import java.util.Stack;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;

/**
 * Inflates the {@link FragmentReminder} if the user is not logged in or has not
 * yet registered, otherwise inflates {@link FragmentMyAccount} containing the
 * information of the user.
 * 
 * @author Sumon
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class AccountListFragment extends TabFragment {
	
	private final String TAG=this.getClass().getSimpleName();
	protected Stack<Fragment> backEndStack;
	BoardingPassApplication appInstance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance = (BoardingPassApplication) getActivity().getApplication();
		backEndStack = new Stack<Fragment>();
		// String email=appInstance.getUserCred().getEmail();
		// Fragment fragment=new FragmentMyAccount();
		// fragment.parent = this;
		// backEndStack.push(fragment);
		if (appInstance.getUserCred().getEmail().equals("")) {
			FragmentReminder fragment = new FragmentReminder();
			fragment.parent = this;
			backEndStack.push(fragment);

		} else {
			FragmentMyAccount fragment = new FragmentMyAccount();
			fragment.parent = this;
			backEndStack.push(fragment);

		}
		try {
			((MainActivity) getActivity()).mDrawerList.setItemChecked(1, true);
			((MainActivity) getActivity()).mDrawerList.setSelection(1);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.e(TAG, "AccountListFragmentononCreateView");
		Log.i(TAG,"onCreateView");
		// ViewParent parent = (ViewParent) container.getParent();
		View v = inflater.inflate(R.layout.fragment_tab3, container, false);
		return v;
	}

	@Override
	public void onStart() {
		// Constants.GOTABFROMWRITETOPIC=2;
		// Log.e("testting", "AccountListFragmentonStart");

		((MainActivity) getActivity()).mDrawerList.setItemChecked(1, true);
		((MainActivity) getActivity()).mDrawerList.setSelection(1);
		Fragment fragment = backEndStack.peek();
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, fragment);
		fragmentTransaction.commitAllowingStateLoss();
		super.onStart();
	}


	/**
	 * Pops up the top fragment from the {@link #backEndStack}.
	 */
	public void clearr() {
		backEndStack.pop();
	}
	/**
	 * If the {@link #backEndStack} does not have user-pushed fragment, then
	 * this method calls {@link #showAlertToExit()} to show an exit-confirmation
	 * dialog.<br>
	 * Otherwise, it backs to the previously opened fragment on back-button
	 * pressed.
	 *
	 * @see com.seatunity.boardingpass.fragment.TabFragment#onBackPressed()
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		// ((HomeActivity)
		// getActivity()).mTabHost.setCurrentTab(Constants.GOTABFROMWRITETOPIC);
		if (backEndStack.size() == 1) {
			// ((MainActivity) getActivity()).close();
			((MainActivity) getActivity()).displayView(0);
			((MainActivity) getActivity()).mDrawerList.setSelection(0);
		} else {
			if (backEndStack.size() == 1) {
				((MainActivity) getActivity()).close();
			} else {
				backEndStack.pop();
				Fragment frg = backEndStack.peek();
				FragmentManager fragmentManager = getActivity().getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.tab3Content, frg).commitAllowingStateLoss();
			}

		}
	}
}
