package com.ckj.sticker.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MaterialDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "materials.db";
	public static final String MATERIAL_TABLE_NAME = "material_table";

	private static final int DATABASE_VERSION = 1;

	public MaterialDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("create table " + MATERIAL_TABLE_NAME
				+ "(_id  integer primary key, " + "name text,"
				+ "type integer)");
        db.execSQL("insert into " + MATERIAL_TABLE_NAME + "(name,type)values('zhuangban',1)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("drop table if exists" + MATERIAL_TABLE_NAME);

		onCreate(db);
	}

}
