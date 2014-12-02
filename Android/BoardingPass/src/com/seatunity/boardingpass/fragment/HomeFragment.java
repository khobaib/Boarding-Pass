package com.seatunity.boardingpass.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seatunity.boardingpass.AcountActivity;
import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;

/**
 * This fragment is shown when the user has not yet registered to the system or
 * has been loagged out. It contains a plus button to add a new boarding pass
 * without registration & a register button to register to the system.
 * 
 * @author Sumon
 * 
 */
@SuppressLint("NewApi")
public class HomeFragment extends Fragment {

	private final String TAG = this.getClass().getSimpleName();

	ImageView img_add_boardingpass, img_addprofile;
	TextView tv_add_boardingpass, tv_add_profile;
	HomeListFragment parent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Log.i(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_add_screen_not_loggedin, container, false);
		img_add_boardingpass = (ImageView) rootView.findViewById(R.id.img_add_boardingpass);
		img_addprofile = (ImageView) rootView.findViewById(R.id.img_addprofile);
		tv_add_boardingpass = (TextView) rootView.findViewById(R.id.tv_add_boardingpass);
		tv_add_profile = (TextView) rootView.findViewById(R.id.tv_add_profile);
		img_addprofile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onclickprofile();
			}
		});
		tv_add_profile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onclickprofile();
			}
		});
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

		ImageView ivWelcome = (ImageView) rootView.findViewById(R.id.iv_welcome);
		float w = getActivity().getResources().getDisplayMetrics().widthPixels * 0.3f;
		Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.welcome_txt);
		bm = Bitmap.createScaledBitmap(bm, Math.round(w), Math.round(w / bm.getWidth() * bm.getHeight()), true);
		ivWelcome.setImageBitmap(bm);

		return rootView;
	}

	/**
	 * Opens the {@link AcountActivity}
	 */
	public void onclickprofile() {
		Intent intent = new Intent(getActivity(), AcountActivity.class);
		intent.putExtra("to", "0");
		startActivity(intent);
		// getActivity().finish();
	}

	/**
	 * Opens the dialog to add a new boarding-pass.
	 */
	public void onclickaddboardingpass() {
		((MainActivity) getActivity()).openDialogToAddBoardingPass();

	}
}