package com.seatunity.boardingpass.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.seatunity.model.BoardingPass;

/**
 * Class containing the boarding-pass table operations
 * 
 * @author Sumon
 * 
 */
public class BoardingPassDbManager {
	private static final String TAG = BoardingPassDbManager.class.getSimpleName();

	private static String TABLE_BOARDING_PASS_LIST = "boardingpass";
	private static final String TABLE_PRIMARY_KEY = "_id";
	private static final String KEY_TRAVEL_CLASS = "travel_class";
	private static final String KEY_STRING_FORM = "stringform";
	private static final String KEY_FIRSTNAME = "firstname";
	private static final String KEY_LASTNAME = "lastname";
	private static final String KEY_PNR = "PNR";
	private static final String KEY_TRAVEL_FROM = "travel_from";
	private static final String KEY_TRAVEL_TO = "travel_to";
	private static final String KEY_CARRIER = "carrier";
	private static final String KEY_FLIGHT_NO = "flight_no";
	private static final String KEY_JULIAN_DATE = "julian_date";
	private static final String KEY_COMPARTMENT_CODE = "compartment_code";
	private static final String KEY_SEAT = "seat";
	private static final String KEY_DEPARTURE = "departure";
	private static final String KEY_ARRIVAL = "arrival";
	private static final String KEY_CODETYPE = "codetype";
	private static final String KEY_ID = "id";
	private static final String KEY_TRAVEL_FROM_NAME = "travel_from_name";
	private static final String KEY_TRAVEL_TO_NAME = "travel_to_name";
	private static final String KEY_CARRIER_NAME = "carrier_name";
	private static final String KEY_DELETESTATE = "deletestate";

	private static final String CREATE_TABLE_BOARDING_LIST = "create table if not exists " + TABLE_BOARDING_PASS_LIST
			+ " ( " + TABLE_PRIMARY_KEY + " integer primary key autoincrement, " + KEY_TRAVEL_CLASS + " text, "
			+ KEY_STRING_FORM + " text, " + KEY_FIRSTNAME + " text, " + KEY_LASTNAME + " text, " + KEY_PNR + " text, "
			+ KEY_TRAVEL_FROM + " text, " + KEY_TRAVEL_TO + " text, " + KEY_CARRIER + " text, " + KEY_FLIGHT_NO
			+ " text, " + KEY_JULIAN_DATE + " text, " + KEY_COMPARTMENT_CODE + " text, " + KEY_SEAT + " text, "
			+ KEY_DEPARTURE + " text, " + KEY_ARRIVAL + " text, " + KEY_CODETYPE + " text, " + KEY_TRAVEL_FROM_NAME
			+ " text, " + KEY_TRAVEL_TO_NAME + " text, " + KEY_CARRIER_NAME + " text, " + KEY_DELETESTATE + " text, "
			+ KEY_ID + " text);";

	/**
	 * Inserts the boarding-pass as a new one in the DB.
	 * 
	 * @param db
	 * @param boardingPass
	 * @return
	 * @throws SQLException
	 */
	private static long insert(SQLiteDatabase db, BoardingPass boardingPass) throws SQLException {
		ContentValues cv = new ContentValues();
		cv.put(KEY_TRAVEL_CLASS, boardingPass.getTravel_class());
		cv.put(KEY_STRING_FORM, boardingPass.getStringform());
		cv.put(KEY_FIRSTNAME, boardingPass.getFirstname());
		cv.put(KEY_LASTNAME, boardingPass.getLastname());
		cv.put(KEY_PNR, boardingPass.getPNR());
		cv.put(KEY_TRAVEL_FROM, boardingPass.getTravel_from());
		cv.put(KEY_TRAVEL_TO, boardingPass.getTravel_to());
		cv.put(KEY_CARRIER, boardingPass.getCarrier());
		cv.put(KEY_FLIGHT_NO, boardingPass.getFlight_no());
		cv.put(KEY_JULIAN_DATE, boardingPass.getJulian_date());
		cv.put(KEY_COMPARTMENT_CODE, boardingPass.getCompartment_code());
		cv.put(KEY_SEAT, boardingPass.getSeat());
		cv.put(KEY_DEPARTURE, boardingPass.getDeparture());
		cv.put(KEY_ARRIVAL, boardingPass.getArrival());
		cv.put(KEY_CODETYPE, boardingPass.getCodetype());
		cv.put(KEY_ID, boardingPass.getId());
		cv.put(KEY_TRAVEL_FROM_NAME, boardingPass.getTravel_from_name());
		cv.put(KEY_TRAVEL_TO_NAME, boardingPass.getTravel_to_name());
		cv.put(KEY_CARRIER_NAME, boardingPass.getCarrier_name());
		cv.put(KEY_DELETESTATE, "" + boardingPass.getDeletestate());

		return db.insert(TABLE_BOARDING_PASS_LIST, null, cv);
	}

