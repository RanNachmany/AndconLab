package com.gdg.andconlab;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.gdg.andconlab.models.Event;
import com.gdg.andconlab.models.Lecture;
import com.gdg.andconlab.models.Speaker;

public class DatabaseHelper extends SQLiteOpenHelper{

	public static final String DB_NAME = "db";
	public static final int DB_VERSION = 7;
	
	public static final String LECTURE_SPEAKER_PAIT_TABLE = "lecture_speaker_pair";
	public static final String PAIR_LECTURE_ID = "lecture_id";
	public static final String PAIR_SPEAKER_ID = "speaker_id";
	
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create events table
		StringBuilder sb = new StringBuilder();

		try {
		DBUtils.createTable(db, sb, 
				Event.TABLE_NAME,
				Event.COLUMN_NAME_ID, "INTEGER PRIMARY KEY ",
				Event.COLUMN_NAME_NAME, "TEXT",
				Event.COLUMN_NAME_DESCRIPTION, "TEXT",
				Event.COLUMN_NAME_START_DATE, "TEXT",
				Event.COLUMN_NAME_END_DATE, "TEXT",
				Event.COLUMN_NAME_LOGO_URL, "TEXT",
				Event.COLUMN_NAME_WEBSITE_URL, "TEXT");
		
		//create lectures table
		DBUtils.createTable(db, sb, 
				Lecture.TABLE_NAME,
				Lecture.COLUMN_NAME_ID, "INTEGER PRIMARY KEY ",
				Lecture.COLUMN_NAME_DESCRIPTION, "TEXT",
				Lecture.COLUMN_NAME_DURATION, "TEXT",
				Lecture.COLUMN_NAME_EVENT_ID, "INTEGER",
				Lecture.COLUMN_NAME_NAME, "TEXT", 
				Lecture.COLUMN_NAME_SLIDES_URL, "TEXT",
				Lecture.COLUMN_NAME_VIDEO_URL, "TEXT",
				Lecture.COLUMN_NAME_YOOUTBE_ASSET_ID, "TEXT");
		
		//create speakers table
		DBUtils.createTable(db, sb, 
				Speaker.TABLE_NAME,
				Speaker.COLUMN_NAME_ID, "INTEGER PRIMARY KEY",
				Speaker.COLUMN_FIRST_NAME, "TEXT",
				Speaker.COLUMN_LAST_NAME, "TEXT",
				Speaker.COLUMN_NAME_BIO, "TEXT",
				Speaker.COLUMN_NAME_IMAGE_URL, "TEXT");
		
		
		//create lecture <-> speaker pair table
		DBUtils.createTable(db, sb, LECTURE_SPEAKER_PAIT_TABLE, 
				PAIR_LECTURE_ID, "TEXT" ,
				PAIR_SPEAKER_ID, "TEXT");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		StringBuilder sb = new StringBuilder();
		DBUtils.dropTable(db,sb , Event.TABLE_NAME);
		DBUtils.dropTable(db,sb , Lecture.TABLE_NAME);
		DBUtils.dropTable(db,sb , Speaker.TABLE_NAME);
		DBUtils.dropTable(db,sb , LECTURE_SPEAKER_PAIT_TABLE);
		onCreate(db);
	}
}
