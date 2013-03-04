package com.gdg.andconlab;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.gdg.andconlab.models.Event;
import com.gdg.andconlab.models.Lecture;
import com.gdg.andconlab.models.Speaker;
import com.gdg.andconlab.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for DB related operations
 *
 * @author Amir Lazarovich
 * @version 0.1
 */
public class DBUtils {
	private static final String TAG = "DBUtils";
	//select lecturerImage from assets where lectureVideoId='Y4UMzOWcgGQ';
	/**
	 * Create DB table
	 *
	 * @param db        Reference to the underlying database
	 * @param sb        Clears any existing values before starting to append new values
	 * @param tableName The name of the DB table
	 * @param columns   Tuples of column names and their corresponding type and properties. This field must be even for that same
	 *                  reason. I.e. "my_column", "INTEGER PRIMARY KEY AUTOINCREMENT", "my_second_column", "VARCHAR(255)"
	 */
	public static void createTable(SQLiteDatabase db, StringBuilder sb, String tableName, String... columns) {
		if (columns.length % 2 != 0) {
			throw new IllegalArgumentException(
					"Columns length should be even since each column is followed by its corresponding type and properties");
		}

		StringUtils.clearBuffer(sb);
		// Prepare table
		sb.append("CREATE TABLE ");
		sb.append(tableName);
		sb.append(" (");

		// Parse all columns
		int length = columns.length;
		for (int i = 0; i < length; i += 2) {
			sb.append(columns[i]);
			sb.append(" ");
			sb.append(columns[i + 1]);

			if (i + 2 < length) {
				// Append comma only if this isn't the last column
				sb.append(", ");
			}
		}

		sb.append(");");

		// Create table
		db.execSQL(sb.toString());
	}

	/**
	 * Drop table if exists in given database
	 *
	 * @param db        Reference to the underlying database
	 * @param tableName The table name of which we try to drop
	 */
	public static void dropTable(SQLiteDatabase db, String tableName) {
		dropTable(db, new StringBuilder(), tableName);
	}

	/**
	 * Drop table if exists in given database
	 *
	 * @param db        Reference to the underlying database
	 * @param sb        Clears any existing values before starting to append new values
	 * @param tableName The table name of which we try to drop
	 */
	public static void dropTable(SQLiteDatabase db, StringBuilder sb, String tableName) {
		StringUtils.clearBuffer(sb);
		sb.append("DROP TABLE IF EXISTS ");
		sb.append(tableName);

		// Drop table
		db.execSQL(sb.toString());
	}

