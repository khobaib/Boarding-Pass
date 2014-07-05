package com.seatunity.boardingpass.fragment;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.Intents;
import com.google.zxing.common.BitMatrix;
import com.seatunity.boardingpass.AcountActivity;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.db.BoardingPassDbManager;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassParser;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class FragmentUpcomingBoardingPassDetails extends Fragment {
    BoardingPass bpass;
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


}