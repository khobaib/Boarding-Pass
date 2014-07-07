
package com.seatunity.boardingpass.fragment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.seatunity.boardingpass.EditUserNameActivity;
import com.seatunity.boardingpass.HomeActivity;
import com.seatunity.boardingpass.PasswordChangeActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.UploadPicActivity;
import com.seatunity.boardingpass.adapter.AdapterForSettings;
import com.seatunity.boardingpass.adapter.NothingSelectedSpinnerAdapter;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.boardingpass.utilty.PkpassReader;
import com.seatunity.model.ImageScale;
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;
import com.touhiDroid.filepicker.FilePickerActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
public class FragmentMyAccount extends Fragment{
	ImageView img_edit,img_prof_pic;
	ArrayList<String>setting_criteria;
	BoardingPassApplication appInstance;
	AdapterForSettings adapter;
	ListView lv_setting;
	Spinner spn_country;
	AccountListFragment parent;
	TextView tv_uname,tv_email,tv_stataus;
	UserCred userCred;
	int ACTION_REQUEST_CAMERA=0;
	int ACTION_REQUEST_GALLERY=1;
	 String drectory;
	 String photofromcamera;
	ViewGroup v ;
	Bitmap photo;
	public FragmentMyAccount(){
	}
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	Log.e("inside", "onCreate");
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
	public void setView(){
		tv_uname.setText(appInstance.getUserCred().getFirstname());
		tv_email.setText(appInstance.getUserCred().getEmail());
		tv_stataus.setText(appInstance.getUserCred().getStatus());
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//Log.e("inside", "onResume");

		appInstance =(BoardingPassApplication) getActivity().getApplication();
		userCred=appInstance.getUserCred();
		if(photo!=null){
			img_prof_pic.setImageBitmap(photo);
		}
		setView();
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
	//	Log.e("inside", "onCreateView");
		 v = (ViewGroup) inflater.inflate(R.layout.fragment_my_account,
				container, false);
		tv_uname=(TextView) v.findViewById(R.id.tv_uname);
		tv_email=(TextView) v.findViewById(R.id.tv_email);
		tv_stataus=(TextView) v.findViewById(R.id.tv_stataus);
		img_prof_pic=(ImageView) v.findViewById(R.id.img_prof_pic);
		lv_setting=(ListView) v.findViewById(R.id.lv_setting);
		adapter=new AdapterForSettings(getActivity(), setting_criteria,userCred);
		lv_setting.setAdapter(adapter);
		img_edit=(ImageView) v.findViewById(R.id.img_edit);
		spn_country=(Spinner) v.findViewById(R.id.spn_country);

		img_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getActivity(), EditUserNameActivity.class);
				startActivity(intent);
			}
		});
		img_prof_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String[] photochooser = getActivity().getResources().getStringArray(R.array.upload_photo_from); 
				showDialogTochosePhoto(photochooser,getActivity().getResources().getString(R.string.txt_select_country));



			}
		});
		lv_setting.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position==0){
					String[] country_list = getActivity().getResources().getStringArray(R.array.countries_array); 
					showDialogForGender(country_list,getActivity().getResources().getString(R.string.txt_select_country),position);
				}
				if(position==1){
					String[] age_list = getActivity().getResources().getStringArray(R.array.age_range_list); 
					showDialogForGender(age_list,getActivity().getResources().getString(R.string.txt_select_age),position);
				}
				else if(position==2){
					String[] gender_list = getActivity().getResources().getStringArray(R.array.gender); 
					showDialogForGender(gender_list,getActivity().getResources().getString(R.string.txt_select_age),position);
				}
				else if(position==3){
					setSomethingAbou(getActivity().getResources().getString(R.string.txt_edit_profesion)
							,getActivity().getResources().getString(R.string.acc_prof),position);
				}
				else if(position==4){
					String[] seating_pref_list = getActivity().getResources().getStringArray(R.array.seating_pref); 
					String title=getActivity().getResources().getString(R.string.txt_seatting_pref_cap);
					showDialogForGender(seating_pref_list,title,position);
				}
				else if(position==5){
					setSomethingAbou(getActivity().getResources().getString(R.string.txt_sm_about)
							,getActivity().getResources().getString(R.string.txt_say_sm_about),position);
				}
				else if(position==6){
					Intent intent= new Intent(getActivity(), PasswordChangeActivity.class);
					startActivity(intent);
				}
				else if(position==7){
					Signout();
				}
			}
		});

		return v;
	}
	public void Signout(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getActivity().getResources().getString(R.string.txt_sign_out_msz))
		.setCancelable(false)
		.setPositiveButton(getActivity().getResources().getString(R.string.txt_confirm), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				try {
					JSONObject loginObj = new JSONObject();
					loginObj.put("token", appInstance.getUserCred().getToken());

					AsyncaTaskApiCall logoutcall=new AsyncaTaskApiCall(appInstance,FragmentMyAccount.this, loginObj.toString(),
							getActivity(), "logout");
					logoutcall.execute();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		})
		.setNegativeButton(getActivity().getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		})
		.setTitle(getActivity().getResources().getString(R.string.acc_sign_out))
		.setIcon(R.drawable.ic_sing_out);
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void showDialogTochosePhoto(final CharSequence[] items,String title)
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setPositiveButton(getActivity().getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {

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
					
					
//					Intent cameraIntent = new Intent(
//							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//					startActivityForResult(
//							cameraIntent,
//							ACTION_REQUEST_CAMERA);
				}
				else if(which==1){
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
				}
			}
		});
		builder.show();

	}

	public void showDialogForGender(final CharSequence[] items,String title,final int postion)
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setPositiveButton(getActivity().getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();



			}
		});

		builder.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
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

					if(postion==0){
						String country=items[which].toString();
						loginObj.put("live_in", country);
						userCred.setLive_in(country);
					}
					else if(postion==1){
						String age=items[which].toString();
						loginObj.put("age", age);
						userCred.setAge(age);
					}
					else if(postion==2){
						String gender=items[which].toString();
						loginObj.put("gender", gender);
						userCred.setGender(gender);
					}
					else if(postion==4){
						String seat_pref=items[which].toString();
						loginObj.put("seating_pref", seat_pref);
						userCred.setSeating_pref(seat_pref);
					}
					AsyncaTaskApiCall logoutcall=new AsyncaTaskApiCall(appInstance,FragmentMyAccount.this, loginObj.toString(),
							getActivity(), "reg");
					logoutcall.execute();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		builder.show();

	}
	public void setSomethingAbou(String title,final String hint,final int position){

		final EditText input = new EditText(getActivity());
		input.setHint(hint);
		final AlertDialog d = new AlertDialog.Builder(getActivity())
		.setView(input)
		.setTitle(title)
		.setPositiveButton(getActivity().getResources().getString(R.string.txt_ok), null) //Set to null. We override the onclick
		.setNegativeButton(getActivity().getResources().getString(R.string.txt_cancel), null)
		.create();

		d.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String value = input.getText().toString();

						if((value==null)||(value.equals(""))){
							Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.
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

								AsyncaTaskApiCall logoutcall=new AsyncaTaskApiCall(appInstance,FragmentMyAccount.this, loginObj.toString(),
										getActivity(), "reg");
								logoutcall.execute();
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
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.edit_user_name);
		dialog.show();
	}
	public void successfullyUpdateYourProfile(){
		appInstance.setUserCred(userCred);
		adapter.notifyDataSetChanged();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//img_prof_pic.setVisibility(View.GONE);
		Log.e("inside", "onActivityResult");
		if (requestCode == 0) {
			if (requestCode == ACTION_REQUEST_GALLERY) {
				Uri selectedImageUri = data.getData();
				Log.e("Uri", selectedImageUri.toString());
			} else if (requestCode ==ACTION_REQUEST_CAMERA) {
				try
				{

					String filepath = drectory+"/"+photofromcamera;

					File file=new File(filepath);

					if(file.exists()){
						ImageScale scaleimage=new ImageScale();
						final Bitmap photo = scaleimage.decodeImagetoUpload(file.getAbsolutePath());
						Log.e("bitmap ", ""+photo.getHeight()+"   "+photo.getWidth());
						//Bitmap p=Bitmap.createScaledBitmap(photo, photo.getWidth()/6, photo.getHeight()/6,true);
						file.delete();
						ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						photo.compress(Bitmap.CompressFormat.PNG, 80, bytes);
						img_prof_pic.setImageBitmap(photo);  
						File f = new File(filepath);
						f.createNewFile();
						FileOutputStream fo = new FileOutputStream(f);
						fo.write(bytes.toByteArray());
						fo.close();

						Toast.makeText(getActivity(), ""+img_prof_pic+" "+photo.getHeight(),
								3000).show();
						
//					img_prof_pic.post(new Runnable() {
//					        @Override
//					        public void run()
//					        {
//					        	img_prof_pic.setImageBitmap(photo);  
//					        }
//					    });
//						String thumnilpath=drectorythumb+"/"+photofromcamera;
//						Log.e("path", thumnilpath);
//						//	photo=Bitmap.createScaledBitmap(photo, photo.getWidth()/6, photo.getHeight()/600,true);
//						Log.e("path", "a  "+ p);
//						bytes = new ByteArrayOutputStream();
//						p.compress(Bitmap.CompressFormat.PNG, 80, bytes);
//
//						File filethumb = new File(thumnilpath);
//						filethumb.createNewFile();
//						FileOutputStream fothumb = new FileOutputStream(filethumb);
//						fothumb.write(bytes.toByteArray());
//						fothumb.close();


					}

				}
				catch(Exception e)
				{
					Log.e("Could not save", e.toString());
				}
				 
				//photo = (Bitmap) data.getExtras().get("data");
				//img_prof_pic=(ImageView) v.findViewById(R.id.img_prof_pic);
				 

			}
		}
	}

	private void handleSmallCameraPhoto(final Bitmap bmp) {
		img_prof_pic.post(new Runnable() {
			@Override
			public void run()
			{
				img_prof_pic.setImageBitmap(bmp);
			}
		});
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
}

