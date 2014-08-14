package com.seatunity.boardingpass.fragment;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;


/**
 * Shows static text on the "Terms & Condition" about using this application.
 * 
 * @author Sumon
 * 
 */
@SuppressLint("NewApi")
public class FragmentTermsAndCondition extends Fragment  {
	HomeListFragment parent;
	TextView tv_title,tv_details;
	String title,details;
	public FragmentTermsAndCondition(String title,String details){
		this.title=title;
		this.details=details;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.terms_privacy_policy, container, false);
		tv_title=(TextView) rootView.findViewById(R.id.tv_title);
		tv_details=(TextView) rootView.findViewById(R.id.tv_details);
		tv_title.setText(title);
		tv_details.setText(details);
		((MainActivity)getActivity()).setTitle(getActivity().getResources().getString(R.string.about)+" "+
				getActivity().getResources().getString(R.string.app_name));

		return rootView;
	}
	
}