package com.seatunity.boardingpass.fragment;
import java.util.EnumMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
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
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.Constants;
import com.seatunity.model.BoardingPass;
@SuppressLint("NewApi")
public class FragmentUpcomingBoardingPassDetails extends Fragment {
	BoardingPass bpass;
	//BoardingPassApplication appInstance;
	public FragmentUpcomingBoardingPassDetails(BoardingPass bpass){
		this.bpass=bpass;
	}
	HomeListFragment parent;
	ImageView img_barcode;
	BoardingPassApplication appInstance;
	BoardingPass boardingPass;
	TextView tv_name_carrier,tv_month_inside_icon,tv_date_inside_icon,tv_from_air,tv_to_air,tv_start_time,tv_arrival_time,
	tv_flight_var,tv_seat_var,tv_compartment_class_var,tv_passenger_name;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		appInstance =(BoardingPassApplication) getActivity().getApplication();
		View rootView = inflater.inflate(R.layout.fragment_boarding_details, container, false);
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

		tv_name_carrier.setText(bpass.getCarrier());
		tv_from_air.setText(bpass.getTravel_from());
		tv_to_air.setText(bpass.getTravel_to());
		tv_start_time.setText(bpass.getDeparture());
		tv_arrival_time.setText(bpass.getArrival());
		tv_flight_var.setText(bpass.getFlight_no());
		tv_seat_var.setText(bpass.getSeat());
		tv_compartment_class_var.setText(bpass.getCompartment_code());
		tv_passenger_name.setText(bpass.getFirstname());

		tv_month_inside_icon.setText(bpass.getCarrier());
		tv_date_inside_icon.setText(bpass.getCarrier());
		String date=com.seatunity.boardingpass.utilty.Constants.getDayandYear(Integer.parseInt(bpass.getJulian_date()));
		String[] dateParts = date.split(":");
		String month=dateParts[1];
		String dateofmonth=dateParts[0];
		tv_month_inside_icon.setText(month);
		tv_date_inside_icon.setText(dateofmonth);

		generateBArcode();
		return rootView;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((MainActivity)getActivity()).item.setVisible(true);
		((MainActivity)getActivity()).item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				deleteBoardingPass();
				return false;
			}
		});
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		((MainActivity)getActivity()).item.setVisible(false);
	}
	public void generateBArcode(){
		String barcode_data = bpass.getStringform();

		// barcode image
		Bitmap bitmap = null;


		try {

			bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.AZTEC, 600, 600);
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
							AsyncaTaskApiCall callserver=new AsyncaTaskApiCall(FragmentUpcomingBoardingPassDetails.this, getJsonObjet(), getActivity(), url);
							callserver.execute();
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


}