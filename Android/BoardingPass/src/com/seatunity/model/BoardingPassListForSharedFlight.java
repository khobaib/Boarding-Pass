package com.seatunity.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
public class BoardingPassListForSharedFlight {
	private String status;
	private ArrayList<BoardingPass>boarding_passes=new ArrayList<BoardingPass>();
	public BoardingPassListForSharedFlight (){

	}
	public BoardingPassListForSharedFlight (String status,ArrayList<BoardingPass>article_list){
		this.boarding_passes=boarding_passes;
		this.status=status;
	}
	public static  BoardingPassListForSharedFlight getBoardingPassListObject(JSONObject joObject){
		String res=joObject.toString();
		Gson gson = new Gson();
		BoardingPassListForSharedFlight object = gson.fromJson(res, BoardingPassListForSharedFlight.class);
		return object;
	}
	public ArrayList<BoardingPass> getBoardingPassList(){
		return this.boarding_passes;
	}
	public String getStatus(){
		return this.status;
	}
}
