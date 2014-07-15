package com.seatunity.boardingpass.alert;
import java.util.ArrayList;

import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.adapter.AdapterForBoardingPass;
import com.seatunity.model.BoardingPass;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
public class LisAlertDialog {
	
	Context context;
	ArrayList<BoardingPass> item;
	
	
	
	
	public LisAlertDialog(Context context,ArrayList<BoardingPass> item){
		
		this.context=context;	
		this.item=item;
	
	}
	public void show_alert() {
		ListView list_alert1;
		Button cancel_button;
		final Dialog dia = new Dialog(context);
		dia.setContentView(R.layout.list_alert);
		cancel_button=(Button) dia.findViewById(R.id.cancel_button);
		dia.setTitle(context.getResources().getString(R.string.txt_shared_flight_title));
		dia.setCancelable(true);
		list_alert1 = (ListView) dia.findViewById(R.id.alert_list);
		AdapterForBoardingPass arrayAdapter=new AdapterForBoardingPass(context, item);
		
		cancel_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dia.cancel();
			}
		});
		list_alert1.setAdapter(arrayAdapter); 
		list_alert1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				
				dia.cancel();
			}
		});
		dia.show();
	}
	public void gomemberpage(int position){
	}
}
