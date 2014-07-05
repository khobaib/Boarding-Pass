package com.seatunity.boardingpass.fragment;
import com.seatunity.boardingpass.AcountActivity;
import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
 
@SuppressLint("NewApi")
public class HomeFragment extends Fragment {
     
     ImageView img_add_boardingpass,img_addprofile;
     TextView tv_add_boardingpass,tv_add_profile;
     HomeListFragment parent;
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_add_screen_not_loggedin, container, false);
        img_add_boardingpass=(ImageView) rootView.findViewById(R.id.img_add_boardingpass);
        img_addprofile=(ImageView) rootView.findViewById(R.id.img_addprofile);
        tv_add_boardingpass=(TextView) rootView.findViewById(R.id.tv_add_boardingpass);
        tv_add_profile=(TextView) rootView.findViewById(R.id.tv_add_profile);
        img_addprofile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onclickprofile();
			}
		});
        tv_add_profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onclickprofile();
			}
		});
        tv_add_boardingpass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onclickaddboardingpass();	
			}
		});
        img_add_boardingpass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onclickaddboardingpass();
			}
		});
          
        return rootView;
    }
    public void onclickprofile(){
    	Intent intent=new Intent(getActivity(), AcountActivity.class);
    	startActivity(intent);
    	getActivity().finish();
    	
    }
    
    public void onclickaddboardingpass(){
    	parent.startAddBoardingPass();
		

    }
}