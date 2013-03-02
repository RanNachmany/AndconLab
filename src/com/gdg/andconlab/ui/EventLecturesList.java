package com.gdg.andconlab.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.gdg.andconlab.DBUtils;
import com.gdg.andconlab.DatabaseHelper;
import com.gdg.andconlab.ItemsListActivity;
import com.gdg.andconlab.R;
import com.gdg.andconlab.models.Lecture;

/**
 * Activity that displays a list of all lectures in a specific event. 
 * THis activity expects to get an event id in the intent that calls it. 
 * @author Ran Nachmany
 *
 */

public class EventLecturesList extends SherlockActivity {

	public static final String EXTRA_EVENT_ID = "event_id";
	
	private static final int ILLEGAL_ID = -1; 
	
	private ListView mList;
	private long mEventId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.lectures_activity);
		
		getSupportActionBar().setHomeButtonEnabled(true);
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		
		mList = (ListView) findViewById(R.id.events_list);
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> list, View view, int position,
					long id) {
			}
		});
		
		mEventId = getIntent().getLongExtra(EXTRA_EVENT_ID, ILLEGAL_ID);
		if (ILLEGAL_ID == mEventId) {
			throw new IllegalStateException("This Activity should be called with EXTRA_EVENT_ID in Intent bundle");
		}
		else {
			new lecturesLoader().execute(Long.valueOf(mEventId));
		}
	}

	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	     switch (item.getItemId()) {
	        case android.R.id.home:
	           // This ID represents the Home or Up button. In the case of this
	           // activity, the Up button is shown. Use NavUtils to allow users
	           // to navigate up one level in the application structure. For
	           // more details, see the Navigation pattern on Android Design:
	           //
	           // http://developer.android.com/design/patterns/navigation.html#up-vs-back
	           //
	           NavUtils.navigateUpTo(this, new Intent(this, EventsListActivity.class));
	           return true;
	     }
	     return super.onOptionsItemSelected(item);
	  }
	

	////////////////////////////////
	// Async task that queries the DB in background
	////////////////////////////////
	private class lecturesLoader extends AsyncTask<Long, Void, Cursor> {
		@Override
		protected Cursor doInBackground(Long... params) {
			SQLiteDatabase db = new DatabaseHelper(EventLecturesList.this.getApplicationContext(), DatabaseHelper.DB_NAME,null , DatabaseHelper.DB_VERSION).getReadableDatabase();
			return DBUtils.getLecturesByEventId(db,params[0]);
		}

		@Override
		protected void onPostExecute(Cursor result) {
			LecturesAdapter adapter = (LecturesAdapter) mList.getAdapter();
			if (null == adapter) {
				adapter = new LecturesAdapter(EventLecturesList.this.getApplicationContext(), result);
				mList.setAdapter(adapter);	
			}
			else {
				adapter.changeCursor(result);
			}
		}
	}
}


