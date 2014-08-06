
package com.seatunity.boardingpass.fragment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.seatunity.boardingpass.EditUserNameActivity;
import com.seatunity.boardingpass.HomeActivity;
import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.PasswordChangeActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.UploadPicActivity;
import com.seatunity.boardingpass.adapter.AdapterForSettings;
import com.seatunity.boardingpass.adapter.NothingSelectedSpinnerAdapter;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.Base64;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.boardingpass.utilty.PkpassReader;
import com.seatunity.model.ImageScale;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;
import com.touhiDroid.filepicker.FilePickerActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class FragmentMyAccount extends Fragment implements CallBackApiCall{
	ImageView img_edit,img_prof_pic,img_status;
	ArrayList<String>setting_criteria;
	BoardingPassApplication appInstance;
	AdapterForSettings adapter;
	ListView lv_setting;
	Spinner spn_country;
	AccountListFragment parent;
	Activity activity;
	TextView tv_uname,tv_email,tv_stataus,tv_statau;
	UserCred userCred;
	int ACTION_REQUEST_CAMERA=31;
	int ACTION_REQUEST_GALLERY=21;
	int ACTION_REQUEST_GALLERY_KITKAT=22;
	String drectory;
	String photofromcamera;
	Context context;
	String contentbodyremeber="";
	ViewGroup v ;
	Bitmap photo;
	int callfrom=0;
	JSONObject loginObj ;
	private Bundle savedState = null;
	public FragmentMyAccount(){
	}
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//	Log.e("inside", "onCreate");
		if(getActivity()!=null){
			context=getActivity();
			activity=getActivity();
		}
		appInstance =(BoardingPassApplication) getActivity().getApplication();
		userCred=appInstance.getUserCred();
		setting_criteria=new ArrayList<String>();
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_live_in));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_age));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_gender));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_prof));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_seatting_pref));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_som_about_you));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_change_pass));
		setting_criteria.add(getActivity().getResources().getString(R.string.acc_sign_out));
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if(getActivity()!=null){
			context=getActivity();
			activity=getActivity();
		}
	}
	public void setView(){
		tv_uname.setText(appInstance.getUserCred().getFirstname());
		tv_email.setText(appInstance.getUserCred().getEmail());
		tv_stataus.setText(appInstance.getUserCred().getStatus());
	}
	public void uploadProfileImage(Bitmap bitmap){
		try {
			loginObj = new JSONObject();
			loginObj.put("token", appInstance.getUserCred().getToken());
			loginObj.put("password", appInstance.getUserCred().getPassword());
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
			loginObj.put("image_name", appInstance.getUserCred().getLastname()+System.currentTimeMillis()+".png");
			loginObj.put("image_type", "image/png");
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG,100, bao);
			byte[] ba = bao.toByteArray();
			String base64Str = Base64.encodeBytes(ba);
			loginObj.put("image_content", base64Str);
			callfrom=2;
			Constants.CHANGE_PHOTO_FLAG=true;
			AsyncaTaskApiCall update_prof_lisenar =new AsyncaTaskApiCall(FragmentMyAccount.this, loginObj.toString(),context,
					"reg_update",Constants.REQUEST_TYPE_POST);
			update_prof_lisenar.execute();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onResume() {

		// TODO Auto-generated method stub
		super.onResume();


	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		v = (ViewGroup) inflater.inflate(R.layout.fragment_my_account,
				container, false);
		tv_uname=(TextView) v.findViewById(R.id.tv_uname);
		tv_email=(TextView) v.findViewById(R.id.tv_email);
		tv_stataus=(TextView) v.findViewById(R.id.tv_stataus);
		img_prof_pic=(ImageView) v.findViewById(R.id.img_prof_pic);
		lv_setting=(ListView) v.findViewById(R.id.lv_setting);
		img_status=(ImageView) v.findViewById(R.id.img_status);
		tv_stataus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ShowStatus();
			}
		});
		img_status.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowStatus();
			}
		});
		img_edit=(ImageView) v.findViewById(R.id.img_edit);
		spn_country=(Spinner) v.findViewById(R.id.spn_country);

		img_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(context, EditUserNameActivity.class);
				startActivity(intent);
			}
		});
		img_prof_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String[] photochooser = context.getResources().getStringArray(R.array.upload_photo_from); 
				showDialogTochosePhoto(photochooser,context.getResources().getString(R.string.txt_select_photo));

			}
		});
		setlistView();
		Log.e("onResume", "onResume test ");
		Log.e("Image Url", "" +this.img_prof_pic);

		appInstance =(BoardingPassApplication) getActivity().getApplication();
		userCred=appInstance.getUserCred();

		if((appInstance.getUserCred().getImage_url().equals(""))||(appInstance.getUserCred().getImage_url()==null)){

		}
		else{
			ImageLoader.getInstance().displayImage(appInstance.getUserCred().getImage_url()
					, img_prof_pic);
		}
		Constants.IMG_PROF_PIC=img_prof_pic;
		setView();



		return v;
	}



	public void setlistView(){
		userCred=appInstance.getUserCred();
		adapter=new AdapterForSettings(context, setting_criteria,userCred);
		lv_setting.setAdapter(adapter);
		lv_setting.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position==0){
					String[] country_list =context.getResources().getStringArray(R.array.countries_array); 
					showDialogForGender(country_list,context.getResources().getString(R.string.txt_select_country),position);
				}
				if(position==1){
					String[] age_list =context.getResources().getStringArray(R.array.age_range_list); 
					showDialogForGender(age_list,context.getResources().getString(R.string.txt_select_age),position);
				}
				else if(position==2){
					String[] gender_list = context.getResources().getStringArray(R.array.gender); 
					showDialogForGender(gender_list,context.getResources().getString(R.string.txt_gender),position);
				}
				else if(position==3){
					Constants.POFESSION_FLAG=true;
					setSomethingAbou(context.getResources().getString(R.string.txt_edit_profesion)
							,context.getResources().getString(R.string.acc_prof),position);
				}
				else if(position==4){
					String[] seating_pref_list =context.getResources().getStringArray(R.array.seating_pref); 
					String title=context.getResources().getString(R.string.txt_seatting_pref_cap);
					showDialogForGender(seating_pref_list,title,position);
				}
				else if(position==5){
					Constants.SOME_ABOUT_FLAG=true;
					setSomethingAbou(context.getResources().getString(R.string.txt_sm_about)
							,context.getResources().getString(R.string.txt_say_sm_about),position);
				}
				else if(position==6){
					Intent intent= new Intent(context, PasswordChangeActivity.class);
					startActivity(intent);
				}
				else if(position==7){
					Signout();
				}
			}
		});
	}
	public void ShowStatus(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		TextView title=(TextView) customTitleView.findViewById(R.id.tv_title);
		title.setText(context.getResources().getString(R.string.txt_status));
		builder.setCustomTitle(customTitleView);
		builder.setMessage(appInstance.getUserCred().getStatus())
		.setCancelable(false)
		.setPositiveButton(context.getResources().getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();

			}
		})
		.setTitle(context.getResources().getString(R.string.acc_sign_out))
		.setIcon(R.drawable.ic_sing_out);
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void Signout(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		ImageView im_icon=(ImageView) customTitleView.findViewById(R.id.im_icon);
		TextView tvtitle=(TextView) customTitleView.findViewById(R.id.tv_title);
		tvtitle.setText(context.getResources().getString(R.string.acc_sign_out));
		im_icon.setVisibility(View.VISIBLE);
		im_icon.setImageResource(R.drawable.ic_sing_out);
		builder.setCustomTitle(customTitleView);
		builder.setMessage(context.getResources().getString(R.string.txt_sign_out_msz))
		.setCancelable(false)
		.setPositiveButton(context.getResources().getString(R.string.txt_confirm), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				try {
					loginObj = new JSONObject();
					loginObj.put("token", appInstance.getUserCred().getToken());
					callfrom=1;
					AsyncaTaskApiCall log_in_lisenar =new AsyncaTaskApiCall(FragmentMyAccount.this, loginObj.toString(), context,
							"logout",Constants.REQUEST_TYPE_POST);
					log_in_lisenar.execute();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		})
		.setNegativeButton(context.getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		})
		.setTitle(context.getResources().getString(R.string.acc_sign_out))
		.setIcon(R.drawable.ic_sing_out);
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void showDialogTochosePhoto(final CharSequence[] items,String title)
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		TextView tvtitle=(TextView) customTitleView.findViewById(R.id.tv_title);
		tvtitle.setText(title);
		builder.setCustomTitle(customTitleView);

		builder.setPositiveButton(context.getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		builder.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				if(which==0){
					createfolder();
					photofromcamera=System.currentTimeMillis()+".jpg";
					Constants.drectory=drectory;
					Constants.photofromcamera=photofromcamera;
					final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(drectory, photofromcamera);
					Log.e("pos", drectory+photofromcamera+" " +f.exists());
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, ACTION_REQUEST_CAMERA);
					Constants.SELECTEDBOARDINGPASSPOSITION=1;
				}
				else if(which==1){

					if (Build.VERSION.SDK_INT <19){
						Intent intent = new Intent(
								Intent.ACTION_GET_CONTENT);
						intent.setType("image/*");

						Intent chooser = Intent
								.createChooser(
										intent,
										"Choose a Picture");
						startActivityForResult(
								chooser,
								ACTION_REQUEST_GALLERY);
					} else {
						
						Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						intent.setType("image/jpeg");
						startActivityForResult(intent, ACTION_REQUEST_GALLERY_KITKAT);
					}
					Constants.SELECTEDBOARDINGPASSPOSITION=1;
				}
			}
		});
		builder.show();

	}

	public void showDialogForGender(final CharSequence[] items,String title,final int postion)
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		TextView tvtitle=(TextView) customTitleView.findViewById(R.id.tv_title);
		tvtitle.setText(title);
		builder.setCustomTitle(customTitleView);
		builder.setPositiveButton(context.getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				Constants.setAllFlagFalse();
			}
		});
		builder.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				try {
					loginObj = new JSONObject();
					loginObj.put("token", appInstance.getUserCred().getToken());
					loginObj.put("password", appInstance.getUserCred().getPassword());
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
					if(postion==0){
						String country=items[which].toString();
						loginObj.put("live_in", country);
						userCred.setLive_in(country);
						Constants.LIVE_IN_FLAG=true;
					}
					else if(postion==1){
						String age=items[which].toString();
						loginObj.put("age", age);
						userCred.setAge(age);
						Constants.AGE_FLAG=true;

					}
					else if(postion==2){
						String gender=items[which].toString();
						loginObj.put("gender", gender);
						userCred.setGender(gender);
						Constants.GENDER_FLAG=true;

					}
					else if(postion==4){
						String seat_pref=items[which].toString();
						loginObj.put("seating_pref", seat_pref);
						userCred.setSeating_pref(seat_pref);
						Constants.SEATING_PREF_FLAG=true;

					}
					callfrom=2;
					AsyncaTaskApiCall update_prof_lisenar =new AsyncaTaskApiCall(FragmentMyAccount.this, loginObj.toString(),context,
							"reg_update",Constants.REQUEST_TYPE_POST);
					update_prof_lisenar.execute();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		builder.show();

	}
	public void setSomethingAbou(String title,final String hint,final int position){
		final EditText input = new EditText(context);
		input.setHint(hint);
		final AlertDialog d = new AlertDialog.Builder(context)
		.setView(input)
		.setPositiveButton(context.getResources().getString(R.string.txt_ok), null) //Set to null. We override the onclick
		.setNegativeButton(context.getResources().getString(R.string.txt_cancel), null)
		.create();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		TextView tvtitle=(TextView) customTitleView.findViewById(R.id.tv_title);
		tvtitle.setText(title);
		d.setCustomTitle(customTitleView);
		d.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button ok = d.getButton(AlertDialog.BUTTON_POSITIVE);
				Button cancel = d.getButton(AlertDialog.BUTTON_NEGATIVE);
				cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						d.cancel();
						Constants.setAllFlagFalse();
					}
				});
				ok.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String value = input.getText().toString();

						if((value==null)||(value.equals(""))){
							Toast.makeText(context,context.getResources().getString(R.string.
									txt_please_enter)+" "+hint, Toast.LENGTH_SHORT).show();
						}
						else{
							d.cancel();
							try {
								JSONObject loginObj = new JSONObject();
								loginObj.put("token", appInstance.getUserCred().getToken());
								loginObj.put("password", appInstance.getUserCred().getPassword());
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
								if(position==3){
									loginObj.put("profession", value);
									userCred.setProfession(value);
								}
								else if(position==5){

									loginObj.put("some_about_you", value);
									userCred.setSomethinAbout(value);

								}
								callfrom=2;
								AsyncaTaskApiCall update_prof_lisenar =new AsyncaTaskApiCall(FragmentMyAccount.this, loginObj.toString(), context,
										"reg_update",Constants.REQUEST_TYPE_POST);
								update_prof_lisenar.execute();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
				});
			}
		});
		d.show();
	}

	public void showCustomDialogForPasswordChange(){
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.edit_user_name);
		dialog.show();
	}
	//08-03 12:38:52.628: E/context(3956): ab com.seatunity.boardingpass.MainActivity@418f8ce0  com.seatunity.boardingpass.db.SeatUnityDatabase$DatabaseHelper@418498e0
	@SuppressLint("NewApi")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if ((requestCode == ACTION_REQUEST_GALLERY)||(requestCode==ACTION_REQUEST_GALLERY_KITKAT)) {
			Uri selectedImageUri ;
			ImageScale scaleimage=new ImageScale();
			String tempPath ="";
			if(requestCode==ACTION_REQUEST_GALLERY_KITKAT){
				selectedImageUri = data.getData();
//				final int takeFlags = data.getFlags()
//						& (Intent.FLAG_GRANT_READ_URI_PERMISSION
//								| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//				getActivity().getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
//				
//				Log.e("KITKAT", "msg "+selectedImageUri.toString());
				tempPath =Constants.getPath(activity, selectedImageUri);


			}
			else{
				selectedImageUri = data.getData();
				tempPath =getPath(selectedImageUri,activity);
				Log.e("external", "msg "+selectedImageUri.toString());

			}
		
		File file=new File(tempPath);
		photo = scaleimage.decodeImagetoUpload(file.getAbsolutePath());
		file.delete();
		uploadProfileImage(photo);
	} 
	else if (requestCode ==ACTION_REQUEST_CAMERA) {
		try
		{
			Log.e("inside", "onActivityResultCamera");

			String filepath = drectory+"/"+photofromcamera;

			File file=new File(filepath);

			if(file.exists()){
				ImageScale scaleimage=new ImageScale();
				photo = scaleimage.decodeImagetoUpload(file.getAbsolutePath());
				file.delete();

				uploadProfileImage(photo);
			}

		}
		catch(Exception e)
		{
			Log.e("Could not save", e.toString());
		}
	}
}

