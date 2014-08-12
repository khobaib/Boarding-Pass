package com.seatunity.model;

import java.util.ArrayList;

import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * @author Sumon
 *
 */
public class BoardingPassListForSharedFlight {
	private String status;
	private ArrayList<BoardingPass> boarding_passes = new ArrayList<BoardingPass>();

	/**
	 * Empty constructor doing nothing special inside
	 */
	public BoardingPassListForSharedFlight() {

	}

	/**
	 * @param status
	 * @param boarding_passes_list
	 */
	public BoardingPassListForSharedFlight(String status, ArrayList<BoardingPass> boarding_passes_list) {
		this.boarding_passes = boarding_passes_list;
		this.status = status;
	}

	/**
	 * @param joObject
	 * @return
	 */
	public static BoardingPassListForSharedFlight getBoardingPassListObject(JSONObject joObject) {
		String res = joObject.toString();
		Gson gson = new Gson();
		BoardingPassListForSharedFlight object = gson.fromJson(res, BoardingPassListForSharedFlight.class);
		return object;
	}


	/**
	 * @return all the boarding pass as a list
	 */
	public ArrayList<BoardingPass> getBoardingPassList() {
		return this.boarding_passes;
	}

	/**
	 * @return status string of the pass-list
	 */
	public String getStatus() {
		return this.status;
	}
}
