package com.example.test24;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	public MySQLiteOpenHelper(Context context){
		super(context,"mindora.sqlite3",null,1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 自動生成されたメソッド・スタブ
		db.execSQL("CREATE TABLE IF NOT EXISTS " +
					"Member (memberID TEXT PRIMARY KEY NOT NULL , Password TEXT NOT NULL , username TEXT NOT NULL , birthyear TEXT NOT NULL , seibetu TEXT NOT NULL);");

		db.execSQL("CREATE TABLE IF NOT EXISTS " +
					"Spot (title TEXT PRIMARY KEY NOT NULL ,  location TEXT NOT NULL ,  comment TEXT NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自動生成されたメソッド・スタブ
		db.execSQL("drop table Member");
		onCreate(db);
		db.execSQL("drop table Spot");
		onCreate(db);
	}


	public void insertMember(SQLiteDatabase db, String inputID, String inputpass, String inputname, String inputyear, String inputseibetu){
		String sqlstr = "insert into Member (memberID,Password,username,birthyear,seibetu) values('" + inputID + "','" + inputpass + "','" + inputname + "','" + inputyear + "','" + inputseibetu + "');";
			try{
				db.beginTransaction();
				db.execSQL(sqlstr);
				db.setTransactionSuccessful();
			}catch(SQLException e){
				Log.e("ERROR", e.toString());
			}finally{
				db.endTransaction();
			}
	return;
	}

	public String selectMember(SQLiteDatabase db, String inputloginID, String inputloginpass) {

		String stSelect = null;
		String sqlstr = "SELECT memberID,username FROM Member WHERE memberID = '" + inputloginID + "' AND password = '" + inputloginpass + "';";
		try{
			SQLiteCursor cursor = (SQLiteCursor)db.rawQuery(sqlstr, null);
			if(cursor.getCount()!=0){
				cursor.moveToFirst();
				stSelect = cursor.getString(1);
				}
			cursor.close();
		}catch(SQLException e){
			Log.e("ERROR",e.toString());
		}finally{
		}
	return stSelect;
	}

	public void insertSpot(SQLiteDatabase db, String inputTitle,String inputComment) {
		// TODO 自動生成されたメソッド・スタブ
		String sqlstr = "insert into Spot (title,comment) values('" + inputTitle + "','" + inputComment + "');";
		try{
			db.beginTransaction();
			db.execSQL(sqlstr);
			db.setTransactionSuccessful();
		}catch(SQLException e){
			Log.e("ERROR", e.toString());
	}finally{
			db.endTransaction();
		}
	return;
	}
}
