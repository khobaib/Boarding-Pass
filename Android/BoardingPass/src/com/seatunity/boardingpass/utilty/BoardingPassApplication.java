package com.seatunity.boardingpass.utilty;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.UserCred;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class BoardingPassApplication extends Application {
	private static Context context;
	protected SharedPreferences User;
	

	@Override
	public void onCreate() {
		super.onCreate();
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true).cacheOnDisc(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).defaultDisplayImageOptions(
						defaultOptions).build();
		ImageLoader.getInstance().init(config);

		context = getApplicationContext();
		User = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static Context getAppContext() {
		return context;        
	}
	public void setFirstTime(Boolean firstTimeFlag){
//		Editor editor = User.edit();
//		editor.putBoolean(Constants.FIRST_TIME, firstTimeFlag);
//		editor.commit();        
	}
	public void setRememberMe(Boolean rememberMeFlag){
		Editor editor = User.edit();
		editor.putBoolean(Constants.REMEMBER_ME, rememberMeFlag);
		editor.commit();        
	}
	public void setUserCred(UserCred userCred){
		Editor editor = User.edit();
		editor.putString(Constants.EMAIL, userCred.getEmail());
		editor.putString(Constants.PASSWORD, userCred.getPassword());
		editor.putString(Constants.TOKEN, userCred.getToken());
		editor.putString(Constants.LANGUAGE, userCred.getLanguage());
		editor.putString(Constants.FIRSTNAME, userCred.getFirstname());
		editor.putString(Constants.LASTNAME, userCred.getLastname());
		editor.putString(Constants.GENDER, userCred.getGender());
		editor.putString(Constants.LIVE_IN, userCred.getLive_in());
		editor.putString(Constants.AGE, userCred.getAge());
		editor.putString(Constants.PROFESSION, userCred.getProfession());
		editor.putString(Constants.SEATING, userCred.getSeating_pref());
		editor.putString(Constants.IMAGE_URL, userCred.getImage_url());
		editor.putString(Constants.SOME_THING_ABOUT_YOU, userCred.getSomethinAbout());
		editor.putString(Constants.STATUS, userCred.getStatus());
		editor.commit();
	}
	public UserCred getUserCred(){
		String email =User.getString(Constants.EMAIL, "");
		String password=User.getString(Constants.PASSWORD, "");
		String token =User.getString(Constants.TOKEN, "");
		String language=User.getString(Constants.LANGUAGE, "");
		String firstname=User.getString(Constants.FIRSTNAME, "");
		String lastname=User.getString(Constants.LASTNAME, "");
		String gender=User.getString(Constants.GENDER, "");
		String live_in=User.getString(Constants.LIVE_IN, "");
		String age=User.getString(Constants.AGE, "");
		String profession=User.getString(Constants.PROFESSION, "");
		String seating_pref=User.getString(Constants.SEATING, "");
		String image_url=User.getString(Constants.IMAGE_URL, "");
		String som_thing_about_you =User.getString(Constants.SOME_THING_ABOUT_YOU, "");
		String status=User.getString(Constants.STATUS, "");
		UserCred userCred = new UserCred(email,password,token, language, firstname, lastname, gender, live_in, age, 
				profession,seating_pref, image_url,som_thing_about_you,status);
		return userCred;
	}
//	public boolean isFirstTime(){
//		Boolean firstTimeFlag = User.getBoolean(Constants.FIRST_TIME, true);
//		return firstTimeFlag;
//	}
	public boolean isRememberMe(){
		Boolean rememberMeFlag = User.getBoolean(Constants.REMEMBER_ME, false);
		return rememberMeFlag;
	}
}