public String getPath(Uri uri, Activity activity) {
	String[] projection = { MediaColumns.DATA };
	Cursor cursor = activity
			.managedQuery(uri, projection, null, null, null);
	int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	cursor.moveToFirst();
	return cursor.getString(column_index);
}
public void createfolder(){
	String newFolder = "/Lipberryfinal";
	String thumb="/Lipberrythumb";
	String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
	drectory= extStorageDirectory + newFolder;
	File myNewFolder = new File(drectory);
	myNewFolder.mkdir();
}
public static boolean deleteDirectory(File path) {
	if( path.exists() ) {
		File[] files = path.listFiles();
		if (files == null) {
			return true;
		}
		for(int i=0; i<files.length; i++) {
			if(files[i].isDirectory()) {
				deleteDirectory(files[i]);
			}
			else {
				files[i].delete();
			}
		}
	}
	return( path.delete() );
}
@Override
public void responseOk(JSONObject job) {
	// TODO Auto-generated method stub
	Log.e("afImageUrl", "" +Constants.IMG_PROF_PIC);
	try {
		if(job.get("success").equals("true")){
			Constants.setAllFlagFalse();
			if(callfrom==1){
				UserCred ucrCred=new UserCred("", "", "", "", "", "", "", "", "", "", "", "", "", "");
				activity.finish();
				appInstance.setUserCred(ucrCred);
				appInstance.setRememberMe(false);
				SeatUnityDatabase dbInstance = new SeatUnityDatabase(context);
				dbInstance.open();
				dbInstance.droptableBoardingPassDbManager();
				dbInstance.createtableBoardingPassDbManager();
				dbInstance.close();
				Toast.makeText(context, getResources().getString(R.string.txt_logout_success),
						Toast.LENGTH_SHORT).show();
			}
			else if(callfrom==2){
				String imageurl=job.getString("image_url");

				Toast.makeText(context,context.getResources().getString(R.string.txt_update_success),
						Toast.LENGTH_SHORT).show();
				if(!imageurl.equals("")){
					userCred.setImage_url(imageurl);
					appInstance.setUserCred(userCred);
					ImageLoader.getInstance().displayImage(appInstance.getUserCred().getImage_url(), img_prof_pic);
				}
				appInstance.setUserCred(userCred);
				setlistView();
				Constants.photo=null;
			}
			//this.img_prof_pic.setImageBitmap(photo);
			ImageLoader.getInstance().displayImage(appInstance.getUserCred().getImage_url(), Constants.IMG_PROF_PIC);

		}

	} catch (JSONException e) {

		e.printStackTrace();
	}

}
@Override
public void responseFailure(JSONObject job) {
	// TODO Auto-generated method stub
	try {
		JSONObject joberror=new JSONObject(job.getString("error"));
		String code =joberror.getString("code");
		if(code.equals("x05")){
			JSONObject loginObj = new JSONObject();
			loginObj.put("email", appInstance.getUserCred().getEmail());
			loginObj.put("password", appInstance.getUserCred().getPassword());
			String loginData = loginObj.toString();
			AsyncaTaskApiCall log_in_lisenar =new AsyncaTaskApiCall(FragmentMyAccount.this, loginData, 
					context,"login",Constants.REQUEST_TYPE_POST,true);
			log_in_lisenar.execute();

		}
		else{
			if(callfrom==1){

				Toast.makeText(context, getResources().getString(R.string.txt_logout_failed),
						Toast.LENGTH_SHORT).show();
			}
			else if(callfrom==2){
				Toast.makeText(context, getResources().getString(R.string.txt_update_failed),
						Toast.LENGTH_SHORT).show();

			}
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
		UserCred ucredFromServer;

		String status=job.getString("success");

		if(status.equals("true")){
			ucredFromServer=UserCred.parseUserCred(job);
			ucredFromServer.setEmail(appInstance.getUserCred().getEmail());
			ucredFromServer.setPassword(appInstance.getUserCred().getPassword());
			appInstance.setUserCred(ucredFromServer);
			appInstance.setRememberMe(true);
			Log.e("tagged email", "abc "+appInstance.getUserCred().getEmail());
			JSONObject loginObjnew = new JSONObject();
			loginObjnew.put("token", appInstance.getUserCred().getToken());
			loginObjnew.put("password", appInstance.getUserCred().getPassword());
			loginObjnew.put("language", appInstance.getUserCred().getLanguage());
			loginObjnew.put("firstname", appInstance.getUserCred().getFirstname());
			loginObjnew.put("lastname", appInstance.getUserCred().getLastname());
			loginObjnew.put("gender", appInstance.getUserCred().getGender());
			loginObjnew.put("live_in", appInstance.getUserCred().getLive_in());
			loginObjnew.put("age", appInstance.getUserCred().getAge());
			loginObjnew.put("profession", appInstance.getUserCred().getProfession());
			loginObjnew.put("seating_pref", appInstance.getUserCred().getSeating_pref());
			loginObjnew.put("some_about_you", appInstance.getUserCred().getSomethinAbout());
			loginObjnew.put("status", appInstance.getUserCred().getStatus());
			loginObjnew.put("image_name", "");
			loginObjnew.put("image_type", "");
			loginObjnew.put("image_content", "");
			UserCred ucredcopy=ucredFromServer;
			if(Constants.LIVE_IN_FLAG){
				ucredcopy.setLive_in(userCred.getLive_in());
				loginObjnew.put("live_in", userCred.getLive_in());
			}
			else if(Constants.AGE_FLAG){
				ucredcopy.setAge(userCred.getAge());
				loginObjnew.put("age", userCred.getAge());

			}
			else if(Constants.GENDER_FLAG){
				ucredcopy.setGender(userCred.getGender());
				loginObjnew.put("gender", userCred.getGender());

			}
			else if(Constants.POFESSION_FLAG){
				ucredcopy.setProfession(userCred.getProfession());
				loginObjnew.put("profession", userCred.getProfession());

			}
			else if(Constants.SEATING_PREF_FLAG){
				ucredcopy.setSeating_pref(userCred.getSeating_pref());
				loginObjnew.put("seating_pref", userCred.getSeating_pref());

			}
			else if(Constants.SOME_ABOUT_FLAG){
				ucredcopy.setSomethinAbout(userCred.getSomethinAbout());
				loginObjnew.put("some_about_you", userCred.getSomethinAbout());


			}
			else if(Constants.CHANGE_PHOTO_FLAG){
				//ucredcopy.setLive_in(userCred.getLive_in());
				loginObjnew.put("image_name", loginObj.get("image_name"));
				loginObjnew.put("image_type", loginObj.get("image_type"));
				loginObjnew.put("image_content",loginObj.get("image_content"));

			}
			userCred=ucredcopy;
			if(callfrom==1){
				JSONObject loginObj2 = new JSONObject();
				loginObj2.put("token", appInstance.getUserCred().getToken());
				callfrom=1;
				AsyncaTaskApiCall log_in_lisenar =new AsyncaTaskApiCall(FragmentMyAccount.this, loginObj2.toString(), context,
						"logout",Constants.REQUEST_TYPE_POST);
				log_in_lisenar.execute();
			}
			else if(callfrom==2){
				callfrom=2;
				AsyncaTaskApiCall update_prof_lisenar =new AsyncaTaskApiCall(FragmentMyAccount.this, loginObjnew.toString(),context,
						"reg_update",Constants.REQUEST_TYPE_POST);
				update_prof_lisenar.execute();
			}


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
		Constants.setAllFlagFalse();
		String message=joberror.getString("message");
		Toast.makeText(context, message,
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

