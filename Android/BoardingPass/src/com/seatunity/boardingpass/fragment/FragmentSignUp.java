
package com.seatunity.boardingpass.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.seatunity.boardingpass.AcountActivity;
import com.seatunity.boardingpass.ForgotPassActivity;
import com.seatunity.boardingpass.HomeActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.adapter.NothingSelectedSpinnerAdapter;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.Member;
import com.seatunity.model.ServerResponse;
import android.R.anim;
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
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class FragmentSignUp extends Fragment implements CallBackApiCall{
	//int selectedageposition=-1;
	TextView tv_signup_message,tv_sign_message;
	EditText et_email,et_password,et_confirm_password,et_first_name,et_last_name,et_livein,et_profession,et_seatting_pref,et_age;
	Button bt_register;
	String email="",gender,password="",confirmpassword="",firstname="",lastname="",livein="",age="",profession="",seating="";
	//Spinner s_age;
	RadioGroup rdgrp_gender;
	int SEATING_PREF=0;
	int AGE=1;
	int LIVE_IN=2;

	ArrayList<String> agelist=new ArrayList<String>();
	String[] NoCore_Array = new String [5];
	{ 
		NoCore_Array[0] = "1";
		NoCore_Array[1] = "2";
		NoCore_Array[2] = "3";
		NoCore_Array[3] = "4";
		NoCore_Array[4] = "5";

	}
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		agelist.clear();
		agelist.add("< 15");
		for(int i=15;i<100;){
			String agecalculation=i+" - "+(i+4);
			agelist.add(agecalculation);
			i=i+5;
		}
		agelist.add("100 <");
		for(int i=0;i<agelist.size();i++){
			Log.i(""+i, agelist.get(i));

		}


	}
	public void showCustomDialog(){
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_dilog);
		RelativeLayout  re_ok = (RelativeLayout) dialog.findViewById(R.id.re_ok);
		re_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				dialog.dismiss();

			}
		});
		dialog.show();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.signup,
				container, false);
		rdgrp_gender=(RadioGroup) v.findViewById(R.id.rdgrp_gender);

		initview(v);
		return v;
	}
	
	public void initview(ViewGroup v){
		bt_register=(Button) v.findViewById(R.id.bt_register);
		tv_signup_message=(TextView) v.findViewById(R.id.tv_signup_message);
		tv_sign_message=(TextView) v.findViewById(R.id.tv_sign_message);
		et_email=(EditText) v.findViewById(R.id.et_email);
		et_password=(EditText) v.findViewById(R.id.et_password);
		et_confirm_password=(EditText) v.findViewById(R.id.et_confirm_password);
		et_first_name=(EditText) v.findViewById(R.id.et_first_name);
		et_last_name=(EditText) v.findViewById(R.id.et_last_name);
		et_livein=(EditText) v.findViewById(R.id.et_livein);
		et_age=(EditText) v.findViewById(R.id.et_age);
		et_profession=(EditText) v.findViewById(R.id.et_profession);
		et_seatting_pref=(EditText) v.findViewById(R.id.et_seatting_pref);
		et_seatting_pref.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String[] seating_pref_list = getActivity().getResources().getStringArray(R.array.seating_pref); 
				String title=getActivity().getResources().getString(R.string.txt_seatting_pref_cap);
				showDialogForSeatingPref(seating_pref_list,title,SEATING_PREF);
			}
		});
		et_age.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				showDialogForSeatingPref(agelist.toArray(new CharSequence[agelist.size()])
						,getActivity().getResources().getString(R.string.txt_age),AGE);
				
			}
		});
		String text = "<font color=#000000>By registering, you acknowledge that you have read and agreed to the SeatUnit</font>" +
				" <font color=#0099cc>Terms of Conditions</font>";
		tv_sign_message.setText(Html.fromHtml(text));
		bt_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				//email,password,confirmpassword,firstname,lastname,livein,age,profession,seating
				email=et_email.getText().toString();
				password=et_password.getText().toString();
				confirmpassword=et_confirm_password.getText().toString();
				firstname=et_first_name.getText().toString();
				lastname=et_last_name.getText().toString();
				livein=et_livein.getText().toString();
				age=et_age.getText().toString();
				profession=et_profession.getText().toString();
				//seating=et_seatting_pref.getText().toString();
				et_email.addTextChangedListener(new TextWatcher() {          
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
						et_email.setBackgroundResource(android.R.drawable.editbox_background_normal);

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

				et_password.addTextChangedListener(new TextWatcher() {          
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
						et_password.setBackgroundResource(android.R.drawable.editbox_background_normal);

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


				if(Constants.isOnline(getActivity())){
					if(!confirmpassword.equals(password)){
						Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.txt_password_mismatched),
								Toast.LENGTH_SHORT).show();
						et_password.setBackgroundResource(R.drawable.rounded_text_nofield);
						et_confirm_password.setBackgroundResource(R.drawable.rounded_text_nofield);


					}
					else if(!Constants.isValidEmail(email)){
						Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.txt_enter_valid_email),
								Toast.LENGTH_SHORT).show();
						et_email.setBackgroundResource(R.drawable.rounded_text_nofield);

					}
					else{
						callsignUp();
					}
				}
				else{
					Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.txt_check_internet),
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	//	setcity();


	}
