package com.seatunity.boardingpass.alert;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.adapter.AdapterForBoardingPass;
import com.seatunity.model.BoardingPass;

/**
 * @deprecated This calss was intended to show alerts/dialogs, but it's never
 *             used in anywhere inside this app.
 * 
 * @author Sumon
 * 
 */
public class LisAlertDialog {

	Context context;
	ArrayList<BoardingPass> item;

	/**
	 * @param context
	 * @param item
	 */
	public LisAlertDialog(Context context, ArrayList<BoardingPass> item) {
		this.context = context;
		this.item = item;

	}

	/**
	 * @deprecated Never used This method shows a dialog with no-message &
	 *             titled as "Shared Flights"
	 */
	public void show_alert() {
		ListView list_alert1;
		Button cancel_button;
		final Dialog dia = new Dialog(context);
		dia.setContentView(R.layout.list_alert);
		cancel_button = (Button) dia.findViewById(R.id.cancel_button);
		dia.setTitle(context.getResources().getString(R.string.txt_shared_flight_title));
		dia.setCancelable(true);
		list_alert1 = (ListView) dia.findViewById(R.id.alert_list);
		AdapterForBoardingPass arrayAdapter = new AdapterForBoardingPass(context, item);

		cancel_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dia.cancel();
			}
		});
		list_alert1.setAdapter(arrayAdapter);
		list_alert1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				dia.cancel();
			}
		});
		dia.show();
	}

	/**
	 * Nothing done inside this method.
	 * 
	 * @param position
	 */
	public void gomemberpage(int position) {
	}
}
