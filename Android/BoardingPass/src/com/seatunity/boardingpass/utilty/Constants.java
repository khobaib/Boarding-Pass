package com.seatunity.boardingpass.utilty;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.R.array;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class Constants {
	public static String[] okFileExtensions =  new String[] {"jpg", "png", "gif","jpeg"};
	public static boolean LIVE_IN_FLAG=false;
	public static boolean AGE_FLAG=false;
	public static boolean GENDER_FLAG=false;
	public static boolean POFESSION_FLAG=false;
	public static boolean SEATING_PREF_FLAG=false;
	public static boolean SOME_ABOUT_FLAG=false;
	public static boolean CHANGE_PHOTO_FLAG=false;


	public static void setAllFlagFalse(){
		LIVE_IN_FLAG=false;
		AGE_FLAG=false;
		GENDER_FLAG=false;
		POFESSION_FLAG=false;
		SEATING_PREF_FLAG=false;
		SOME_ABOUT_FLAG=false;
		CHANGE_PHOTO_FLAG=false;
	}

	public static boolean isImage(String file){
		for (String extension : okFileExtensions)
		{
			if (file.toLowerCase().endsWith(extension))
			{ 
				return true;
			}
		}
		return false;       
	}
	public static boolean isPdf(String file){

		if (file.toLowerCase().endsWith(".pdf"))
		{ 
			return true;
		}
		else{
			return false;
		}

	}
	public static boolean isPkPass(String file){

		if (file.toLowerCase().endsWith(".pkpass"))
		{ 
			return true;
		}
		else{
			return false;
		}

	}

	public static String DOMAIN_NAME="http://seatunity.net";
	public static Bitmap photo=null;
	public static int SELECTEDPOSITION=0;
	public static String getDayandYear(int dayOfYear){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
		//		    int year = 2004;
		//		    calendar.set(Calendar.YEAR, year);
		int i=calendar.get(Calendar.MONTH);
		return calendar.get(Calendar.DAY_OF_MONTH)+":" +getMonth(i);

	}



	public static String getMonth(int month) {
		String mon= new DateFormatSymbols().getMonths()[month];
		mon=mon.substring(0,3);
		mon=mon.toUpperCase();
		return mon;
	}
	public static boolean isLeapYear(int year) {

		if (year % 4 != 0) {
			return false;
		} else if (year % 400 == 0) {
			return true;
		} else if (year % 100 == 0) {
			return false;
		} else {
			return true;
		}
	}

	public static String REMEMBER_ME="remember_me";
	public static boolean pushnotificationcalllive=false;

	public static String type;

	public static boolean topicwritesuccess=false;
	public static boolean IMAGEPAGECALLED=false; 
	public static boolean STATECALLPDFORMENU=false;
	public static int GOTABFROMWRITETOPIC;
	public static boolean GOARTCLEPAGEFROMMEMBER=false; 

	public static boolean GOARTCLEPAGE=false; 
	public static int from;

	public static String INTER_ARTICLE_ID;
	public static String INTER_MEMBER_ID;

	public static boolean GOMEMBERSTATEFROMINTERACTION=false; 
	public static boolean GOMEMBERSTATEFROMSETTING=false; 

	public static boolean MESSAGESETTINGSTATE=false;
	public static boolean catgeory=false;
	public static String caturl="-1";
	public static String caname=""; 

	public static boolean writetopicsuccess=false;
	public static String drectory;
	public static String photofromcamera;
	public static int notificationcount;
	public static String userid="8150";
	public static String  baseurl="http://seatunity.net/";
	public static boolean isOnline(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public  static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}


	public static  boolean namecheck(String name){

		if(((name.length()>10)||(name.length()<3))){
			return false;
		}
		Pattern p = Pattern.compile("[A-Za-z]+([_|A-Za-z|0-9])*");
		return p.matcher(name).matches();
	}
	public static final int REQUEST_TYPE_GET = 1;
	public static final int REQUEST_TYPE_POST = 2;
	public static final int REQUEST_TYPE_PUT = 3;
	public static final int REQUEST_TYPE_DELETE = 4;


	public static final String TOKEN = "token";
	public static final String LANGUAGE = "language";
	public static final String FIRSTNAME = "firstname";
	public static final String LASTNAME = "lastname";
	public static final String GENDER = "gender";
	public static final String LIVE_IN = "live_in";
	public static final String AGE = "age";
	public static final String PROFESSION = "profession";
	public static final String SEATING = "seating_pref";
	public static final String IMAGE_URL = "image_url";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	public static final String SOME_THING_ABOUT_YOU = "something_about_you";
	public static final String STATUS = "status";


	public static final int ESSENTIAL_EDUCATION_INDEX = 0;
	public static final int ESSENTIAL_ETHNICITY_INDEX = 1;
	public static final int ESSENTIAL_DIET_INDEX = 2;
	public static final int ESSENTIAL_DRINKS_INDEX = 3;
	public static final int ESSENTIAL_SMOKES_INDEX = 4;
	public static final int ESSENTIAL_RELIGION_INDEX = 5;
	public static final int ESSENTIAL_KIDS_INDEX = 6;
	public static final int ESSENTIAL_POLITICS_INDEX = 7;
	public static final int ESSENTIAL_SIGN_INDEX = 8;
	public static final int ESSENTIAL_PROFESSION_INDEX = 9;
	public static final int ESSENTIAL_HOMETOWN_INDEX = 10;
	public static final int ESSENTIAL_LANGUAGES_INDEX = 11;
	public static final int ESSENTIAL_STATIC_FIELD_COUNT = 9;
	public static final int TEMPLATE_ID_SOMETHING_ELSE = 8;
	public static final int FAVORITE_STATUS_STRANGER = 1;
	public static final int FAVORITE_STATUS_FRIEND = 2;
	public static final int FAVORITE_STATUS_SENT = 3;
	public static final int FAVORITE_STATUS_WAITING = 4;
	public static final int RESPONSE_STATUS_CODE_SUCCESS = 200;
	public static final String DISPLAY_MESSAGE_ACTION ="com.sparkzi.DISPLAY_MESSAGE";
	public static final String EXTRA_MESSAGE = "message";
	public static final File APP_DIRECTORY =
			new File(Environment.getExternalStorageDirectory(),"BoardingPass");
	public static final String FROM_ACTIVITY = "from_activity";
	public static final int PARENT_ACTIVITY_LOGIN = 101;
	public static final int PARENT_ACTIVITY_PROFILE = 102;
	public static final int RETRIEVE_ALL_CONVERSATIONS = 201;
	public static final int RETRIEVE_SPECIFIC_USER_CONVERSATIONS = 202;

}