//	private void setcity(){

//		ArrayAdapter  adapter2 = new ArrayAdapter<String>(getActivity(),
//				R.drawable.contact_spinner_row_nothing_selected_age, agelist);
//		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		s_age.setAdapter( new  NothingSelectedSpinnerAdapter(
//				adapter2, R.drawable.contact_spinner_row_nothing_selected_age, getActivity()));
//		s_age.setOnItemSelectedListener(new OnItemSelectedListener(){
//
//			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, 
//					long arg3){
//				selectedageposition=position-1;
//
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				selectedageposition=-1;
//			}
//		}); 
//	}

	public void callsignUp(){
		if((!email.equals(""))&&(!password.equals(""))&&(!password.equals(""))){
			String signupdata="";
			signupdata=getJsonObjet();
			AsyncaTaskApiCall sign_uplisenar =new AsyncaTaskApiCall(FragmentSignUp.this, signupdata, getActivity(),
					"reg",Constants.REQUEST_TYPE_POST);
			sign_uplisenar.execute();
		}
		else{
			if(email.equals("")){
				et_email.setBackgroundResource(R.drawable.rounded_text_nofield);
			}
			if(password.equals("")){
				et_password.setBackgroundResource(R.drawable.rounded_text_nofield);
			}
			if(confirmpassword.equals("")){
				et_confirm_password.setBackgroundResource(R.drawable.rounded_text_nofield);
			}

			Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.txt_fillup_required_field),
					Toast.LENGTH_SHORT).show();
		}
		et_confirm_password.addTextChangedListener(new TextWatcher() {          
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {                                   
				et_confirm_password.setBackgroundResource(android.R.drawable.editbox_background_normal);

			}                       
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}                       
			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}
	public String getJsonObjet(){
		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("email",email);
			loginObj.put("password",password);
			String required = loginObj.toString();
			loginObj.put("firstname",firstname);
			loginObj.put("lastname",lastname);
			loginObj.put("seating_pref","male");
			loginObj.put("language",Locale.getDefault().getLanguage());
			loginObj.put("live_in",livein);
			int selected =  rdgrp_gender.getCheckedRadioButtonId();
			RadioButton rb_gender;
			if(selected==R.id.radio_female){

				gender=getActivity().getResources().getString(R.string.txt_gender_female);
			}
			else if(selected==R.id.radio_male){
				gender=getActivity().getResources().getString(R.string.txt_gender_male);
			}
			else if(selected==R.id.radio_not_say){
				gender=getActivity().getResources().getString(R.string.txt_gender_rather_not_say);
			}
			loginObj.put("gender",gender);
			String age=et_age.getText().toString();
			if((!age.equals(""))&&(!age.equals(getActivity().getResources().getString(R.string.txt_age)))){
				loginObj.put("age",age);
			}
			else{
				loginObj.put("age","");
			}
			loginObj.put("profession",profession);
			loginObj.put("seating_pref",seating);
			return loginObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public void showDialogForSeatingPref(final CharSequence[] items,String title,final int type)
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
		View customTitleView = inflater.inflate(R.layout.custom_title_view, null);
		TextView tv_title=(TextView) customTitleView.findViewById(R.id.tv_title);
		tv_title.setText(title);
		builder.setCustomTitle(customTitleView);
		builder.setPositiveButton(getActivity().getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		builder.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(type==SEATING_PREF){
					 seating=items[which].toString();
					 et_seatting_pref.setText(seating);
				}
				else if(type==AGE){
					et_age.setText(items[which].toString());
				}
				dialog.cancel();
			}
		});
		builder.show();

	}
	@Override
	public void responseOk(JSONObject job) {
		// TODO Auto-generated method stub
		((AcountActivity)getActivity()).indicator.setViewPager(((AcountActivity)getActivity()).pager, 0);
		showCustomDialog();
	}
	@Override
	public void responseFailure(JSONObject job) {
		// TODO Auto-generated method stub
		Log.e("A", "1");
		Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_signup_failed1), 
				Toast.LENGTH_SHORT).show();
	}
	@Override
	public void saveLoginCred(JSONObject job) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void LoginFailed(JSONObject job) {
		// TODO Auto-generated method stub
//		Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_signup_failed1), 
//				Toast.LENGTH_SHORT).show();
	}
}

