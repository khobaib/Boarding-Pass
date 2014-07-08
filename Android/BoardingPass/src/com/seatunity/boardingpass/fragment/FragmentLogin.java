
package com.seatunity.boardingpass.fragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.seatunity.boardingpass.AcountActivity;
import com.seatunity.boardingpass.HomeActivity;
import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class FragmentLogin extends Fragment{
	EditText et_email,et_password;
	TextView tv_errorshow;
	Button bt_login;
	String email,password;
	BoardingPassApplication appInstance;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance =(BoardingPassApplication) getActivity().getApplication();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.login,
				container, false);
		initview(v);
		return v;
	}
	public void initview(ViewGroup v){
		tv_errorshow=(TextView) v.findViewById(R.id.tv_errorshow);
		et_email=(EditText) v.findViewById(R.id.et_email);
		et_password=(EditText) v.findViewById(R.id.et_password);
		bt_login=(Button) v.findViewById(R.id.bt_login);
		
		et_password.addTextChangedListener(new TextWatcher() {          
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
            	et_password.setBackgroundResource(android.R.drawable.editbox_background_normal);
				tv_errorshow.setVisibility(View.GONE);

            }                       
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub                          
            }                       
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub                          

            }
        });
		et_email.addTextChangedListener(new TextWatcher() {          
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
            	et_email.setBackgroundResource(android.R.drawable.editbox_background_normal);
				tv_errorshow.setVisibility(View.GONE);


            }                       
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub                          
            }                       
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub                          

            }
        });
		bt_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				email=et_email.getText().toString();
				password=et_password.getText().toString();
				if(Constants.isOnline(getActivity())){
					if(!Constants.isValidEmail(email)){
						Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.txt_enter_valid_email),
								Toast.LENGTH_SHORT).show();
						et_email.setBackgroundResource(R.drawable.rounded_text_nofield);
					}
					else if(password.equals("")){
						Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.txt_enter_password),
								Toast.LENGTH_SHORT).show();
						et_password.setBackgroundResource(R.drawable.rounded_text_nofield);

					}
					else{
						callloginApi();
					}
				}
				else{
					Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.txt_check_internet),
							Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}
	public void callloginApi(){
		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("email", email);
			loginObj.put("password", password);
			String loginData = loginObj.toString();
			AsyncaTaskApiCall logincall=new AsyncaTaskApiCall(FragmentLogin.this, loginData, getActivity());
			logincall.execute();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void callBackFromApicall(ServerResponse serverResponse){
		setUsercredential(serverResponse.getjObj().toString());

	}
	
	public void setUsercredential(String result){
		UserCred usercred=new UserCred();
		try {
			JSONObject  job=new JSONObject (result);
			String status=job.getString("success");
			if(status.equals("true")){
				usercred=usercred.parseUserCred(job);
				usercred.setEmail(email);
				usercred.setPassword(password);
				appInstance.setUserCred(usercred);
				appInstance.setRememberMe(true);
				Intent intent=new Intent(getActivity(), MainActivity.class);
				intent.putExtra("select", 1);
		    	startActivity(intent);
		    	getActivity().finish();		
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_login_success), Toast.LENGTH_SHORT).show();
				
			}
			else{
				tv_errorshow.setVisibility(View.VISIBLE);
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_login_failed), Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
		}
	}
}

