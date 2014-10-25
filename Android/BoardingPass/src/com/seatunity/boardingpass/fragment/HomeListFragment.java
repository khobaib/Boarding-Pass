package com.seatunity.boardingpass.fragment;

import java.util.Stack;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;

import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.SeatMate;
import com.seatunity.model.SeatMetList;

/**
 * This fragment holds all other fragments in the app.
 * 
 * @author Sumon
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint({ "NewApi", "InflateParams" })
public class HomeListFragment extends TabFragment {

	private final String TAG = this.getClass().getSimpleName();

	/**
	 * Fragment-stack to implement the back-stack app flow.
	 */
	protected Stack<Fragment> backEndStack;
	BoardingPassApplication appInstance;
	// private ArrayList<BoardingPass> list;

	/**
	 * Indicator of the fragment-no. to load.
	 */
	int from;

	public HomeListFragment(int from) {
		this.from = from;
	}

	public HomeListFragment() {
		// this.from = from;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance = (BoardingPassApplication) getActivity().getApplication();
		backEndStack = new Stack<Fragment>();
		// SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
		// dbInstance.open();
		// ArrayList<BoardingPass> list = (ArrayList<BoardingPass>)
		// dbInstance.retrieveBoardingPassList();
		// dbInstance.close();
		// String email = appInstance.getUserCred().getEmail();
		if (from == 0) {
			FragmentGetBoardingPasseFromBackend fragment = new FragmentGetBoardingPasseFromBackend();
			fragment.parent = this;
			Constants.parent = this;
			backEndStack.push(fragment);
		} else {
			FragmentAbout newFragment = new FragmentAbout();
			newFragment.parent = this;
			backEndStack.push(newFragment);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.e("again", "onCreate");

		@SuppressWarnings("unused")
		ViewParent parent = (ViewParent) container.getParent();
		View v = inflater.inflate(R.layout.fragment_tab3, container, false);
		return v;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onStart() {
		((MainActivity) getActivity()).mDrawerList.setItemChecked(0, true);
		((MainActivity) getActivity()).mDrawerList.setSelection(0);
		Fragment fragment;
		if (from == 0) {
			fragment = backEndStack.peek();
		} else {
			fragment = backEndStack.peek();
		}

		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, fragment);
		fragmentTransaction.commitAllowingStateLoss();
		super.onStart();
	}

	/**
	 * Starts the {@link HomeFragment} when the user has not registered yet or
	 * is logged out.
	 */
	public void startHomeFragment() {
		HomeFragment newFragment = new HomeFragment();
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}

	/**
	 * Opens the {@link FragmentSingleSeatMet} fragment with the selected
	 * boarding-pass & seat-mate data.
	 * 
	 * @param seatmate
	 * @param bpass
	 */
	public void startFragmentSingleSeatmet(SeatMate seatmate, BoardingPass bpass) {
		Log.e("insideList", bpass.getTravel_from_name());
		FragmentSingleSeatMet newFragment = new FragmentSingleSeatMet(seatmate, bpass);
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}

	/**
	 * Opens the static {@link FragmentTermsAndCondition } fragment.
	 * 
	 * @param title
	 * @param details
	 */
	public void startFragmentTermsAndCondition(String title, String details) {
		FragmentTermsAndCondition newFragment = new FragmentTermsAndCondition(title, details);
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}

	/**
	 * Shows the future boarding-pass list in the {@link FragmentBoardingPasses }
	 * fragment.
	 * 
	 * @param futureBPList
	 */
	public void startFragmentBoardingPasses() {
		Log.i(TAG, "startFragmentBoardingPasses");
		FragmentBoardingPasses newFragment = FragmentBoardingPasses.newInstance();
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}

	/**
	 * Starts the {@link FragmentAbout} with some brief of the app.
	 */
	public void startFragmentAbout() {
		FragmentAbout newFragment = FragmentAbout.newInstance();
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}

	/**
	 * Shows the details of the passed boaring-pass in the
	 * {@link FragmentUpcomingBoardingPassDetails } fragment.
	 * 
	 * @param bpass
	 */
	public void startUpCommingBoardingDetails(BoardingPass bpass) {
		Log.e("insideList", bpass.getTravel_from_name());
		FragmentUpcomingBoardingPassDetails newFragment = FragmentUpcomingBoardingPassDetails.newInstance(bpass);
		newFragment.parent = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}

	/**
	 * Shows the passed seat-mate list in the {@link FragmentSeatMet } fragment.
	 * 
	 * @param seatmetlist
	 * @param bpass
	 */
	public void startSeatmetList(SeatMetList seatmetlist, BoardingPass bpass) {
		Log.e("insideList3", bpass.getTravel_from_name());
		FragmentSeatMet newFragment = new FragmentSeatMet(seatmetlist, bpass);
		newFragment.parentAsHome = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}

	public void startAddBoardingPassDuringLogin() {
		FragmentAddBoardingPassDuringLogin newFragment = new FragmentAddBoardingPassDuringLogin();
		newFragment.parentAsHome = this;
		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.tab3Content, newFragment);
		fragmentTransaction.addToBackStack(null);
		backEndStack.push(newFragment);
		fragmentTransaction.commitAllowingStateLoss();
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
		if (from == 0) {
			if (backEndStack.size() <= 2) {
				showAlertToExit();
			} else {
				if (backEndStack.size() == 2) {
					((MainActivity) getActivity()).close();
				} else {
					backEndStack.pop();
					Fragment frg = backEndStack.peek();
					Log.d(TAG, "Stack-size greater than 2 ...");
					FragmentManager fragmentManager = getActivity().getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.replace(R.id.tab3Content, frg).commitAllowingStateLoss();
				}

			}
		} else {
			if (backEndStack.size() <= 1) {
				showAlertToExit();
			} else {
				if (backEndStack.size() == 1) {
					((MainActivity) getActivity()).close();
				} else {
					backEndStack.pop();
					Fragment frg = backEndStack.peek();
					Log.d(TAG, "Stack-size greater than 1 ...");
					FragmentManager fragmentManager = getActivity().getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.replace(R.id.tab3Content, frg).commitAllowingStateLoss();
				}

			}
		}

	}

	/**
	 * Shows an exit-confirmation dialog.
	 */
	public void showAlertToExit() {
		final AlertDialog d = new AlertDialog.Builder(getActivity())
				.setPositiveButton(getActivity().getResources().getString(R.string.txt_ok), null)

				.setNegativeButton(getActivity().getResources().getString(R.string.txt_cancel), null)
				.setMessage(getActivity().getString(R.string.txt_exit_message)).create();
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		TextView tvtitle = (TextView) customTitleView.findViewById(R.id.tv_title);
		tvtitle.setText(getActivity().getString(R.string.app_name_seatunity));
		d.setCustomTitle(customTitleView);
		d.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button ok = d.getButton(AlertDialog.BUTTON_POSITIVE);
				Button cancel = d.getButton(AlertDialog.BUTTON_NEGATIVE);
				cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						d.cancel();
					}
				});
				ok.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						d.cancel();
						((MainActivity) getActivity()).close();
					}
				});
			}
		});
		d.show();
	}
}
