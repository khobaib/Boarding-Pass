package com.seatunity.boardingpass.interfaces;

import org.json.JSONObject;

import com.seatunity.model.ServerResponse;

/**
 * Interface to implement the call-backs of the AsyncTask responses into the
 * active class (fragment or activity)
 * 
 * @author Sumon
 * 
 */
public interface CallBackApiCall {

	/**
	 * Call-back for a successfull response from the API.
	 * 
	 * @param job
	 *            the received JSONObject
	 */
	public void responseOk(JSONObject job);

	/**
	 * Call-back for a unsuccessfull response from the API.
	 * 
	 * @param job
	 *            the received JSONObject
	 */
	public void responseFailure(JSONObject job);

	/**
	 * Call-back for a unsuccessfull response from the API (esp., for a status
	 * code other than 200, i.e., net-connectivity is lost in midst of a
	 * net-operation).
	 * 
	 * @param response
	 *            the received {@link ServerResponse}
	 */
	public void responseFailure(ServerResponse response);

	/**
	 * Call-back to save the log-in credentials, even if the user's
	 * device-credential gets invalidated due to log-in to another device.
	 * 
	 * @param job
	 *            the received JSONObject
	 */
	public void saveLoginCred(JSONObject job);

	/**
	 * Call-back to re-dial the log-in process if the user's device-credential
	 * gets invalidated due to log-in to another device.
	 * 
	 * @param job
	 *            the received JSONObject
	 */
	public void LoginFailed(JSONObject job);
}
