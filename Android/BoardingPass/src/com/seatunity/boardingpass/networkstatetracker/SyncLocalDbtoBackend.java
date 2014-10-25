package com.seatunity.boardingpass.networkstatetracker;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;

import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;

/**
 * The class extends the {@link BroadcastReceiver} & listens to the internet
 * on-off events to perform some network operations.
 * 
 * @author Sumon
 * 
 */
public class SyncLocalDbtoBackend {
	BoardingPassApplication appInstance;
	BoardingPass boardingPass;
	Context context;

	/**
	 * Saves own {@link BoardingPass} object to the DB
	 * 
	 * @param context
	 */
	public void sendBoardingPasstoRemoteDB(Context context) {
		this.context = context;
		appInstance = (BoardingPassApplication) context.getApplicationContext();
		if (Constants.isOnline(context)) {
			SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
			dbInstance.open();
			ArrayList<BoardingPass> list = (ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
			dbInstance.close();
			if (!appInstance.getUserCred().getEmail().equals("")) {
				// Log.e("", msg)
				for (int i = 0; i < list.size(); i++) {
					// if(list.get(i).getDeletestate()){
					// String url="bpdelete/"+list.get(i).getId();
					// AsyncaTaskApiCall callserver=new
					// AsyncaTaskApiCall(NetworkStateReceiver.this,
					// getJsonObjet(), context, url,list.get(i));
					// callserver.execute();
					// }
					if (list.get(i).getId().equals("-1")) {
						boardingPass = list.get(i);
						String bpassdata = "";
						bpassdata = getJsonObject(list.get(i));
						AsyncaTaskApiCall apicalling = new AsyncaTaskApiCall(SyncLocalDbtoBackend.this, boardingPass,
								bpassdata, context);
						apicalling.execute();
					}
				}
			}

			// else
			// if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE))
			// {
			// SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
			// dbInstance.open();
			//
			// ArrayList<BoardingPass> list=(ArrayList<BoardingPass>)
			// dbInstance.retrieveBoardingPassList();
			// dbInstance.close();
			// for(int i=0;i<list.size();i++){
			// Log.e("i", list.get(i).getId());
			// }
			// }
		}
	}

	/**
	 * @return the json-string containing the app-token only.
	 */
	public String getJsonObject() {

		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token", appInstance.getUserCred().getToken());
			return loginObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * If the {@code result.getString("success").equals("true") }, then the
	 * method saves the {@link BoardingPass } to the app database.
	 * 
	 * @param result
	 * @param bpass
	 */
	public void addBoardingPassonBackendSuccess(JSONObject result, BoardingPass bpass) {
		Log.e("result", result.toString());
		try {
			String success = result.getString("success");
			if (success.equals("true")) {
				String id = result.getString("id");
				bpass.setId(id);
				// Context context;
				setBoardingpassInLocalDB(bpass);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the BoardingPass object to the app's database.
	 * 
	 * @param bpass
	 */
	public void setBoardingpassInLocalDB(BoardingPass bpass) {
		Log.e("data", "ab  " + bpass.getStringform());
		SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
		dbInstance.open();
		dbInstance.insertOrUpdateBoardingPass(bpass);
		dbInstance.close();
	}

	/**
	 * @return the json-string containing the app-token, version, stringform,
	 *         firstname, lastname, PNR, travel_form, travel_to, carrier,
	 *         flight_no, julian_date, compartment_code, seat, departure,
	 *         arrival & year.
	 */
	public String getJsonObject(BoardingPass bpass) {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);

		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token", appInstance.getUserCred().getToken());
			loginObj.put("version", "1");
			loginObj.put("stringform", bpass.getStringform());
			loginObj.put("firstname", bpass.getFirstname());
			loginObj.put("lastname", bpass.getLastname());
			loginObj.put("PNR", bpass.getPNR());
			loginObj.put("travel_from", bpass.getTravel_from());
			loginObj.put("travel_to", bpass.getTravel_to());
			loginObj.put("carrier", bpass.getCarrier());
			loginObj.put("flight_no", bpass.getFlight_no());
			loginObj.put("julian_date", bpass.getJulian_date());
			loginObj.put("compartment_code", bpass.getCompartment_code());
			loginObj.put("seat", bpass.getSeat());
			loginObj.put("departure", bpass.getDeparture());
			loginObj.put("arrival", bpass.getArrival());
			loginObj.put("year", year);
			return loginObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * The passed boarding pass is deleted from the DB
	 * 
	 * @param bpass
	 */
	public void updateDatabaseWithoutServernotification(BoardingPass bpass) {
		SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
		dbInstance.open();
		dbInstance.DeleteBoardingPass(bpass);
		dbInstance.close();
	}
}