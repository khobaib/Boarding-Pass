package com.seatunity.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
public class SeatMetList {
	 String success,count;
	private ArrayList<SeatMate>seatmatelist=new ArrayList<SeatMate>();
	public SeatMetList (){

	}
	
	public static  SeatMetList getSeatmetListObj(JSONObject joObject){
		String res=joObject.toString();
		Gson gson = new Gson();
		SeatMetList object = gson.fromJson(res, SeatMetList.class);
		return object;
	}
	public ArrayList<SeatMate> getAllSeatmateList(){
		return this.seatmatelist;
	}
	public String getSuccess(){
		return this.success;
	}
	public String getCount(){
		return this.count;
	}
}