	/**
	 * Stores events and their lectures and speakers in db
	 * @param db - Writeable SQLITE DB
	 * @param events - events to be stored
	 * @return
	 */
	public static boolean storeEvents(SQLiteDatabase db, List<Event> events) {

		db.beginTransaction();
		ContentValues cv;
		List<Lecture> lectures;
		List<Speaker> speakers;
		long eventId;
		for (Event event : events) {
			//store event
			cv = event.getContentValues();
			db.replace(Event.TABLE_NAME, null, cv);
			eventId = event.getId();
			//loop through all lectures
			lectures = event.getLectures();
			for (Lecture lecture : lectures) {
				//set event id
				lecture.setEventId(eventId);
				cv = lecture.getContentValues();
				db.replace(Lecture.TABLE_NAME, null, cv);

				//remove all speakers from this lecture
				cleaerLectureSpeakers(db, lecture);
				//loop through all the speakers
				speakers = lecture.getSpeakers();
				for (Speaker speaker : speakers) {
					
					//store speaker in db
					cv = speaker.getContentValues();
//					db.replace(Speaker.TABLE_NAME, null, cv);
					db.insertWithOnConflict(Speaker.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);

					//add speaker to this lecture
					addSpeakerToLecture(db, speaker, lecture);
				}
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();

		return true;
	}

	private static void addSpeakerToLecture(SQLiteDatabase db, Speaker speaker, Lecture lecture) {
		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.PAIR_LECTURE_ID, lecture.getId());
		cv.put(DatabaseHelper.PAIR_SPEAKER_ID, speaker.getId());

		db.insert(DatabaseHelper.LECTURE_SPEAKER_PAIR_TABLE, null, cv);


	}

	private static void cleaerLectureSpeakers(SQLiteDatabase db, Lecture lecture) {
		db.delete(DatabaseHelper.LECTURE_SPEAKER_PAIR_TABLE, DatabaseHelper.PAIR_LECTURE_ID + "=" + lecture.getId(), null);
	}

	/**
	 * Fetch all events from DB
	 * @param db
	 * @return cursor holding id, name, description and logo url
	 */
	public static Cursor getEventsCurosr(SQLiteDatabase db) {

		String[] cols = new String[] {
				Event.COLUMN_NAME_ID,
				Event.COLUMN_NAME_NAME,
				Event.COLUMN_NAME_DESCRIPTION,
				Event.COLUMN_NAME_LOGO_URL
		};
		Cursor c;

		c = db.query(Event.TABLE_NAME, cols, null, null, null, null, Event.COLUMN_NAME_START_DATE + " DESC");

		return c;
	}

	public static Cursor getLecturesByEventId(SQLiteDatabase db, long eventId) {
		String[] cols = new String[] {
				Lecture.COLUMN_NAME_ID,
				Lecture.COLUMN_NAME_NAME,
				Lecture.COLUMN_NAME_DESCRIPTION,
		};
		Cursor c;

		c = db.query(Lecture.TABLE_NAME, cols, Lecture.COLUMN_NAME_EVENT_ID +" = " +eventId, null, null, null, Lecture.COLUMN_NAME_NAME);

		return c;	
	}

	public static Cursor getAllLectures (SQLiteDatabase db) {
		String[] cols = new String[] {
				Lecture.COLUMN_NAME_ID,
				Lecture.COLUMN_NAME_NAME,
				Lecture.COLUMN_NAME_DESCRIPTION,
				Lecture.COLUMN_NAME_DURATION
		};

		return db.query(Lecture.TABLE_NAME, cols, null, null, null, null, Lecture.COLUMN_NAME_EVENT_ID + " DESC");
	}

	/**
	 * Fetches a lecture from db
	 * @param db
	 * @param id
	 * @return Lecture object or null if no lecture found. 
	 */
	public static Lecture getLectureById (SQLiteDatabase db, long id) {
		Cursor c = db.query(Lecture.TABLE_NAME, null, Lecture.COLUMN_NAME_ID + "=" + id, null, null, null, null);
		Lecture lecture = new Lecture();
		if (c.moveToNext()) {
			lecture.buildFromCursor(c);
			c.close();
			return lecture;
		}
		return null;
	}

	public static ArrayList<Speaker> getSpeakersByLectureId (SQLiteDatabase db, long id) {
		ArrayList<Speaker> speakers = new ArrayList<Speaker>();

		String select = "SELECT * FROM " + Speaker.TABLE_NAME +" WHERE " + Speaker.COLUMN_NAME_ID +" IN ("+
				" SELECT " + DatabaseHelper.PAIR_SPEAKER_ID + " FROM " + DatabaseHelper.LECTURE_SPEAKER_PAIR_TABLE+ " WHERE " + DatabaseHelper.PAIR_LECTURE_ID + " = " +id +")";

		Cursor c = db.rawQuery(select, null);

		Speaker speaker;
		while (c.moveToNext()) {
			speaker = new Speaker();
			speaker.buildFromCursor(c);
			speakers.add(speaker);
		}

		c.close();
		return speakers;
	}

	/**
	 * Creates a Content Uri out of given authority and table name at the following manner: <br/>
	 * content://AUTHORITY/PATH
	 *
	 * @param sb
	 * @param authority
	 * @param path
	 * @return
	 */
	/*public static Uri createContentUri(StringBuilder sb, String authority, String... path) {
        StringUtils.clearBuffer(sb);

        sb.append("content://");
        sb.append(authority);
        sb.append("/");

        for (String part : path) {
            sb.append(part);
        }

        return Uri.parse(sb.toString());
    }*/

	/**
	 * Appends the given ID to the end of the path.
	 *
	 * @param builder to append the ID to
	 * @param id to append
	 *
	 * @return the given builder
	 */
	/*public static Uri.Builder appendId(Uri.Builder builder, String id) {
        return builder.appendEncodedPath(id);
    }*/

	/**
	 * Appends the given ID to the end of the path.
	 *
	 * @param contentUri to start with
	 * @param id to append
	 *
	 * @return a new URI with the given ID appended to the end of the path
	 */
	/*public static Uri withAppendedId(Uri contentUri, String id) {
        return appendId(contentUri.buildUpon(), id).build();
    }*/

	/**
	 * Safely apply batch operations on a content resolver
	 *
	 * @param context
	 * @param authority
	 * @param ops
	 */
	/*public static void sApplyBatch(Context context, String authority, ArrayList<ContentProviderOperation> ops) {
        try {
            context.getContentResolver().applyBatch(authority, ops);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't apply batch operations on content resolver", e);
        }
    }*/
}
