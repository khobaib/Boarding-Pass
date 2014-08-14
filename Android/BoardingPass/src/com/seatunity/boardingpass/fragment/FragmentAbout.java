package com.seatunity.boardingpass.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;

/**
 * Shows some static-information about the app.
 * 
 * @author Sumon
 * 
 */
@SuppressLint("NewApi")
public class FragmentAbout extends Fragment implements OnClickListener {
	HomeListFragment parent;
	TextView tv_verion_code, tv_send_feedback, tv_report_prob, tv_rate_app, tv_trmsof_srvs, tv_prvcy_pilcy,
			tv_let_know_bug;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.about, container, false);
		tv_let_know_bug = (TextView) rootView.findViewById(R.id.tv_let_know_bug);
		tv_verion_code = (TextView) rootView.findViewById(R.id.tv_verion_code);
		tv_send_feedback = (TextView) rootView.findViewById(R.id.tv_send_feedback);
		tv_rate_app = (TextView) rootView.findViewById(R.id.tv_rate_app);
		tv_report_prob = (TextView) rootView.findViewById(R.id.tv_report_prob);
		tv_trmsof_srvs = (TextView) rootView.findViewById(R.id.tv_trmsof_srvs);
		tv_prvcy_pilcy = (TextView) rootView.findViewById(R.id.tv_prvcy_pilcy);
		tv_send_feedback.setPaintFlags(tv_send_feedback.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		tv_rate_app.setPaintFlags(tv_rate_app.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		tv_report_prob.setPaintFlags(tv_report_prob.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		tv_trmsof_srvs.setPaintFlags(tv_trmsof_srvs.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		tv_prvcy_pilcy.setPaintFlags(tv_prvcy_pilcy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		((MainActivity) getActivity()).setTitle(getActivity().getResources().getString(R.string.about) + " "
				+ getActivity().getResources().getString(R.string.app_name));
		tv_send_feedback.setOnClickListener(this);
		tv_report_prob.setOnClickListener(this);
		tv_rate_app.setOnClickListener(this);
		tv_trmsof_srvs.setOnClickListener(this);
		tv_prvcy_pilcy.setOnClickListener(this);

		return rootView;
	}

	// public void onclickprofile(){
	// Intent intent=new Intent(getActivity(), AcountActivity.class);
	// intent.putExtra("to", "0");
	// startActivity(intent);
	// getActivity().finish();
	//
	// }

	public void onclickaddboardingpass() {
		((MainActivity) getActivity()).openDialogToAddBoardingPass();

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tv_send_feedback) {

		} else if (v.getId() == R.id.tv_report_prob) {

		} else if (v.getId() == R.id.tv_rate_app) {
			/* This code assumes you are inside an activity */
			launchMarket();

		} else if (v.getId() == R.id.tv_prvcy_pilcy) {
			parent.startFragmentTermsAndCondition(getActivity().getString(R.string.privcy_plcy), getActivity()
					.getString(R.string.lorem_text));

		} else if (v.getId() == R.id.tv_trmsof_srvs) {
			parent.startFragmentTermsAndCondition(getActivity().getString(R.string.txt_term_and_condition),
					getActivity().getString(R.string.lorem_text));

		}

	}

	private void launchMarket() {
		Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
		Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(myAppLinkToMarket);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getActivity(), getActivity().getString(R.string.app_not_found), Toast.LENGTH_LONG).show();
		}
	}
}