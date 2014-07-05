package com.seatunity.boardingpass.fragment;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.android.Intents;
import com.google.zxing.common.HybridBinarizer;
import com.seatunity.boardingpass.R;
import com.seatunity.boardingpass.asynctask.AsyncaTaskApiCall;
import com.seatunity.boardingpass.db.SeatUnityDatabase;
import com.seatunity.boardingpass.utilty.BoardingPassApplication;
import com.seatunity.boardingpass.utilty.PkpassReader;
import com.seatunity.model.BoardingPass;
import com.seatunity.model.BoardingPassParser;
import com.touhiDroid.filepicker.FilePickerActivity;

@SuppressLint("NewApi")
public class FragmentAddBoardingPass extends Fragment {

	public FragmentAddBoardingPass(){}
	HomeListFragment parent;
	ImageView img_from_camera,img_from_sdcard,img_imtotest;
	TextView tv_add_boardingpasswith_camera,tv_add_fromsdcard;
	BoardingPassApplication appInstance;
	BoardingPass boardingPass;
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("calledadd", "onResume");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("calledadd", "onCreateView");
		appInstance =(BoardingPassApplication) getActivity().getApplication();
//M1SUMON/UWEMR         EYWX9ZS DHABARLH 2074 102M037A0016 355>2180KO3075BOS 022052227001 262202331497901  LH                     *30601001205
		
		View rootView = inflater.inflate(R.layout.add_boarding_passs, container, false);
		img_imtotest=(ImageView)rootView.findViewById(R.id.img_imtotest);
		img_imtotest.setVisibility(View.GONE);

		tv_add_boardingpasswith_camera=(TextView) rootView.findViewById(R.id.tv_add_boardingpasswith_camera);
		img_from_sdcard=(ImageView) rootView.findViewById(R.id.img_from_sdcard);
		tv_add_fromsdcard=(TextView) rootView.findViewById(R.id.tv_add_fromsdcard);
		img_from_camera=(ImageView) rootView.findViewById(R.id.img_from_camera);
		img_from_sdcard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onclicksdcard();
			}
		});
		tv_add_fromsdcard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onclicksdcard();
			}
		});
		img_from_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onclickacamera();
							
			}
		});
		tv_add_boardingpasswith_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onclickacamera();
			}
		});
		return rootView;
	}
	public void onclickacamera(){
		Intent intent = new Intent("com.touhiDroid.android.SCAN");
		intent.putExtra(Intents.Scan.FORMATS, Intents.Scan.AZTEC_MODE
								 + "," + Intents.Scan.PDF417_MODE
								 + "," + Intents.Scan.QR_CODE_MODE
								 + "," + Intents.Scan.DATA_MATRIX_MODE
								);
		startActivityForResult(intent, 0);
	}
	public void onclicksdcard(){
		Intent intent = new Intent(getActivity(), FilePickerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, 10);
		
	}
	
	private void handleSmallCameraPhoto(final Bitmap bmp) {
		img_imtotest.setVisibility(View.VISIBLE);
		img_imtotest.post(new Runnable() {
		        @Override
		        public void run()
		        {
//		        	img_imtotest.setVisibility(View.VISIBLE);
//		        	img_imtotest.setImageBitmap(bmp);
		        	
		        }
		    });
	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		tv_add_boardingpasswith_camera.setText("gefcefckhgdfc");
		if (requestCode == 0) {
			if (resultCode == getActivity().RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.e("tag", contents+" "+format);
				saveScannedBoardingPasstodatabes(contents,format);
			} else if (resultCode == getActivity().RESULT_CANCELED) {
				// Handle cancel
				Log.i("App","Scan unsuccessful");

			}
		}
		
		else if (requestCode == 10) {
			if (resultCode == getActivity().RESULT_OK) {
//				 "file_format_key_touhiDoid";
//					public static final String FILE_PATH_KEY = "file_path_key_touhiDoid";
				int format=intent.getIntExtra(FilePickerActivity.FILE_FORMAT_KEY, 0);
				String filepath=intent.getStringExtra(FilePickerActivity.FILE_PATH);
				Log.e("tag", filepath);
				
				if(format==FilePickerActivity.PDF_FILE){
					GetBoardingPassFromPDF(filepath);
				}
				else if(format==FilePickerActivity.PASSBOOK_FILE){
					String boardingpass=PkpassReader.getFormattedStringFromPassFile(filepath);
					Log.e("boardingpass", "A");
				}
				else if(format==FilePickerActivity.IMAGE_FILE){
					Bitmap bitmap = BitmapFactory.decodeFile(filepath);
					scanBarcodeFromImage(bitmap);
					Log.e("boardingpass", "B");
				}
				
			} else if (resultCode == getActivity().RESULT_CANCELED) {
				// Handle cancel
				Log.i("App","Scan unsuccessful");

			}
		}
		else if (requestCode == 12) {
			if (resultCode == getActivity().RESULT_OK) {
				Log.e("tag", "1");
				byte[] ba = intent.getByteArrayExtra("bitmap_ba");
				Log.e("tag", "2");

				Bitmap bmp = BitmapFactory.decodeByteArray(ba, 0, ba.length);
				Log.e("tag", "3");

				if (bmp != null){
					Log.e("tag", "4");
					scanBarcodeFromImage(bmp);
				//	handleSmallCameraPhoto(bmp);
					Log.e("tag", "5");
//					Toast.makeText(getActivity(), "called "+ bmp.getWidth(), 
//			                 Toast.LENGTH_LONG).show();
				}
				

				//img_imtotest.setImageBitmap(bmp);
				
			 
//				byte[] ba=intent.getByteArrayExtra("bitmap_ba");
//				Bitmap bm=BitmapFactory.decodeByteArray(ba, 0, ba.length);
//				
//				Bitmap bmp;
//				bmp = BitmapFactory.decodeByteArray(ba, 0, ba.length);
//				Bitmap mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
//				
//				
//				
//				img_imtotest.setVisibility(View.VISIBLE);
//				img_imtotest.setImageBitmap(mutableBitmap);
				
			} else if (resultCode == getActivity().RESULT_CANCELED) {
				// Handle cancel
				Log.i("App","Scan unsuccessful");

			}
		}
	}
	
	public void GetBoardingPassFromPDF(String filepath){
		Intent intent = new Intent(getActivity(), MuPDFActivity.class);
		intent.setAction("com.touhiDroid.PDF.GET_BITMAP");
		intent.setData(Uri.parse(filepath));
		startActivityForResult(intent, 12);
		
//		Intent intent = new Intent(getActivity(), MuPDFActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setData(Uri.parse(filepath));
//		intent.setAction("com.touhiDroid.PDF.GET_BITMAP");
//		startActivityForResult(intent, 12);
	}
	public void saveScannedBoardingPasstodatabes(String contents,String format){
		
//		
//		stringform, firstname, lastname,PNR ,travel_from, travel_to, carrier, 
//		flight_no, julian_date, compartment_code, seat,
//		departure, arrival,codetype
		
		if(contents.length()<100){
			Log.e("size", contents.length()+"");
			Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_invalid_borading_pass),
					Toast.LENGTH_SHORT).show();
		}
		else{
			Log.e("size", contents.length()+"");
			BoardingPassParser boardingpassparser=new BoardingPassParser(contents, format);
			boardingPass=boardingpassparser.getBoardingpass();
			Log.e("tag", "1");
			if(appInstance.getUserCred().getEmail().equals("")){
				setBoardingpassInLocalDB();	
				Log.e("tag", "2");

			}
			else{
				if(com.seatunity.boardingpass.utilty.Constants.isOnline(getActivity())){
					SaveboardingPasstoServer( boardingPass);
					Log.e("tag", "3");

				}
				else{
					Log.e("tag", "4");

					setBoardingpassInLocalDB();
				}
			}

			
		}
		
