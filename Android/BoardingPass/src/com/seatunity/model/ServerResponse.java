package com.seatunity.model;

import org.json.JSONObject;

import android.util.Log;

/**
 * A typical model to hold any server-response with an integer status-code & a
 * {@link JSONObject}
 * 
 * @author Sumon
 * 
 */
public class ServerResponse {
	JSONObject jObj;
	int status;

	public ServerResponse() {
	}

	public ServerResponse(JSONObject jObj, int status) {
		Log.e("test", "46");

		this.jObj = jObj;
		Log.e("test", "47");

		this.status = status;
		Log.e("test", "48");

	}

	public JSONObject getjObj() {
		return jObj;
	}

	public void setjObj(JSONObject jObj) {
		this.jObj = jObj;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