	/**
	 * Creates the table containing the all boarding-passes.
	 * 
	 * @param db
	 */
	public static void createTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_BOARDING_LIST);
	}

	/**
	 * Drops the table containing the all boarding-passes.
	 * 
	 * @param db
	 */
	public static void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOARDING_PASS_LIST);
	}

	/**
	 * Sends a list containing all the boarding-pass existing in the DB.
	 * 
	 * @param db
	 * @return
	 * @throws SQLException
	 */
	public static List<BoardingPass> retrieve(SQLiteDatabase db) throws SQLException {
		List<BoardingPass> boardingPasslistlist = new ArrayList<BoardingPass>();
		Cursor c = db.query(TABLE_BOARDING_PASS_LIST, null, null, null, null, null, null);
		Log.i(TAG, "All b-pass count in local DB: " + c.getCount());
		if (c != null && c.getCount() >= 0) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				String travel_class_local = c.getString(c.getColumnIndex(KEY_TRAVEL_CLASS));
				String stringform_local = c.getString(c.getColumnIndex(KEY_STRING_FORM));
				String firstname_local = c.getString(c.getColumnIndex(KEY_FIRSTNAME));
				String lastname_local = c.getString(c.getColumnIndex(KEY_LASTNAME));
				String PNR_local = c.getString(c.getColumnIndex(KEY_PNR));
				String travel_from_local = c.getString(c.getColumnIndex(KEY_TRAVEL_FROM));
				String travel_to_local = c.getString(c.getColumnIndex(KEY_TRAVEL_TO));
				String carrier_local = c.getString(c.getColumnIndex(KEY_CARRIER));
				String flight_no_local = c.getString(c.getColumnIndex(KEY_FLIGHT_NO));
				String julian_date_local = c.getString(c.getColumnIndex(KEY_JULIAN_DATE));
				String compartment_code_local = c.getString(c.getColumnIndex(KEY_COMPARTMENT_CODE));
				String seat_local = c.getString(c.getColumnIndex(KEY_SEAT));
				String departure_local = c.getString(c.getColumnIndex(KEY_DEPARTURE));
				String arrival_local = c.getString(c.getColumnIndex(KEY_ARRIVAL));
				String codetype_local = c.getString(c.getColumnIndex(KEY_CODETYPE));
				String travel_from_name_local = c.getString(c.getColumnIndex(KEY_TRAVEL_FROM_NAME));
				String travel_to_name_local = c.getString(c.getColumnIndex(KEY_TRAVEL_TO_NAME));
				String carrier_name_local = c.getString(c.getColumnIndex(KEY_CARRIER_NAME));
				boolean deletestate_local = Boolean.parseBoolean(c.getString(c.getColumnIndex(KEY_DELETESTATE)));
				String id_local = c.getString(c.getColumnIndex(KEY_ID));
				BoardingPass bpass = new BoardingPass(travel_class_local, stringform_local, firstname_local,
						lastname_local, PNR_local, travel_from_local, travel_to_local, carrier_local, flight_no_local,
						julian_date_local, compartment_code_local, seat_local, departure_local, arrival_local,
						codetype_local, id_local, travel_from_name_local, travel_to_name_local, carrier_name_local,
						deletestate_local);
				boardingPasslistlist.add(bpass);

				c.moveToNext();
			}
		}
		return boardingPasslistlist;
	}

	public static List<BoardingPass> retrieveFutureList(SQLiteDatabase db) {
		Calendar cal = Calendar.getInstance();
		int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		Log.i(TAG, "retrieveFutureList : Current day of the year: " + dayOfYear);
		List<BoardingPass> boardingPasslistlist = new ArrayList<BoardingPass>();
		Cursor cursor = db.rawQuery(
				"SELECT * FROM " + TABLE_BOARDING_PASS_LIST + " WHERE " + KEY_JULIAN_DATE + " >= ?",
				new String[] { dayOfYear + "" });
		Log.d(TAG, "retrieveFutureList : got queried size: " + cursor.getCount());
		if (cursor != null && cursor.getCount() >= 0 && cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				String travel_class_local = cursor.getString(cursor.getColumnIndex(KEY_TRAVEL_CLASS));
				String stringform_local = cursor.getString(cursor.getColumnIndex(KEY_STRING_FORM));
				String firstname_local = cursor.getString(cursor.getColumnIndex(KEY_FIRSTNAME));
				String lastname_local = cursor.getString(cursor.getColumnIndex(KEY_LASTNAME));
				String PNR_local = cursor.getString(cursor.getColumnIndex(KEY_PNR));
				String travel_from_local = cursor.getString(cursor.getColumnIndex(KEY_TRAVEL_FROM));
				String travel_to_local = cursor.getString(cursor.getColumnIndex(KEY_TRAVEL_TO));
				String carrier_local = cursor.getString(cursor.getColumnIndex(KEY_CARRIER));
				String flight_no_local = cursor.getString(cursor.getColumnIndex(KEY_FLIGHT_NO));
				String julian_date_local = cursor.getString(cursor.getColumnIndex(KEY_JULIAN_DATE));
				String compartment_code_local = cursor.getString(cursor.getColumnIndex(KEY_COMPARTMENT_CODE));
				String seat_local = cursor.getString(cursor.getColumnIndex(KEY_SEAT));
				String departure_local = cursor.getString(cursor.getColumnIndex(KEY_DEPARTURE));
				String arrival_local = cursor.getString(cursor.getColumnIndex(KEY_ARRIVAL));
				String codetype_local = cursor.getString(cursor.getColumnIndex(KEY_CODETYPE));
				String travel_from_name_local = cursor.getString(cursor.getColumnIndex(KEY_TRAVEL_FROM_NAME));
				String travel_to_name_local = cursor.getString(cursor.getColumnIndex(KEY_TRAVEL_TO_NAME));
				String carrier_name_local = cursor.getString(cursor.getColumnIndex(KEY_CARRIER_NAME));
				boolean deletestate_local = Boolean.parseBoolean(cursor.getString(cursor
						.getColumnIndex(KEY_DELETESTATE)));
				String id_local = cursor.getString(cursor.getColumnIndex(KEY_ID));
				BoardingPass bpass = new BoardingPass(travel_class_local, stringform_local, firstname_local,
						lastname_local, PNR_local, travel_from_local, travel_to_local, carrier_local, flight_no_local,
						julian_date_local, compartment_code_local, seat_local, departure_local, arrival_local,
						codetype_local, id_local, travel_from_name_local, travel_to_name_local, carrier_name_local,
						deletestate_local);
				boardingPasslistlist.add(bpass);

				cursor.moveToNext();
			}
		}
		return boardingPasslistlist;
	}

	public static List<BoardingPass> retrievePastList(SQLiteDatabase db) {
		Calendar cal = Calendar.getInstance();
		int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		Log.i(TAG, "retrieveFutureList : Current day of the year: " + dayOfYear);
		List<BoardingPass> boardingPasslistlist = new ArrayList<BoardingPass>();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOARDING_PASS_LIST + " WHERE " + KEY_JULIAN_DATE + " < ?",
				new String[] { dayOfYear + "" });
		Log.d(TAG, "retrievePastList : got queried size: " + cursor.getCount());
		if (cursor != null && cursor.getCount() >= 0 && cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				String travel_class_local = cursor.getString(cursor.getColumnIndex(KEY_TRAVEL_CLASS));
				String stringform_local = cursor.getString(cursor.getColumnIndex(KEY_STRING_FORM));
				String firstname_local = cursor.getString(cursor.getColumnIndex(KEY_FIRSTNAME));
				String lastname_local = cursor.getString(cursor.getColumnIndex(KEY_LASTNAME));
				String PNR_local = cursor.getString(cursor.getColumnIndex(KEY_PNR));
				String travel_from_local = cursor.getString(cursor.getColumnIndex(KEY_TRAVEL_FROM));
				String travel_to_local = cursor.getString(cursor.getColumnIndex(KEY_TRAVEL_TO));
				String carrier_local = cursor.getString(cursor.getColumnIndex(KEY_CARRIER));
				String flight_no_local = cursor.getString(cursor.getColumnIndex(KEY_FLIGHT_NO));
				String julian_date_local = cursor.getString(cursor.getColumnIndex(KEY_JULIAN_DATE));
				String compartment_code_local = cursor.getString(cursor.getColumnIndex(KEY_COMPARTMENT_CODE));
				String seat_local = cursor.getString(cursor.getColumnIndex(KEY_SEAT));
				String departure_local = cursor.getString(cursor.getColumnIndex(KEY_DEPARTURE));
				String arrival_local = cursor.getString(cursor.getColumnIndex(KEY_ARRIVAL));
				String codetype_local = cursor.getString(cursor.getColumnIndex(KEY_CODETYPE));
				String travel_from_name_local = cursor.getString(cursor.getColumnIndex(KEY_TRAVEL_FROM_NAME));
				String travel_to_name_local = cursor.getString(cursor.getColumnIndex(KEY_TRAVEL_TO_NAME));
				String carrier_name_local = cursor.getString(cursor.getColumnIndex(KEY_CARRIER_NAME));
				boolean deletestate_local = Boolean.parseBoolean(cursor.getString(cursor
						.getColumnIndex(KEY_DELETESTATE)));
				String id_local = cursor.getString(cursor.getColumnIndex(KEY_ID));
				BoardingPass bpass = new BoardingPass(travel_class_local, stringform_local, firstname_local,
						lastname_local, PNR_local, travel_from_local, travel_to_local, carrier_local, flight_no_local,
						julian_date_local, compartment_code_local, seat_local, departure_local, arrival_local,
						codetype_local, id_local, travel_from_name_local, travel_to_name_local, carrier_name_local,
						deletestate_local);
				boardingPasslistlist.add(bpass);

				cursor.moveToNext();
			}
		}
		return boardingPasslistlist;
	}

	/**
	 * Updats an existing boarding-pass with the one passed as the param.
	 * 
	 * @param db
	 * @param boardingPass
	 *            which is to be updated with.
	 * @return
	 * @throws SQLException
	 */
	public static long update(SQLiteDatabase db, BoardingPass boardingPass) throws SQLException {
		Log.d(TAG, "update : inside");
		ContentValues cv = new ContentValues();
		cv.put(KEY_TRAVEL_CLASS, boardingPass.getTravel_class());
		cv.put(KEY_STRING_FORM, boardingPass.getStringform());
		cv.put(KEY_FIRSTNAME, boardingPass.getFirstname());
		cv.put(KEY_LASTNAME, boardingPass.getLastname());
		cv.put(KEY_PNR, boardingPass.getPNR());
		cv.put(KEY_TRAVEL_FROM, boardingPass.getTravel_from());
		cv.put(KEY_TRAVEL_TO, boardingPass.getTravel_to());
		cv.put(KEY_CARRIER, boardingPass.getCarrier());
		cv.put(KEY_FLIGHT_NO, boardingPass.getFlight_no());
		cv.put(KEY_JULIAN_DATE, boardingPass.getJulian_date());
		cv.put(KEY_COMPARTMENT_CODE, boardingPass.getCompartment_code());
		cv.put(KEY_SEAT, boardingPass.getSeat());
		cv.put(KEY_DEPARTURE, boardingPass.getDeparture());
		cv.put(KEY_ARRIVAL, boardingPass.getArrival());
		cv.put(KEY_CODETYPE, boardingPass.getCodetype());
		cv.put(KEY_ID, boardingPass.getId());
		cv.put(KEY_TRAVEL_FROM_NAME, boardingPass.getTravel_from_name());
		cv.put(KEY_TRAVEL_TO_NAME, boardingPass.getTravel_to_name());
		cv.put(KEY_CARRIER_NAME, boardingPass.getCarrier_name());
		cv.put(KEY_DELETESTATE, "" + boardingPass.getDeletestate());
		return db.update(TABLE_BOARDING_PASS_LIST, cv, KEY_STRING_FORM + " = ?",
				new String[] { String.valueOf(boardingPass.getStringform()) });
		// return db.update(TABLE_BOARDING_PASS_LIST, cv, stringform + "= ?" +
		// new String[] {boardingPass.getStringform()}, null);
	}

	/**
	 * Deletes the passed boarding-pass from the DB.
	 * 
	 * @param db
	 * @param boardingPass
	 * @throws SQLException
	 */
	public static void delete(SQLiteDatabase db, BoardingPass boardingPass) throws SQLException {
		db.delete(TABLE_BOARDING_PASS_LIST, KEY_STRING_FORM + " = ?",
				new String[] { String.valueOf(boardingPass.getStringform()) });
	}

	/**
	 * @param db
	 * @param stringformtocheck
	 * @return true if the barcode exists in the currnt DB; false otherwise.
	 * @throws SQLException
	 */
	public static boolean isExist(SQLiteDatabase db, String stringformtocheck) throws SQLException {
		boolean itemExist = false;
		Cursor c = db.query(TABLE_BOARDING_PASS_LIST, null, KEY_STRING_FORM + "= ?",
				new String[] { stringformtocheck }, null, null, null);
		if ((c != null) && (c.getCount() > 0)) {
			itemExist = true;
		}
		return itemExist;
	}

	/**
	 * Decides whether to insert the boarding-pass as a new one or to update a
	 * previous one.
	 * 
	 * @param db
	 * @param boardingPass
	 */
	public static void insertOrupdate(SQLiteDatabase db, BoardingPass boardingPass) {
		Log.d(TAG, "insertOrupdate : inside");
		if (isExist(db, boardingPass.getStringform())) {
			Log.e(TAG, "boardingPass : exists");
			if (boardingPass.getId().equals("-1")) {
				Log.e(TAG, "boardingPass.getId().equals('-1')");
				long r = update(db, boardingPass);
				Log.e(TAG, "update process returned: " + r);
			} else {
				Log.e(TAG, "boardingPass ID >-1");
				long ret = update(db, boardingPass);
				Log.e(TAG, "update process returned: " + ret);
			}
		} else {
			Log.e(TAG, "inserting ... ");
			long ret = insert(db, boardingPass);
			Log.e(TAG, "inserted = " + ret);
		}
	}
}