// 
	}
	
	public void SaveboardingPasstoServer(BoardingPass bpass){
		String bpassdata="";
		bpassdata=getJsonObjet(bpass);
		AsyncaTaskApiCall apicalling=new AsyncaTaskApiCall(FragmentAddBoardingPass.this, bpassdata, getActivity());
		apicalling.execute();
	}
	
	public String getJsonObjet(BoardingPass bpass){

		try {
			JSONObject loginObj = new JSONObject();
			loginObj.put("token",appInstance.getUserCred().getToken());
			loginObj.put("version","1");
			loginObj.put("stringform",bpass.getStringform());
			loginObj.put("firstname",bpass.getFirstname());
			loginObj.put("lastname",bpass.getLastname());
			loginObj.put("PNR",bpass.getPNR());
			loginObj.put("travel_from",bpass.getTravel_from());
			loginObj.put("travel_to",bpass.getTravel_to());
			loginObj.put("carrier",bpass.getCarrier());
			loginObj.put("flight_no",bpass.getFlight_no());
			loginObj.put("julian_date",bpass.getJulian_date());
			loginObj.put("compartment_code",bpass.getCompartment_code());
			loginObj.put("seat",bpass.getSeat());
			loginObj.put("departure",bpass.getDeparture());
			loginObj.put("arrival",bpass.getArrival());
			loginObj.put("year","2014");
			return loginObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public void addBoardingPassonBackendSuccess(JSONObject result){
		Log.e("result", result.toString());
		try {
			//07-01 23:11:05.628: E/responses(6577): {"id":"10","success":"true"}

			String success=result.getString("success");
			if(success.equals("true")){
				String id=result.getString("id");
				boardingPass.setId(id);
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_boarding_pass_added_successfully),
						Toast.LENGTH_SHORT).show();
				setBoardingpassInLocalDB();

			}
			else{
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.txt_invalid_borading_pass),
						Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void setBoardingpassInLocalDB(){
		SeatUnityDatabase dbInstance = new SeatUnityDatabase(getActivity());
		dbInstance.open();
		dbInstance.insertOrUpdateBoardingPass(boardingPass);
		ArrayList<BoardingPass>list=(ArrayList<BoardingPass>) dbInstance.retrieveBoardingPassList();
		dbInstance.close();	
	}
	
	
	public void scanBarcodeFromImage(Bitmap bmap){
		Bitmap bMap = bmap;
		try {
			int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
			bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
			        bMap.getHeight());
			LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
			        bMap.getHeight(), intArray);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			Reader reader = new MultiFormatReader();// use this otherwise
			try {
				 Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<DecodeHintType, Object>();
				 decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
				 decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
				 Result result = reader.decode(bitmap, decodeHints);
			     Log.e("text", result.getText())  ;
			     Log.e("format", result.getBarcodeFormat().toString())  ;


			} catch (NotFoundException e) {
			    e.printStackTrace();
			} catch (ChecksumException e) {
			    e.printStackTrace();
			} catch (FormatException e) {
			    e.printStackTrace();
			} catch (NullPointerException e) {
			    e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}