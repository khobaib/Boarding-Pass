package com.seatunity.boardingpass.fragment;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.adapter.AdapterForPastBoardingPass;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.model.BoardingPass;

/**
 * Fragment showing the past boarding-passes
 * 
 * @author Sumon
 * 
 */
@SuppressLint("NewApi")
public class FragmentPastBoardingPasses extends Fragment {
	
	private final String TAG=this.getClass().getSimpleName();
	
	PastBoardingPassListFragment parent;
	EditText et_email, et_password;
	TextView tv_errorshow;
	Button bt_login;
	ArrayList<BoardingPass> list;
	String email, password;
	BoardingPassApplication appInstance;
	ListView lv_boarding_past_pass;
	RelativeLayout re_errorshow, re_list_holder;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance = (BoardingPassApplication) getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG,"onCreateView");
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_past_boarding_pases, container, false);
		re_errorshow = (RelativeLayout) v.findViewById(R.id.re_errorshow);
		re_list_holder = (RelativeLayout) v.findViewById(R.id.re_list_holder);
		Calendar c = Calendar.getInstance();
		int dayofyear = c.get(Calendar.DAY_OF_YEAR);
		Log.e("dayofyear", "" + dayofyear);
		lv_boarding_past_pass = (ListView) v.findViewById(R.id.lv_boarding_past_pass);
		tv_errorshow = (TextView) v.findViewById(R.id.tv_errorshow);
		SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
		dbInstance.open();
		list = (ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
		dbInstance.close();
		ArrayList<BoardingPass> list_smaller = new ArrayList<BoardingPass>();
		// list.get(0).getJulian_date();
		for (int count = 0; count < list.size(); count++) {
			int ju_date = Integer.parseInt(list.get(count).getJulian_date().trim());
			if ((ju_date < dayofyear)) {
				list_smaller.add(list.get(count));
			}

		}
		if (list_smaller.size() > 0) {
			re_errorshow.setVisibility(View.GONE);
			re_list_holder.setVisibility(View.VISIBLE);
			AdapterForPastBoardingPass adapter = new AdapterForPastBoardingPass(getActivity(), list_smaller);
			lv_boarding_past_pass.setAdapter(adapter);
		} else {
			re_errorshow.setVisibility(View.VISIBLE);
			re_list_holder.setVisibility(View.GONE);

		}

		return v;
	}

}
