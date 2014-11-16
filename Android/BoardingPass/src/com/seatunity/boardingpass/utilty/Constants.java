package com.seatunity.boardingpass.utilty;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.seatunity.boardingpass.fragment.HomeListFragment;

@TargetApi(Build.VERSION_CODES.KITKAT)
@SuppressLint({ "NewApi", "DefaultLocale" })
public class Constants {

	// StringBuilder sb = new StringBuilder(inputString);
	// It has the method deleteCharAt(), along with many other mutator methods.
	//
	// Just delete the characters that you need to delete and then get the
	// result as follows:
	//
	// String resultString = sb.toString();

	public static String removeingprecingZero(String seat_no) {
		String seatno = seat_no;
		seatno = seatno.replaceFirst("^0+(?!$)", "");
		return seatno;
	}

	public static boolean isBPassDateInFuture(String bpassJulianDate) {
		Calendar cal = Calendar.getInstance();
		int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		int year = cal.get(Calendar.YEAR);
		boolean isLeapYear = ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
		Log.i("isBPassDateInFuture", "Current day of the year: " + dayOfYear);
		int bpDate = -1;
		try {
			bpDate = Integer.parseInt(bpassJulianDate);
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			return false;
		}
		int totalDayInThisYear = isLeapYear ? 366 : 365;
		if (dayOfYear > (totalDayInThisYear - 7))
			dayOfYear -= 365;
		if (dayOfYear <= bpDate && bpDate <= (dayOfYear + 7))
			return true;
		return false;
	}

	/**
	 * @param context
	 * @param uri
	 * @return the parsed absolute-path of the {@link Uri}.
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("NewApi")
	public static String getPath(Context context, Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * @param context
	 * @param uri
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	public static ImageView IMG_PROF_PIC;
	public static String SAVE_PRO_PIC = "SAVE_PRO_PIC";
	public static HomeListFragment parent;
	public static int SELECTEDBOARDINGPASSPOSITION = 0;
	public static String[] okFileExtensions = new String[] { "jpg", "png", "gif", "jpeg" };
	public static boolean LIVE_IN_FLAG = false;
	public static boolean AGE_FLAG = false;
	public static boolean GENDER_FLAG = false;
	public static boolean POFESSION_FLAG = false;
	public static boolean SEATING_PREF_FLAG = false;
	public static boolean SOME_ABOUT_FLAG = false;
	public static boolean CHANGE_PHOTO_FLAG = false;

	/**
	 * Method to set all the flags of th app to {@code false}.
	 */
	public static void setAllFlagFalse() {
		LIVE_IN_FLAG = false;
		AGE_FLAG = false;
		GENDER_FLAG = false;
		POFESSION_FLAG = false;
		SEATING_PREF_FLAG = false;
		SOME_ABOUT_FLAG = false;
		CHANGE_PHOTO_FLAG = false;
	}

	/**
	 * @param file
	 *            name of the file.
	 * @return true if the file contains a {@code jpg, png, gif or jpeg }
	 *         extension, <br>
	 *         false otherwise.
	 */
	public static boolean isImage(String file) {
		for (String extension : okFileExtensions) {
			if (file.toLowerCase().endsWith(extension)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param file
	 *            name of the file.
	 * @return true if the file contains a {@code *.pdf } extension, <br>
	 *         false otherwise.
	 */
	public static boolean isPdf(String file) {
		if (file.toLowerCase().endsWith(".pdf")) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * @param file
	 *            name of the file.
	 * @return true if the file contains a {@code *.pkpass } extension, <br>
	 *         false otherwise.
	 */
	public static boolean isPkPass(String file) {
		if (file.toLowerCase().endsWith(".pkpass")) {
			return true;
		} else {
			return false;
		}

	}

	public static String DOMAIN_NAME = "http://seatunity.net";
	public static Bitmap photo = null;
	public static int SELECTEDPOSITION = 0;

	/**
	 * @param dayOfYear
	 *            in between 1 & 365 (366 for a leap year)
	 * @return a string formatted with
	 *         {@code calendar.get(Calendar.DAY_OF_MONTH)+":" +getMonth(i) },
	 *         where {@code calender} is a gregorian {@link Calendar} with its
	 *         day no. of year (in between 1~365) set as the passed parameter
	 */
	public static String getDayandYear(int dayOfYear) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
		// int year = 2004;
		// calendar.set(Calendar.YEAR, year);
		int i = calendar.get(Calendar.MONTH);
		return calendar.get(Calendar.DAY_OF_MONTH) + ":" + getMonth(i);

	}

	/**
	 * @param month
	 * @return the textual form of the month no.
	 */
	public static String getMonth(int month) {
		String mon = new DateFormatSymbols().getMonths()[month];
		mon = mon.substring(0, 3);
		mon = mon.toUpperCase();
		return mon;
	}

	/**
	 * @param year
	 *            to check
	 * @return {@code true} if the year is a leap year, false otherwise.
	 */
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

	public static String REMEMBER_ME = "remember_me";
	public static boolean pushnotificationcalllive = false;

	public static String type;

	public static boolean topicwritesuccess = false;
	public static boolean IMAGEPAGECALLED = false;
	public static boolean STATECALLPDFORMENU = false;
	public static int GOTABFROMWRITETOPIC;
	public static boolean GOARTCLEPAGEFROMMEMBER = false;

	public static boolean GOARTCLEPAGE = false;
	public static int from;

	public static String INTER_ARTICLE_ID;
	public static String INTER_MEMBER_ID;

	public static boolean GOMEMBERSTATEFROMINTERACTION = false;
	public static boolean GOMEMBERSTATEFROMSETTING = false;

	public static boolean MESSAGESETTINGSTATE = false;
	public static boolean catgeory = false;
	public static String caturl = "-1";
	public static String caname = "";

	public static boolean writetopicsuccess = false;
	public static String drectory;
	public static String photofromcamera;
	public static int notificationcount;
	public static String userid = "8150";
	public static String baseurl = "http://seatunity.net/";

	/**
	 * @param context
	 *            : Working context
	 * @return true : if any net-connection is on (but not necessarily active), <br>
	 *         false: otherwise
	 */
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	/**
	 * @param target
	 *            : The email to be verified
	 * @return true : if the email is well-formatted <br>
	 *         false: otherwise
	 */
	public static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}

	public static boolean namecheck(String name) {

		if (((name.length() > 10) || (name.length() < 3))) {
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
	public static final String DISPLAY_MESSAGE_ACTION = "com.sparkzi.DISPLAY_MESSAGE";
	public static final String EXTRA_MESSAGE = "message";
	public static final File APP_DIRECTORY = new File(Environment.getExternalStorageDirectory(), "BoardingPass");
	public static final String FROM_ACTIVITY = "from_activity";
	public static final int PARENT_ACTIVITY_LOGIN = 101;
	public static final int PARENT_ACTIVITY_PROFILE = 102;
	public static final int RETRIEVE_ALL_CONVERSATIONS = 201;
	public static final int RETRIEVE_SPECIFIC_USER_CONVERSATIONS = 202;

}
