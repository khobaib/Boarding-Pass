package com.seatunity.boardingpass.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;

/**
 * If the logged-in user doesn't have any boarding pass to show in list, then
 * this fragment will be inflated with a plus button to add new boarding-pass
 * from it.
 * 
 * @author Sumon
 * 
 */
@SuppressLint("NewApi")
public class FragmentAddBoardingPassDuringLogin extends Fragment {
	
	private final String TAG=this.getClass().getSimpleName();
	HomeListFragment parent;
	ImageView img_add_boardingpass;
	TextView tv_add_boardingpass;
	BoardingPassApplication appInstance;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance = (BoardingPassApplication) getActivity().getApplication();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG,"onCreateView");
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_add_screen_loggedin, container, false);
		img_add_boardingpass = (ImageView) v.findViewById(R.id.img_add_boardingpass);

		tv_add_boardingpass = (TextView) v.findViewById(R.id.tv_add_boardingpass);
		tv_add_boardingpass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onclickaddboardingpass();
			}
		});
		img_add_boardingpass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onclickaddboardingpass();
			}
		});

		return v;
	}

	/**
	 * Calls the {@link MainActivity#openDialogToAddBoardingPass() } method.
	 */
	public void onclickaddboardingpass() {
		((MainActivity) getActivity()).openDialogToAddBoardingPass();

	}

}
