package com.seatunity.boardingpass.fragment;

import com.seatunity.boardingpass.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentUpgradePager extends Fragment{
	
	private ImageView ivHeader,ivBottom;
	private TextView tvDescription;
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
	
		View v=inflater.inflate(R.layout.fragment_upgrade_pager,null,false);
		ivHeader=(ImageView)v.findViewById(R.id.iv_header);
		ivBottom=(ImageView)v.findViewById(R.id.iv_bottom);
		tvDescription=(TextView)v.findViewById(R.id.tv_des);
		setData();
		return v;
	}
	private void setData()
	{
		int position = getArguments().getInt("position");
		if (position == 0) {
			ivHeader.setImageResource(R.drawable.premium_account_ideal_seatm);
			ivBottom.setImageResource(R.drawable.ic_slider_1);
			tvDescription.setText(R.string.premium_account_ideal_seatmate);
		} else if (position == 1) {
			ivHeader.setImageResource(R.drawable.premium_account_messaging_f);
			ivBottom.setImageResource(R.drawable.ic_slider_2);
			tvDescription.setText(R.string.premium_account_messaging_feature);
		} else if (position == 2) {
			ivHeader.setImageResource(R.drawable.premium_account_full_profil);
			ivBottom.setImageResource(R.drawable.ic_slider_3);
			tvDescription.setText(R.string.premium_account_full_profile);
		} else if (position == 3) {
			ivHeader.setImageResource(R.drawable.premium_account_add_flight);
			ivBottom.setImageResource(R.drawable.ic_slider_4);
			tvDescription.setText(R.string.premium_account_flight);
		} else if (position == 4) {
			ivHeader.setImageResource(R.drawable.premium_account_shared_flig);
			ivBottom.setImageResource(R.drawable.ic_slider_5);
			tvDescription.setText(R.string.premium_account_shared_flight);
		}
		
	}

}
