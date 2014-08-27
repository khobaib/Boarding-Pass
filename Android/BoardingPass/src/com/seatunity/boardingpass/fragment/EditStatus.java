
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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
public class EditStatus extends Fragment{
	EditStatusHolder parent;
	ListView lv_status;
	ArrayList<String> statuslist;
	EditText et_status;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		statuslist=new ArrayList<String>();
		statuslist.add("Available");
		statuslist.add("Busy");
		
		statuslist.add("Busy");
		statuslist.add("Busy");
		statuslist.add("Busy");
		statuslist.add("Busy");
		statuslist.add("Busy");
		statuslist.add("Busy");
		statuslist.add("Busy");
		statuslist.add("Busy");
		statuslist.add("Busy");
		statuslist.add("Busy");
		statuslist.add("Busy");
		statuslist.add("Busy");
		statuslist.add("Busy");

		
		statuslist.add("Do not sisturb");
		statuslist.add("Do not sisturb");
		statuslist.add("Do not sisturb");
		statuslist.add("Do not sisturb");
		statuslist.add("Do not sisturb");
		statuslist.add("Do not sisturb");
		statuslist.add("Do not sisturb");
		statuslist.add("Do not sisturb");
		statuslist.add("Do not sisturb");
		statuslist.add("Do not sisturb");

		
		statuslist.add("Online");
		statuslist.add("Online");
		statuslist.add("Online");
		statuslist.add("Online");
		statuslist.add("Online");
		statuslist.add("Online");
		statuslist.add("Online");
		statuslist.add("Online");




		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.edit_status,
				container, false);
		lv_status=(ListView) v.findViewById(R.id.lv_status);
		et_status=(EditText) v.findViewById(R.id.et_status);
		String[] stockArr = new String[statuslist.size()];
		
		  ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
	              R.layout.mytextview, R.id.tv_item, statuslist.toArray(stockArr));
		  
		  lv_status.setAdapter(adapter);
		  lv_status.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				et_status.setText(statuslist.get(position));
			}
		});

		return v;
	}

}

