
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
import com.seatunity.model.ServerResponse;
import com.seatunity.model.UserCred;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
	
	public FragmentMyAccount(){
	}
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInstance =(BoardingPassApplication) getActivity().getApplication();
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_my_account,
				container, false);
		tv_uname=(TextView) v.findViewById(R.id.tv_uname);
		tv_email=(TextView) v.findViewById(R.id.tv_email);
		tv_stataus=(TextView) v.findViewById(R.id.tv_stataus);
		img_prof_pic=(ImageView) v.findViewById(R.id.img_prof_pic);
		lv_setting=(ListView) v.findViewById(R.id.lv_setting);
		adapter=new AdapterForSettings(getActivity(), setting_criteria,appInstance.getUserCred());
		lv_setting.setAdapter(adapter);
		img_edit=(ImageView) v.findViewById(R.id.img_edit);
		spn_country=(Spinner) v.findViewById(R.id.spn_country);
		tv_uname.setText(appInstance.getUserCred().getFirstname());
		tv_email.setText(appInstance.getUserCred().getEmail());
		tv_stataus.setText(appInstance.getUserCred().getStatus());
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
				// TODO Auto-generated method stub
				Intent intent=new Intent(getActivity(), UploadPicActivity.class);
				startActivity(intent);
				
			}
		});
		lv_setting.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position==0){
					String[] country_list = getActivity().getResources().getStringArray(R.array.countries_array); 
					showDialogForGender(country_list,getActivity().getResources().getString(R.string.txt_select_country));
				}
				if(position==1){
					String[] age_list = getActivity().getResources().getStringArray(R.array.age_range_list); 
					showDialogForGender(age_list,getActivity().getResources().getString(R.string.txt_select_age));
				}
				else if(position==2){
					setSomethingAbou(getActivity().getResources().getString(R.string.txt_sm_about)
							,getActivity().getResources().getString(R.string.txt_say_sm_about));
				}
				else if(position==3){
					setSomethingAbou(getActivity().getResources().getString(R.string.txt_edit_profesion)
							,getActivity().getResources().getString(R.string.acc_prof));
				}
				else if(position==4){
					String[] seating_pref_list = getActivity().getResources().getStringArray(R.array.seating_pref); 
					String title=getActivity().getResources().getString(R.string.txt_seatting_pref_cap);
					showDialogForGender(seating_pref_list,title);
				}
				else if(position==5){
					setSomethingAbou(getActivity().getResources().getString(R.string.txt_sm_about)
							,getActivity().getResources().getString(R.string.txt_say_sm_about));
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

	private void setcountry(){
		ArrayList<String> allcountryname=new ArrayList<String>();
		String[] cn_list = getActivity().getResources().getStringArray(R.array.countries_array); 
		allcountryname = new ArrayList<String>(Arrays.asList(cn_list));
		ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, allcountryname);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn_country.setAdapter(
				new NothingSelectedSpinnerAdapter(
						adapter,
						R.drawable.contact_spinner_row_nothing_selected_country,
						getActivity()));
		spn_country.performClick();
		//spn_uname.performItemClick(view, position, id)

		spn_country.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> parent1, View arg1, int position, 
					long arg3){
				if(position>0){
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	public void showDialogForGender(final CharSequence[] items,String title)
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
			}
		});
		builder.show();

	}
	public void setSomethingAbou(String title,String hint){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(title);
		final EditText input = new EditText(getActivity());
		input.setHint(hint);
		alert.setView(input);
		alert.setPositiveButton(getActivity().getResources().getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
			}
		});

		alert.setNegativeButton(getActivity().getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		alert.show();
	}

	public void showCustomDialogForPasswordChange(){
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.edit_user_name);
		dialog.show();
	}
	public void successfullyLoggedout(){
		
	}


}

