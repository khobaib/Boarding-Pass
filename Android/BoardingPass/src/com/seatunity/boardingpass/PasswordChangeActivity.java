package com.seatunity.boardingpass;
import java.util.ArrayList;
import java.util.Calendar;
import org.json.JSONException;
import org.json.JSONObject;
import com.bugsense.trace.BugSenseHandler;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.fragment.FragmentMyAccount;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
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
import android.content.res.Resources.NotFoundException;
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
public class PasswordChangeActivity extends Activity implements CallBackApiCall{
	EditText et_enter_old_pass,et_enter_new_pass,et_confirm_new_pass;
	String oldPassword,newPassword,confirmPassword;
	BoardingPassApplication appInstance;
	UserCred userCred;
	Drawable back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        BugSenseHandler.initAndStartSession(PasswordChangeActivity.this, "2b60c090");
        appInstance =(BoardingPassApplication)getApplication();
        userCred=appInstance.getUserCred();
        setContentView(R.layout.change_password);
        et_enter_old_pass=(EditText) findViewById(R.id.et_enter_old_pass);
        et_enter_new_pass=(EditText) findViewById(R.id.et_enter_new_pass);
        et_confirm_new_pass=(EditText) findViewById(R.id.et_confirm_new_pass);
        back=et_confirm_new_pass.getBackground();
        editTextControl();
    }
    @Override
    protected void onStop() {
        super.onStop();
        BugSenseHandler.closeSession(PasswordChangeActivity.this);
    }
    public void editTextControl(){
    	et_confirm_new_pass.addTextChangedListener(new TextWatcher() {          
             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
            	 et_confirm_new_pass.setBackgroundDrawable(back);
 				


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
    	 et_enter_old_pass.addTextChangedListener(new TextWatcher() {          
             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
             	et_enter_old_pass.setBackgroundDrawable(back);
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
    	 et_enter_new_pass.addTextChangedListener(new TextWatcher() {          
             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
            	 et_enter_new_pass.setBackgroundDrawable(back);
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
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void Cancel(View view){
    	finish();
    }
    public void ChangePassword(View view){
    	oldPassword=et_enter_old_pass.getText().toString();
    	newPassword=et_enter_new_pass.getText().toString();
    	confirmPassword=et_confirm_new_pass.getText().toString();
    	if(appInstance.getUserCred().getPassword().equals(oldPassword)){
    		if(!newPassword.equals("")){
    			 if(newPassword.equals(confirmPassword)){
    				 changePassword();
    			 }
    			 else{
    				 et_confirm_new_pass.setBackgroundResource(R.drawable.rounded_text_nofield);
    	        		Toast.makeText(PasswordChangeActivity.this, getResources().getString(R.string.txt_confirm_new_pass),
    	        				Toast.LENGTH_SHORT).show();
    			 }
    		}
    		else{
    			et_enter_new_pass.setBackgroundResource(R.drawable.rounded_text_nofield);
        		Toast.makeText(PasswordChangeActivity.this, getResources().getString(R.string.txt_enter_new_pass),
        				Toast.LENGTH_SHORT).show();
    		}
    	}
    	else{
    		et_enter_old_pass.setBackgroundResource(R.drawable.rounded_text_nofield);
    		Toast.makeText(PasswordChangeActivity.this, getResources().getString(R.string.txt_enter_old_pass),
    				Toast.LENGTH_SHORT).show();
    				
    	}
    }
    
    public void changePassword(){
    	try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token", appInstance.getUserCred().getToken());
			loginObj.put("password", newPassword);
			loginObj.put("language", appInstance.getUserCred().getLanguage());
			loginObj.put("firstname", appInstance.getUserCred().getFirstname());
			loginObj.put("lastname", appInstance.getUserCred().getLastname());
			loginObj.put("gender", appInstance.getUserCred().getGender());
			loginObj.put("live_in", appInstance.getUserCred().getLive_in());
			loginObj.put("age", appInstance.getUserCred().getAge());
			loginObj.put("profession", appInstance.getUserCred().getProfession());
			loginObj.put("seating_pref", appInstance.getUserCred().getSeating_pref());
			loginObj.put("some_about_you", appInstance.getUserCred().getSomethinAbout());
			loginObj.put("status", appInstance.getUserCred().getStatus());
			loginObj.put("image_name", "");
			loginObj.put("image_type", "");
			loginObj.put("image_content", "");
			AsyncaTaskApiCall change_pass =new AsyncaTaskApiCall(PasswordChangeActivity.this, loginObj.toString(), PasswordChangeActivity.this,
					"reg",Constants.REQUEST_TYPE_PUT);
			change_pass.execute();
	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 
	@Override
	public void responseOk(JSONObject job) {
		// TODO Auto-generated method stub
		try {
			if(job.getString("success").equals("true")){
				Toast.makeText(PasswordChangeActivity.this,getResources().getString(R.string.txt_update_success),
						Toast.LENGTH_SHORT).show();
				userCred.setPassword(newPassword);
		    	appInstance.setUserCred(userCred);
		    	finish();
				
			}
			else{


			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void responseFailure(JSONObject job) {
		
		try {
			JSONObject joberror=new JSONObject(job.getString("error"));
			String code =joberror.getString("code");
			if(code.equals("x05")){
//				String message=joberror.getString("message");
//				Toast.makeText(EditUserNameActivity.this, message,Toast.LENGTH_SHORT).show();
				JSONObject loginObj = new JSONObject();
				loginObj.put("email", appInstance.getUserCred().getEmail());
				loginObj.put("password", appInstance.getUserCred().getPassword());
				String loginData = loginObj.toString();
				AsyncaTaskApiCall log_in_lisenar =new AsyncaTaskApiCall(PasswordChangeActivity.this, loginData, 
						PasswordChangeActivity.this,"login",Constants.REQUEST_TYPE_POST,true);
				log_in_lisenar.execute();

			}
			else{
				String message=joberror.getString("message");
				Toast.makeText(PasswordChangeActivity.this, getResources().getString(R.string.txt_update_failed),
						Toast.LENGTH_SHORT).show();
			}
			
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void saveLoginCred(JSONObject job) {
		// TODO Auto-generated method stub
		try {
			String status=job.getString("success");
			if(status.equals("true")){
				userCred=userCred.parseUserCred(job);
				userCred.setEmail(appInstance.getUserCred().getEmail());
				userCred.setPassword(appInstance.getUserCred().getPassword());
				Log.e("tagged before",  appInstance.getUserCred().getToken());
				appInstance.setUserCred(userCred);
				Log.e("tagged after",  appInstance.getUserCred().getToken());
				Log.e("tagged actuual", userCred.getToken());
				appInstance.setRememberMe(true);
				JSONObject loginObj = new JSONObject();
				loginObj.put("token", appInstance.getUserCred().getToken());
				loginObj.put("password", newPassword);
				loginObj.put("language", appInstance.getUserCred().getLanguage());
				loginObj.put("firstname", appInstance.getUserCred().getFirstname());
				loginObj.put("lastname", appInstance.getUserCred().getLastname());
				loginObj.put("gender", appInstance.getUserCred().getGender());
				loginObj.put("live_in", appInstance.getUserCred().getLive_in());
				loginObj.put("age", appInstance.getUserCred().getAge());
				loginObj.put("profession", appInstance.getUserCred().getProfession());
				loginObj.put("seating_pref", appInstance.getUserCred().getSeating_pref());
				loginObj.put("some_about_you", appInstance.getUserCred().getSomethinAbout());
				loginObj.put("status", appInstance.getUserCred().getStatus());
				loginObj.put("image_name", "");
				loginObj.put("image_type", "");
				loginObj.put("image_content", "");
				AsyncaTaskApiCall edit_uname =new AsyncaTaskApiCall(PasswordChangeActivity.this,loginObj.toString() , 
						PasswordChangeActivity.this,	"reg",Constants.REQUEST_TYPE_PUT);
				edit_uname.execute();
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void LoginFailed(JSONObject job) {
		// TODO Auto-generated method stub
		try {
			JSONObject joberror=new JSONObject(job.getString("error"));
			String code =joberror.getString("code");
			String message=joberror.getString("message");
			Toast.makeText(PasswordChangeActivity.this, message,
						Toast.LENGTH_SHORT).show();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
