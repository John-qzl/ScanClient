package com.example.user.scandemo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	//final private String CREATE_TABLE_SQL = "create table dict(_id integer primary key autoincrement, word, detail)";

	final private int saveCount = 1000;
	final private String CREATE_TABLE_SQL = "create table barcode(_id integer primary key autoincrement,result TEXT);";
	public DatabaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_SQL);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void insert(SQLiteDatabase db,String data){
		Cursor cursor = db.rawQuery("select * from barcode;", null);
		if(cursor!=null){
			int count = cursor.getCount();
			int _id = 0;
			while(count>=saveCount){
				cursor = db.rawQuery("select * from barcode;", null);
				count = cursor.getCount();
				cursor.moveToFirst();
				_id = cursor.getInt(0);
				db.execSQL("delete from barcode where _id="+ _id);
				count--;
			}
		}
		
		insertData(db,data);	
	}
	
	private void insertData(SQLiteDatabase db,String data){
		if(data!=null){
			db.execSQL("insert into barcode(result) VALUES(?);",new String[]{data});
		}
	}
	public Cursor queryAll(SQLiteDatabase db){
		Cursor cursor = db.rawQuery("select * from barcode;", null);
		return cursor;
	}
}
