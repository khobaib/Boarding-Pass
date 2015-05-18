package com.seatunity.boardingpass.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.adapter.PastFlightListAdapter;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.boardingpass.utilty.Utility;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassList;
import com.seatunity.model.ServerResponse;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FragmentPastFlights extends Fragment implements CallBackApiCall{
	private BoardingPassApplication appInstance;
	private List<BoardingPass> boardingPassliList;
	private ListView lvPastFlights;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.fragment_past_flights,null,false);
		lvPastFlights=(ListView)v.findViewById(R.id.lv_bpass);
		return v;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		appInstance = (BoardingPassApplication) getActivity().getApplication();
		if(Utility.hasInternet(getActivity()))
		{
			new AsyncaTaskApiCall(this, getJsonObjet(), getActivity(), "bplist",
					Constants.REQUEST_TYPE_POST).execute();
		}
		else
		{
			SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
			dbInstance.open();
			// dbInstance.retrieveFutureBoardingPassList();
			boardingPassliList=dbInstance.retrievePastBoardingPassList();
			PastFlightListAdapter pastFlightListAdapter=new PastFlightListAdapter(getActivity(),boardingPassliList);
			Log.e("size",""+boardingPassliList.size());
			lvPastFlights.setAdapter(pastFlightListAdapter);
			dbInstance.close();
		}
	}
	public String getJsonObjet() {
		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token", appInstance.getUserCred().getToken());
			loginObj.put("boarding_pass", "all");
			return loginObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	@Override
	public void responseOk(JSONObject job) {
		ArrayList<BoardingPass> allBoardingPassList;
		try {
			if (job.getString("success").equals("true")) {
				allBoardingPassList = BoardingPassList.getBoardingPassListObject(job).getBoardingPassList();
				Log.e("msg",job.toString());
				SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
				/*Log.i(TAG, "Server  has retrieved BPass list ... Now saving BPass in local DB.");
				dbInstance.open();
				for (int i = 0; i < allBoardingPassList.size(); i++) {
					Log.d(TAG, "Inserting: " + i + " : BPass For Travel From: "
							+ allBoardingPassList.get(i).getTravel_from_name());
					dbInstance.insertOrUpdateBoardingPass(allBoardingPassList.get(i));
				}
				// isolate future b-pass from all b-pass
				ArrayList<BoardingPass> fbpList = isolateFutureBPass(allBoardingPassList);
				if (fbpList.size() < futureBoardingPassList.size()) {
					// TO_DO upload the unsynched local boarding passes to
					// server
					Log.e(TAG, "Upload the UNSYNCHED local boarding passes to server");
					// Log.e(TAG,
					// "UNSYNCHED B.PASS in LOCAL DB !!!!!!\nSynchronizing with remote ...");
					// for (BoardingPass boardingPass : futureBoardingPassList)
					// {
					// if (!fbpList.contains(boardingPass)) {
					// saveBoardingPasstoServer(boardingPass);
					// }
					// }
				} else {
					futureBoardingPassList = (ArrayList<BoardingPass>) dbInstance.retrieveFutureBoardingPassList();
				}
				Log.i(TAG, "Got B-pass list as: " + futureBoardingPassList.size());
				if (futureBoardingPassList.size() < 1) {
					// TODO Look for past list
					ArrayList<BoardingPass> pBpList = (ArrayList<BoardingPass>) dbInstance
							.retrievePastBoardingPassList();
					if (pBpList.size() > 0 && BoardingPassApplication.getHookCallMode()){
						parent.startPastBPassFragmment();
						BoardingPassApplication.setHookCallMode(false);
					}
					else
						parent.startAddBoardingPassDuringLogin();
				} else {
					parent.startFragmentBoardingPasses();
				}
				dbInstance.close();*/
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public void responseFailure(JSONObject job) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void responseFailure(ServerResponse response) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void saveLoginCred(JSONObject job) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void LoginFailed(JSONObject job) {
		// TODO Auto-generated method stub
		
	}
}