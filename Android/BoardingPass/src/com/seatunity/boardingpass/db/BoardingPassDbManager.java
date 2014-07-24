package com.seatunity.boardingpass.db;

import java.util.ArrayList;
import java.util.List;
import com.seatunity.model.BoardingPass;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
public class BoardingPassDbManager {
	private static final String TAG = BoardingPassDbManager.class.getSimpleName();
	private static String TABLE_BOARDING_PASS_LIST = "boardingpass";
	public static final String TABLE_PRIMARY_KEY = "_id";
	private static String travel_class="travel_class";
	private static String stringform = "stringform";
	private static String firstname = "firstname";
	private static String lastname = "lastname";
	private static String PNR = "PNR";
	private static String travel_from = "travel_from";
	private static String travel_to = "travel_to";
	private static String carrier = "carrier";
	private static String flight_no = "flight_no";
	private static String julian_date = "julian_date";
	private static String compartment_code = "compartment_code";
	private static String seat = "seat";
	private static String departure = "departure";
	private static String arrival="arrival";
	private static String codetype="codetype";
	private static String id="id";
	private static String travel_from_name="travel_from_name";
	private static String travel_to_name="travel_to_name";
	private static String carrier_name="carrier_name";
	private static String deletestate="deletestate";
	
	private static final String CREATE_TABLE_EDUCATION_LIST = "create table " + TABLE_BOARDING_PASS_LIST + " ( "
			+ TABLE_PRIMARY_KEY + " integer primary key autoincrement, "
			+ travel_class + " text, " 
			+ stringform + " text, " 
			+ firstname + " text, " 
			+ lastname + " text, "
			+ PNR + " text, "
			+ travel_from + " text, "
			+ travel_to + " text, "
			+ carrier + " text, "
			+ flight_no + " text, "
			+ julian_date + " text, "
			+ compartment_code + " text, "
			+ seat + " text, "
			+ departure + " text, "
			+ arrival + " text, "
			+ codetype + " text, "
			+ travel_from_name + " text, "
			+ travel_to_name + " text, "
			+ carrier_name + " text, "
			+ deletestate + " text, "
			+ id + " text);";
	public static long insert(SQLiteDatabase db, BoardingPass boardingPass) throws SQLException {
		ContentValues cv = new ContentValues();
		cv.put(travel_class , boardingPass.getTravel_class());
		cv.put(stringform , boardingPass.getStringform());
		cv.put(firstname , boardingPass.getFirstname());
		cv.put(lastname , boardingPass.getLastname());
		cv.put(PNR , boardingPass.getPNR());
		cv.put(travel_from , boardingPass.getTravel_from());
		cv.put(travel_to , boardingPass.getTravel_to());
		cv.put(carrier , boardingPass.getCarrier());
		cv.put(flight_no , boardingPass.getFlight_no());
		cv.put(julian_date , boardingPass.getJulian_date());
		cv.put(compartment_code , boardingPass.getCompartment_code());
		cv.put(seat , boardingPass.getSeat());
		cv.put(departure , boardingPass.getDeparture());
		cv.put(arrival , boardingPass.getArrival());
		cv.put(codetype , boardingPass.getCodetype());
		cv.put(id , boardingPass.getId());
		cv.put(travel_from_name , boardingPass.getTravel_from_name());
		cv.put(travel_to_name , boardingPass.getTravel_to_name());
		cv.put(carrier_name , boardingPass.getCarrier_name());
		cv.put(deletestate , ""+boardingPass.getDeletestate());

		return db.insert(TABLE_BOARDING_PASS_LIST, null, cv);
	}
	public static void createTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_EDUCATION_LIST);
	}
	public static void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOARDING_PASS_LIST);
	}
	public static List<BoardingPass> retrieve(SQLiteDatabase db) throws SQLException {
		List<BoardingPass> boardingPasslistlist =new ArrayList<BoardingPass>();
		Cursor c = db.query(TABLE_BOARDING_PASS_LIST, null, null, null, null, null, null);
		if(c != null && c.getCount() >= 0){
			c.moveToFirst();
			while(!c.isAfterLast()){
				String travel_class_local = c.getString(c.getColumnIndex(travel_class));
				String stringform_local = c.getString(c.getColumnIndex(stringform));
				String firstname_local = c.getString(c.getColumnIndex(firstname));
				String  lastname_local = c.getString(c.getColumnIndex( lastname));
				String PNR_local = c.getString(c.getColumnIndex(PNR));
				String travel_from_local = c.getString(c.getColumnIndex(travel_from));
				String travel_to_local = c.getString(c.getColumnIndex(travel_to));
				String carrier_local = c.getString(c.getColumnIndex(carrier));
				String flight_no_local = c.getString(c.getColumnIndex(flight_no));
				String julian_date_local = c.getString(c.getColumnIndex(julian_date));
				String compartment_code_local = c.getString(c.getColumnIndex(compartment_code));
				String seat_local = c.getString(c.getColumnIndex(seat));
				String departure_local = c.getString(c.getColumnIndex(departure));
				String arrival_local = c.getString(c.getColumnIndex(arrival));
				String codetype_local = c.getString(c.getColumnIndex(codetype));
				String travel_from_name_local =c.getString(c.getColumnIndex(travel_from_name));
				String travel_to_name_local =c.getString(c.getColumnIndex(travel_to_name));
				String carrier_name_local =c.getString(c.getColumnIndex(carrier_name));
				boolean deletestate_local =Boolean.parseBoolean(c.getString(c.getColumnIndex(deletestate)));
				String id_local =c.getString(c.getColumnIndex(id));
				BoardingPass bpass=new BoardingPass(travel_class_local,stringform_local,firstname_local,lastname_local,PNR_local,travel_from_local,
						travel_to_local,carrier_local,flight_no_local,julian_date_local,compartment_code_local,
						seat_local,departure_local,arrival_local,codetype_local,id_local,travel_from_name_local
						,travel_to_name_local,carrier_name_local,deletestate_local);
				boardingPasslistlist.add(bpass);

				c.moveToNext();
			}
		}
		return boardingPasslistlist;
	}
	public static long update(SQLiteDatabase db,BoardingPass boardingPass) throws SQLException {
		ContentValues cv = new ContentValues();
		cv.put(travel_class , boardingPass.getTravel_class());
		cv.put(stringform , boardingPass.getStringform());
		cv.put(firstname , boardingPass.getFirstname());
		cv.put(lastname , boardingPass.getLastname());
		cv.put(PNR , boardingPass.getPNR());
		cv.put(travel_from , boardingPass.getTravel_from());
		cv.put(travel_to , boardingPass.getTravel_to());
		cv.put(carrier , boardingPass.getCarrier());
		cv.put(flight_no , boardingPass.getFlight_no());
		cv.put(julian_date , boardingPass.getJulian_date());
		cv.put(compartment_code , boardingPass.getCompartment_code());
		cv.put(seat , boardingPass.getSeat());
		cv.put(departure , boardingPass.getDeparture());
		cv.put(arrival , boardingPass.getArrival());
		cv.put(codetype , boardingPass.getCodetype());
		cv.put(id , boardingPass.getId());
		cv.put(travel_from_name , boardingPass.getTravel_from_name());
		cv.put(travel_to_name , boardingPass.getTravel_to_name());
		cv.put(carrier_name , boardingPass.getCarrier_name());
		cv.put(deletestate , ""+boardingPass.getDeletestate());
		return db.update(TABLE_BOARDING_PASS_LIST, cv, stringform + " = ?",
				new String[] { String.valueOf(boardingPass.getStringform()) });
		//	return db.update(TABLE_BOARDING_PASS_LIST, cv, stringform + "= ?" + new String[] {boardingPass.getStringform()}, null); 
	}
	public static void delete(SQLiteDatabase db,BoardingPass boardingPass) throws SQLException {
		 db.delete(TABLE_BOARDING_PASS_LIST, stringform + " = ?",
				new String[] { String.valueOf(boardingPass.getStringform())});

	}
	public static boolean isExist(SQLiteDatabase db, String stringformtocheck) throws SQLException {
		boolean itemExist = false;
		Cursor c = db.query(TABLE_BOARDING_PASS_LIST, null, stringform + "= ?", new String[] {stringformtocheck}, null, null, null);
		if ((c != null) && (c.getCount() > 0)) {
			itemExist = true;
		}
		return itemExist;
	}
	public static void insertOrupdate(SQLiteDatabase db, BoardingPass boardingPass){

		if(isExist(db, boardingPass.getStringform())){
			Log.e("tag", "1");
			if(boardingPass.getId().equals("-1")){
				Log.e("tag", "2");

				update(db, boardingPass);
				Log.e("tag", "3");

			}
			else{
				Log.e("tag", "4");

				update(db, boardingPass);
				Log.e("tag", "5");

			}

		}
		else{
			Log.e("tag", "6");

			insert(db, boardingPass);
			Log.e("tag", "7");

		}
		


	}
}
