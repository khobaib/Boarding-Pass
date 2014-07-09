package com.seatunity.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
public class BoardingPassList {
	private String status;
	private ArrayList<BoardingPass>boarding_pass=new ArrayList<BoardingPass>();
	public BoardingPassList (){

	}
	public BoardingPassList (String status,ArrayList<BoardingPass>article_list){
		this.boarding_pass=boarding_pass;
		this.status=status;
	}
	public static  BoardingPassList getBoardingPassListObject(JSONObject joObject){
		String res=joObject.toString();
		Gson gson = new Gson();
		BoardingPassList object = gson.fromJson(res, BoardingPassList.class);
		return object;
	}
	public ArrayList<BoardingPass> getBoardingPassList(){
		return this.boarding_pass;
	}
	public String getStatus(){
		return this.status;
	}
}
