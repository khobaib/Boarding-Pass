package com.seatunity.boardingpass.interfaces;

import org.json.JSONObject;

public interface CallBackApiCall {
 public void responseOk(JSONObject job);
 public void responseFailure(JSONObject job);
 public void saveLoginCred(JSONObject job);
 public void LoginFailed(JSONObject job);
}
