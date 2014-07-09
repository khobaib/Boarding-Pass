package com.seatunity.boardingpass;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.bugsense.trace.BugSenseHandler;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.UserCred;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
 
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class EditUserNameActivity extends Activity {
	EditText et_uname,et_email,et_status;
	String username,email,status;
	BoardingPassApplication appInstance;
	UserCred userCred;
	Drawable back;
	EditUserNameActivity lisenar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(EditUserNameActivity.this, "2b60c090");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        appInstance =(BoardingPassApplication)getApplication();
        userCred=appInstance.getUserCred();
        setContentView(R.layout.edit_user_name);
        lisenar=this;
        et_uname=(EditText) findViewById(R.id.et_uname);
        et_email=(EditText) findViewById(R.id.et_email);
        et_status=(EditText) findViewById(R.id.et_status);
        back=et_status.getBackground();
        
        editTextControl();
        setView();
    }
    public void editTextControl(){
    	et_uname.addTextChangedListener(new TextWatcher() {          
             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
            	 et_uname.setBackgroundDrawable(back);
 				


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
            	 et_email.setBackgroundDrawable(back);
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
    	et_status.addTextChangedListener(new TextWatcher() {          
             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
            	 et_status.setBackgroundDrawable(back);
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
    }
    public void setView(){
    	et_uname.setText(appInstance.getUserCred().getFirstname());
    	et_email.setText(appInstance.getUserCred().getEmail());
    	et_status.setText(appInstance.getUserCred().getStatus());

    }
    @Override
    protected void onStop() {
        super.onStop();
        BugSenseHandler.closeSession(EditUserNameActivity.this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void Cancel(View view){
    	finish();
    }
    public void ChangeStatus(View view){
    	username=et_uname.getText().toString();
    	email=et_email.getText().toString();
    	status=et_status.getText().toString();
    	if(!status.equals("")){
    		if(Constants.isValidEmail(email)){
    			 if(!username.equals("")){
    				 changeStatus();
    			 }
    			 else{
    				 et_uname.setBackgroundResource(R.drawable.rounded_text_nofield);
    	        		Toast.makeText(EditUserNameActivity.this, getResources().getString(R.string.txt_please_enter_username),
    	        				Toast.LENGTH_SHORT).show();
    			 }
    		}
    		else{
    			et_email.setBackgroundResource(R.drawable.rounded_text_nofield);
        		Toast.makeText(EditUserNameActivity.this, getResources().getString(R.string.txt_enter_valid_email),
        				Toast.LENGTH_SHORT).show();
    		}
    	}
    	else{
    		et_status.setBackgroundResource(R.drawable.rounded_text_nofield);
    		Toast.makeText(EditUserNameActivity.this, getResources().getString(R.string.txt_enter_status),
    				Toast.LENGTH_SHORT).show();
    				
    	}
    }
    
    public void changeStatus(){
    	try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token", appInstance.getUserCred().getToken());
			loginObj.put("password",  appInstance.getUserCred().getPassword());
			loginObj.put("language", appInstance.getUserCred().getLanguage());
			loginObj.put("firstname", username);
			loginObj.put("lastname", appInstance.getUserCred().getLastname());
			loginObj.put("gender", appInstance.getUserCred().getGender());
			loginObj.put("live_in", appInstance.getUserCred().getLive_in());
			loginObj.put("age", appInstance.getUserCred().getAge());
			loginObj.put("profession", appInstance.getUserCred().getProfession());
			loginObj.put("seating_pref", appInstance.getUserCred().getSeating_pref());
			loginObj.put("some_about_you", appInstance.getUserCred().getSomethinAbout());
			loginObj.put("status", status);
			loginObj.put("image_name", "");
			loginObj.put("image_type", "");
			loginObj.put("image_content", "");
			AsyncaTaskApiCall logoutcall=new AsyncaTaskApiCall(appInstance, lisenar, loginObj.toString(),EditUserNameActivity.this,
					"reg");
//			AsyncaTaskApiCall logoutcall=new AsyncaTaskApiCall(appInstance,EditUserNameActivity.this, loginObj.toString(),
//					this, "reg");
			logoutcall.execute();
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void SuccessUpdateProfile(){
    	userCred.setStatus(status);
    	userCred.setFirstname(username);
    	appInstance.setUserCred(userCred);
    	finish();
    }
    
}