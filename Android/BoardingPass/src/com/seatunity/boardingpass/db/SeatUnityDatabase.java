package com.seatunity.boardingpass.db;

import java.util.ArrayList;
import java.util.List;

import com.seatunity.model.BoardingPass;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class SeatUnityDatabase {
	private static final String TAG = SeatUnityDatabase.class.getSimpleName();
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private Context mContext;
	private static final String DATABASE_NAME = "lipberry_db2";
	private static final int DATABASE_VERSION = 3;
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
	public SeatUnityDatabase(Context ctx) {
		mContext = ctx;
	}
	public SeatUnityDatabase open() throws SQLException {
		dbHelper = new DatabaseHelper(mContext);
		db = dbHelper.getWritableDatabase();
		return this;
	}
	public void close() {
		dbHelper.close();
	}

	public void droptableBoardingPassDbManager() {

		BoardingPassDbManager.dropTable(this.db);

	}
	public void createtableBoardingPassDbManager() {
		BoardingPassDbManager.createTable(db);
	}
	public void insertOrUpdateBoardingPassList(ArrayList<BoardingPass>boardingPasseslist) {
		for (int i=0;i<boardingPasseslist.size();i++){
			insertOrUpdateBoardingPass(boardingPasseslist.get(i));
		}

	}
	public void insertOrUpdateBoardingPass(BoardingPass boardingPass) {
		BoardingPassDbManager.insertOrupdate(this.db, boardingPass);
	}
	public List<BoardingPass> retrieveBoardingPassList() {
		return BoardingPassDbManager.retrieve(this.db);
	}
}
