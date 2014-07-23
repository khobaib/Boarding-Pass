package com.seatunity.boardingpass.fragment;
import java.util.EnumMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.seatunity.boardingpass.MainActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.interfaces.CallBackApiCall;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassList;
import com.seatunity.model.SeatMetList;
import com.seatunity.model.UserCred;
@SuppressLint("NewApi")
public class FragmentUpcomingBoardingPassDetails extends Fragment implements CallBackApiCall {
	BoardingPass bpass;
	SeatMetList list;
	HomeListFragment parent;
	ImageView img_barcode;
	Button btn_seatmate;
	BoardingPassApplication appInstance;
	int callfrom=0;
	Context context;
	MainActivity landingActivity;
	BoardingPass boardingPass;
	TextView tv_name_carrier,tv_month_inside_icon,tv_date_inside_icon,tv_from_air,tv_to_air,tv_start_time,tv_arrival_time,
	tv_flight_var,tv_seat_var,tv_compartment_class_var,tv_passenger_name;

	public FragmentUpcomingBoardingPassDetails(BoardingPass bpass){
		this.bpass=bpass;
		Log.e("insideList5", bpass.getTravel_from_name());
		
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if(getActivity()!=null){
			landingActivity=(MainActivity)getActivity();
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		appInstance =(BoardingPassApplication) getActivity().getApplication();
		View rootView = inflater.inflate(R.layout.fragment_boarding_details, container, false);
		btn_seatmate=(Button) rootView.findViewById(R.id.btn_seatmate);
		img_barcode=(ImageView) rootView.findViewById(R.id.img_barcode);
		tv_name_carrier=(TextView) rootView.findViewById(R.id.tv_name_carrier);
		tv_month_inside_icon=(TextView) rootView.findViewById(R.id.tv_month_inside_icon);
		tv_date_inside_icon=(TextView) rootView.findViewById(R.id.tv_date_inside_icon);
		tv_from_air=(TextView) rootView.findViewById(R.id.tv_from_air);
		tv_to_air=(TextView) rootView.findViewById(R.id.tv_to_air);
		tv_start_time=(TextView) rootView.findViewById(R.id.tv_start_time);
		tv_arrival_time=(TextView) rootView.findViewById(R.id.tv_arrival_time);
		tv_flight_var=(TextView) rootView.findViewById(R.id.tv_flight_var);
		tv_seat_var=(TextView) rootView.findViewById(R.id.tv_seat_var);
		tv_compartment_class_var=(TextView) rootView.findViewById(R.id.tv_compartment_class_var);
		tv_passenger_name=(TextView) rootView.findViewById(R.id.tv_passenger_name);
		tv_name_carrier.setText(bpass.getCarrier_name());
		tv_from_air.setText(bpass.getTravel_from());
		tv_to_air.setText(bpass.getTravel_to());
		tv_start_time.setText(bpass.getDeparture());
		tv_arrival_time.setText(bpass.getArrival());
		tv_flight_var.setText(bpass.getFlight_no());
		tv_seat_var.setText(bpass.getSeat());
		tv_compartment_class_var.setText(bpass.getCompartment_code());
		tv_passenger_name.setText(bpass.getFirstname());
		String date=com.seatunity.boardingpass.utilty.Constants.getDayandYear(Integer.parseInt(bpass.getJulian_date()));
		String[] dateParts = date.split(":");
		String month=dateParts[1];
		String dateofmonth=dateParts[0];
		tv_month_inside_icon.setText(month);
		tv_date_inside_icon.setText(dateofmonth);

		generateBArcode();
		btn_seatmate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if((Constants.isOnline(getActivity()))&&(!appInstance.getUserCred().getEmail().equals(""))){
					callSeatmet();
				}
				else{
					sowAlertMessage();
				}

			}
		});
		return rootView;
	}

	public void sowAlertMessage(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder
		.setMessage(getResources().getString(R.string.txt_seatmet_message_only_online))
		.setCancelable(false)
		.setPositiveButton(getResources().getString(R.string.txt_ok),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {

				dialog.cancel();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	public void callSeatmet(){
		callfrom=2;
		String extendedurl="seatmatelist/"+bpass.getCarrier()+"/"+bpass.getFlight_no()+"/"
				+bpass.getJulian_date();
		extendedurl=extendedurl.replace(" ", "");
		AsyncaTaskApiCall get_list =new AsyncaTaskApiCall(this, getJsonObjet(), getActivity(),
				extendedurl,Constants.REQUEST_TYPE_POST);
		get_list.execute();
	}
	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((MainActivity)getActivity()).delete_menu.setVisible(true);
		((MainActivity)getActivity()).delete_menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				deleteBoardingPass();
				return false;
			}
		});
		if(getActivity()!=null){
			landingActivity=(MainActivity)getActivity();
		}
		landingActivity.mTitle=context.getResources().getString(R.string.txt_boarding_pass);
		landingActivity.getActionBar().setTitle(landingActivity.mTitle);
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		((MainActivity)getActivity()).delete_menu.setVisible(false);
	}
	public void generateBArcode(){
		String barcode_data = bpass.getStringform();

		// barcode image
		Bitmap bitmap = null;
		try {
			BarcodeFormat barcodeFormat;
			Log.e("codetype", "ab "+bpass.getCodetype());
			if(bpass.getCodetype()==null){
				barcodeFormat=BarcodeFormat.QR_CODE;
			}
			else{
				if(bpass.getCodetype().equals("AZTEC")){
					barcodeFormat=BarcodeFormat.AZTEC;
				}
				else if(bpass.getCodetype().equals("QR_CODE")){
					barcodeFormat=BarcodeFormat.QR_CODE;
				}
				else if(bpass.getCodetype().equals("PDF_417")){
					barcodeFormat=BarcodeFormat.PDF_417;
				}
				else {
					barcodeFormat=BarcodeFormat.QR_CODE;
				}
			}
			
			bitmap = encodeAsBitmap(barcode_data, barcodeFormat, 600, 600);
			img_barcode.setImageBitmap(bitmap);

		} catch (WriterException e) {
			e.printStackTrace();
		}

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		appInstance =(BoardingPassApplication) getActivity().getApplication();
		context=getActivity();

	}

	private static final int WHITE = 0xFFFFFFFF;
	private static final int BLACK = 0xFF000000;

	Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
		String contentsToEncode = contents;
		if (contentsToEncode == null) {
			return null;
		}
		Map<EncodeHintType, Object> hints = null;
		String encoding = guessAppropriateEncoding(contentsToEncode);
		if (encoding != null) {
			hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result;
		try {
			result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
		} catch (IllegalArgumentException iae) {
			// Unsupported format
			return null;
		}
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	private static String guessAppropriateEncoding(CharSequence contents) {
		// Very crude at the moment
		for (int i = 0; i < contents.length(); i++) {
			if (contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}


	public void deleteBoardingPass(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle(R.string.txt_delete_boarding_pass);
		alertDialogBuilder.setMessage(R.string.txt_delete_boarding_pass_text);
		alertDialogBuilder.setPositiveButton(R.string.txt_confirm, 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {

				if(Constants.isOnline(getActivity())){
					if(bpass.getId().equals("-1")){
						updateDatabaseWithoutServernotification(1);
					}
					else{
						if(appInstance.getUserCred().getEmail().equals("")){
							updateDatabaseWithoutServernotification(1);
						}
						else{
							String url="bpdelete/"+bpass.getId();
							callfrom=1;
							AsyncaTaskApiCall get_list =new AsyncaTaskApiCall(FragmentUpcomingBoardingPassDetails.this, getJsonObjet(), getActivity(),
									url,Constants.REQUEST_TYPE_POST);
							get_list.execute();
						}
					}
				}
				else{
					if(bpass.getId().equals("-1")){
						updateDatabaseWithoutServernotification(1);
					}
					else{
						updateDatabaseWithoutServernotification(0);
					}
					//updateDatabaseWithoutServernotification(0);
				}
				dialog.cancel();
				parent.onBackPressed();
			}
		});
		alertDialogBuilder.setNegativeButton(R.string.txt_cancel, 
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.cancel();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}
	public void updateDatabaseWithoutServernotification(int state){
		SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
		dbInstance.open();
		if(state==0){
			bpass.setDeletestate(true);
			dbInstance.insertOrUpdateBoardingPass(bpass);
		}
		else{
			dbInstance.DeleteBoardingPass(bpass);
		}

		dbInstance.close();
	}

	public String getJsonObjet(){

		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token",appInstance.getUserCred().getToken());
			return loginObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	@Override
	public void responseOk(JSONObject job) {
		// TODO Auto-generated method stub
		//this.flag
		try {
			if(job.getString("success").equals("true")){
				//boarding_passes
				if(callfrom==1){
					updateDatabaseWithoutServernotification(1);
				}
				else if(callfrom==2){
					SeatMetList  seatmet_listlist=SeatMetList.getSeatmetListObj(job);
					this.list=seatmet_listlist;
					if(list.getAllSeatmateList().size()>0){
						parent.startSeatmetList(list,bpass);
					}

				}

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
				JSONObject loginObj = new JSONObject();
				loginObj.put("email", appInstance.getUserCred().getEmail());
				loginObj.put("password", appInstance.getUserCred().getPassword());
				String loginData = loginObj.toString();
				AsyncaTaskApiCall log_in_lisenar =new AsyncaTaskApiCall(FragmentUpcomingBoardingPassDetails.this, loginData, 
						context,"login",Constants.REQUEST_TYPE_POST,true);
				log_in_lisenar.execute();

			}
			else{
					Toast.makeText(context, job.getString("message"),
							Toast.LENGTH_SHORT).show();
			} 
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void saveLoginCred(JSONObject job) {
		// TODO Auto-generated method stub
		try {
			UserCred userCred;
			String status=job.getString("success");
			if(status.equals("true")){
				userCred=UserCred.parseUserCred(job);
				userCred.setEmail(appInstance.getUserCred().getEmail());
				userCred.setPassword(appInstance.getUserCred().getPassword());
				appInstance.setUserCred(userCred);
				appInstance.setRememberMe(true);
				if(callfrom==1){
					String url="bpdelete/"+bpass.getId();
					callfrom=1;
					AsyncaTaskApiCall get_list =new AsyncaTaskApiCall(FragmentUpcomingBoardingPassDetails.this, getJsonObjet(),context,
							url,Constants.REQUEST_TYPE_POST);
					get_list.execute();
				}
				else if(callfrom==2){
					callfrom=2;
					String extendedurl="seatmatelist/"+bpass.getCarrier()+"/"+bpass.getFlight_no()+"/"
							+bpass.getJulian_date();
					extendedurl=extendedurl.replace(" ", "");
					AsyncaTaskApiCall get_list =new AsyncaTaskApiCall(FragmentUpcomingBoardingPassDetails.this, getJsonObjet(), context,
							extendedurl,Constants.REQUEST_TYPE_POST);
					get_list.execute();
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