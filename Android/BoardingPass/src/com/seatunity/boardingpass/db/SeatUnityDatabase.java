package com.seatunity.boardingpass.db;

import java.util.ArrayList;
import java.util.List;

import com.seatunity.model.BoardingPass;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class containing the DatabaseHelper class & other DB operations
 * 
 * @author Sumon
 * 
 */
public class SeatUnityDatabase {
	private static final String TAG = SeatUnityDatabase.class.getSimpleName();
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private Context mContext;
	private static final String DATABASE_NAME = "lipberry_db2";
	private static final int DATABASE_VERSION = 4;

	/**
	 * The DB helper class of the app containing only the onCreate & onUpgrade
	 * over-ridden methods of {@link SQLiteOpenHelper}
	 * 
	 * @author Sumon
	 * 
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			BoardingPassDbManager.createTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			BoardingPassDbManager.dropTable(db);
			onCreate(db);
		}
	}

	/**
	 * @param ctx
	 */
	public SeatUnityDatabase(Context ctx) {
		Log.i(TAG,"constructor : inside");
		mContext = ctx;
	}

	/**
	 * @return a writable DB as a {@link SeatUnityDatabase} instance.
	 * @throws SQLException
	 */
	public SeatUnityDatabase open() throws SQLException {
		dbHelper = new DatabaseHelper(mContext);
		Log.e(TAG, "ab " + mContext + "  " + dbHelper);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Closes the db helper instance, as well as the DB.
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Calls {@code dropTable()} method of {@link BoardingPassDbManager }
	 */
	public void droptableBoardingPassDbManager() {
		BoardingPassDbManager.dropTable(this.db);
	}

	/**
	 * Calls {@code createTable()} method of {@link BoardingPassDbManager } to
	 * create a new table.
	 */
	public void createtableBoardingPassDbManager() {
		BoardingPassDbManager.createTable(db);
	}

	/**
	 * Iteratively calls the insertOrupdate() method of
	 * {@link BoardingPassDbManager } for all the {@link BoardingPass} objects of
	 * the passed list.
	 * 
	 * @param boardingPasseslist
	 */
	public void insertOrUpdateBoardingPassList(ArrayList<BoardingPass> boardingPasseslist) {
		for (int i = 0; i < boardingPasseslist.size(); i++) {
			insertOrUpdateBoardingPass(boardingPasseslist.get(i));
		}

	}

	/**
	 * Iteratively calls the delete() method of {@link BoardingPassDbManager }
	 * for all the {@link BoardingPass} objects of the passed list to delete th
	 * objects from the DB.
	 * 
	 * @param boardingPasseslist
	 */
	public void DeleteBoardingPass(BoardingPass boardingPass) {
		BoardingPassDbManager.delete(this.db, boardingPass);
	}

	/**
	 * Calls the insertOrupdate() method of {@link BoardingPassDbManager }.
	 * 
	 * @param boardingPasseslist
	 */
	public void insertOrUpdateBoardingPass(BoardingPass boardingPass) {
		Log.d(TAG,"insertOrUpdateBoardingPass : inside");
		BoardingPassDbManager.insertOrupdate(this.db, boardingPass);
	}
	/**
	 * Calls the retrieve() method of {@link BoardingPassDbManager }.
	 * 
	 * @return a list containing all the boarding-passes in the DB.
	 */
	public List<BoardingPass> retrieveBoardingPassList() {
		return BoardingPassDbManager.retrieve(this.db);
	}
	/**
	 * Calls the {@link BoardingPassDbManager#retrieveFutureList(SQLiteDatabase)} method.
	 * 
	 * @return a list containing all the future boarding-passes in the DB.
	 */
	public List<BoardingPass> retrieveFutureBoardingPassList() {
		return BoardingPassDbManager.retrieveFutureList(this.db);
	}
}
