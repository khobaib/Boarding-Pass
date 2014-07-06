package com.seatunity.boardingpass.networkstatetracker;
import java.util.ArrayList;

import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.model.BoardingPass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class NetworkStateReceiver extends BroadcastReceiver {
	BoardingPassApplication appInstance;
	public void onReceive(Context context, Intent intent) {
		if(intent.getExtras()!=null) {
			NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
			appInstance =(BoardingPassApplication) context.getApplicationContext();
			
			if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
				Toast.makeText(context, "Connectes", 2000).show();
				
				SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
				dbInstance.open();
				
				ArrayList list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
				dbInstance.close();
				if(!appInstance.getUserCred().getEmail().equals("")){
					
					
				}
				
			} else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
				Toast.makeText(context, "Not Connected", 2000).show();
				
			}
		}
	}
}