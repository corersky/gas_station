package com.linkage.gas_station.util;

import com.linkage.gas_station.model.OutputInfoModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GetConnData extends SQLiteOpenHelper {
	
	static final String DATABASE_NAME="Gas_db";
	static final int DATABASE_VERSION=1;
	
	static final String _ID="_id";
	
	static final String MONITOR_TABLE="monitor";
	static final String MONITOR_INFO="info";

	public GetConnData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table if not exists "+MONITOR_TABLE+"("+_ID+" integer primary key autoincrement not null, "+MONITOR_INFO+" blob)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 设置最新数据
	 * @param model
	 */
	public synchronized void saveMonitor(OutputInfoModel model) {		
		SQLiteDatabase db_read=this.getReadableDatabase();
		Cursor cs=db_read.query(MONITOR_TABLE, new String[]{MONITOR_INFO}, null, null, null, null, null);
		cs.moveToFirst();
		SQLiteDatabase db_write=this.getWritableDatabase();
		ContentValues values=new ContentValues(1);
		byte[] bytes=Util.serialize(model);
		values.put(MONITOR_INFO, bytes);
		db_write.beginTransaction();
		if(cs.getCount()==0) {
			db_write.insert(MONITOR_TABLE, null, values);			
		}
		else {
			db_write.update(MONITOR_TABLE, values, null, null);
		}
		db_write.setTransactionSuccessful();
		db_write.endTransaction();
		cs.close();
		db_write.close();
		db_read.close();
	}
	
	/*
	 * 后取最新数据
	 */
	public synchronized OutputInfoModel getMonitor() {
		OutputInfoModel model=null;
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor cs=db.query(MONITOR_TABLE, new String[]{MONITOR_INFO}, null, null, null, null, null);
		cs.moveToFirst();
		if(cs.getCount()==0) {
			model=null;
		}
		else {
			model=Util.deserialize(cs.getBlob(0));
		}
		cs.close();
		db.close();
		return model;
	}
	
	/**
	 * 更换号码之和删除数据
	 */
	public synchronized void deleteMonitor() {
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(MONITOR_TABLE, null, null);
		db.close();
	}

}
