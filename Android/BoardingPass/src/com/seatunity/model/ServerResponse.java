package com.seatunity.model;

import org.json.JSONObject;

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
		this.jObj = jObj;
		this.status = status;
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
