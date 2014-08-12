package com.seatunity.model;

import java.util.ArrayList;

import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * @author Sumon
 * 
 */
public class SeatMetList {
	String success, count;
	private ArrayList<SeatMate> seatmatelist = new ArrayList<SeatMate>();

	/**
	 * Empty constructor doing nothing special
	 */
	public SeatMetList() {
	}

	/**
	 * Returns a {@link SeatMetList } object constructed by the {@link GSON }
	 * library from the passed JSON Object
	 * 
	 * @param jsonObject
	 * @return {@link SeatMetList } object
	 */
	public static SeatMetList getSeatmetListObj(JSONObject jsonObject) {
		String res = jsonObject.toString();
		Gson gson = new Gson();
		SeatMetList object = gson.fromJson(res, SeatMetList.class);
		return object;
	}

	/**
	 * @return seatmatelist
	 */
	public ArrayList<SeatMate> getAllSeatmateList() {
		return this.seatmatelist;
	}

	/**
	 * @return success
	 */
	public String getSuccess() {
		return this.success;
	}

	/**
	 * @return count
	 */
	public String getCount() {
		return this.count;
	}
}
