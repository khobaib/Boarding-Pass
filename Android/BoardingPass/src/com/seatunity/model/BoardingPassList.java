package com.seatunity.model;

import java.util.ArrayList;

import org.json.JSONObject;

import com.google.gson.Gson;

public class BoardingPassList {
	private String status;
	private ArrayList<BoardingPass> boarding_pass = new ArrayList<BoardingPass>();

	/**
	 * Empty constructor doinf nothing special inside.
	 * 
	 */
	public BoardingPassList() {
	}

	/**
	 * @param status
	 * @param boarding_pass_list
	 */
	public BoardingPassList(String status, ArrayList<BoardingPass> boarding_pass_list) {
		this.boarding_pass = boarding_pass_list;
		this.status = status;
	}

	/**
	 * @param joObject
	 * @return
	 */
	public static BoardingPassList getBoardingPassListObject(JSONObject joObject) {
		String res = joObject.toString();
		Gson gson = new Gson();
		BoardingPassList object = gson.fromJson(res, BoardingPassList.class);
		return object;
	}

	/**
	 * @return all the boarding pass as a list
	 */
	public ArrayList<BoardingPass> getBoardingPassList() {
		return this.boarding_pass;
	}

	/**
	 * @return status string of the pass-list
	 */
	public String getStatus() {
		return this.status;
	}
}
