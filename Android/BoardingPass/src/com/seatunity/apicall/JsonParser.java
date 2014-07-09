package com.seatunity.apicall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.ServerResponse;

import android.util.Log;


public class JsonParser {
	static InputStream          is   = null;
	static JSONObject           jObj = null;
	static String               json = "";
	private static final String TAG  = JsonParser.class.getSimpleName();
	public JsonParser() {

	}
	public ServerResponse retrieveServerData(int reqType, String url, List<NameValuePair> urlParams, String content, String appToken) {
		int status = 0;
		StringBuilder sb = null;
		if (urlParams != null) {
			String paramString = URLEncodedUtils.format(urlParams, "utf-8");
			url += "?" + paramString;     
			
			Log.d(TAG, "content body = " + content);
		}
		Log.d(TAG, "url after param added = " + url);
	try {
				URL urltoconnect = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) urltoconnect
			            .openConnection();
				connection.setRequestProperty("Content-Type","application/json"); 
				Log.e("test", "16");
			    connection.setDoOutput(true);
			    Log.e("test", "17");
			HttpResponse httpResponse = null;
			if(reqType == Constants.REQUEST_TYPE_GET){
				connection.setRequestMethod("GET");
						connection.connect();
						int statusCode = connection.getResponseCode();
			} else if(reqType == Constants.REQUEST_TYPE_POST){
				connection.setRequestMethod("POST");
				String str =  content;
				byte[] outputInBytes = str.getBytes("UTF-8");
				OutputStream os = connection.getOutputStream();
				os.write( outputInBytes );    
				os.close();
				connection.connect();
				int statusCode = connection.getResponseCode();
				
				//07-09 17:23:44.360: E/AndroidRuntime(10182): android.database.sqlite.SQLiteException: unrecognized token: "[Ljava.lang.String;@4292bca8": , while compiling: UPDATE boardingpass SET departure=?,lastname=?,PNR=?,firstname=?,flight_no=?,julian_date=?,id=?,seat=?,compartment_code=?,arrival=?,carrier=?,stringform=?,codetype=?,travel_to=?,travel_from=? WHERE stringform= ?[Ljava.lang.String;@4292bca8

			//	Ljava.lang.String;@4292bca8": , while compiling: UPDATE boardingpass SET departure=?,lastname=?,PNR=?,firstname=?
//				HttpPost httpPost = new HttpPost(url);
//				httpPost.setHeader("Content-Type", "application/json");
//				httpPost.setHeader("Accept", "application/json");
//				if (appToken != null){
//					httpPost.setHeader("token", appToken);
//				}
//
//				StringEntity se = new StringEntity(content);
//				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//				httpPost.setEntity(se);
//			
//				httpResponse = httpClient.execute(httpPost);                
			} else if(reqType == Constants.REQUEST_TYPE_PUT){
				connection.setRequestProperty("Content-Type","application/json"); 
				connection.setRequestMethod("PUT");
				String str =  content;
				byte[] outputInBytes = str.getBytes("UTF-8");
				OutputStream os = connection.getOutputStream();
				os.write( outputInBytes );    
				os.close();
				connection.connect();
				int statusCode = connection.getResponseCode();
				// writer.write(message);
//				connection.connect();
//				int statusCode = connection.getResponseCode();
//				HttpPut httpPut = new HttpPut(url);
//				httpPut.setHeader("Content-Type", "application/json");
//				httpPut.setHeader("Accept", "application/json");
//				if (appToken != null){
//					httpPut.setHeader("token", appToken);
//				}
//
//				if(content != null){
//					StringEntity se = new StringEntity(content);
//					se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//					httpPut.setEntity(se);
//				}
//
//				httpResponse = httpClient.execute(httpPut);   
				
				
				/*else if(reqType == Constants.REQUEST_TYPE_PUT){
	                HttpPut httpPut = new HttpPut(url);
	                httpPut.setHeader("Content-Type", "application/json");
	                httpPut.setHeader("Accept", "application/json");
	                if (appToken != null){
	                    httpPut.setHeader("token", appToken);
	                }

	                if(content != null){
	                    StringEntity se = new StringEntity(content);
	                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, 
	                    		"application/json"));
	                    httpPut.setEntity(se);
	                }

	                httpResponse = httpClient.execute(httpPut);                
	            } else if(reqType == Constants.REQUEST_TYPE_DELETE){
	                HttpDelete httpDelete = new HttpDelete(url);
	                httpDelete.setHeader("Content-Type", "application/json");
	                httpDelete.setHeader("Accept", "application/json");
	                if (appToken != null){
	                    httpDelete.setHeader("token", appToken);
	                }

	                if(content != null){
	                    StringEntity se = new StringEntity(content);
	                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, 
	                    		"application/json"));
	                    ((HttpResponse) httpDelete).setEntity(se);
	                }

	                httpResponse = httpClient.execute(httpDelete);                
	            }*/
				
				
			} else if(reqType == Constants.REQUEST_TYPE_DELETE){
				connection.setRequestProperty("Content-Type","application/json"); 
				connection.setRequestMethod("DELETE");
				String str =  content;
				byte[] outputInBytes = str.getBytes("UTF-8");
				OutputStream os = connection.getOutputStream();
				os.write( outputInBytes );    
				os.close();
				connection.connect();
				int statusCode = connection.getResponseCode();
//				connection.connect();
//				int statusCode = connection.getResponseCode();
//				HttpDelete httpDelete = new HttpDelete(url);
//				httpDelete.setHeader("Content-Type", "application/json");
//				httpDelete.setHeader("Accept", "application/json");
//				if (appToken != null){
//					httpDelete.setHeader("token", appToken);
//				}
//
//				if(content != null){
//					StringEntity se = new StringEntity(content);
//					se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//					((HttpResponse) httpDelete).setEntity(se);
//				}
//
//				httpResponse = httpClient.execute(httpDelete);                
			}

			status = connection.getResponseCode();
			//connection.
			Log.d(TAG, "STAUS = " + status);
			Log.e("test", "32");

		//	HttpEntity httpEntity = httpResponse.getEntity();
			is = connection.getInputStream();
		} catch (UnsupportedEncodingException e) {
			Log.e("test", "33");

			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.e("test", "34");

			e.printStackTrace();
		} catch (IOException e) {
			Log.e("test", "35");

			e.printStackTrace();
		}

		try {
			Log.d(TAG, "trying to read input stream.");
//			new InputStreamReader			
			Log.e("test", "36");

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			Log.e("test", "37");

			sb = new StringBuilder();
			String line = null;
			Log.e("test", "380");

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			Log.e("test", "39");

			is.close();
			Log.d(TAG, "sb = " + sb.toString());
			json = sb.toString();
		} catch (Exception e) {
			Log.e("test", "40");

			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			Log.e("test", "41");

			jObj = new JSONObject(json);
			Log.e("test", "42");

		} catch (JSONException e) {
			Log.e("test", "43");

			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}
		Log.e("test", "44");

		// return ServerResponse
		return new ServerResponse(jObj, status);
	}

}
