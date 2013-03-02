package com.gdg.andconlab.ui;

import android.app.Activity;
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

public class LecturesList extends SherlockActivity {

	public static final String EXTRA_EVENT_ID = "event_id";
	
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
				Log.d("BUGBUG","Pressed on id " + id);
			}
		});
		
		mEventId = getIntent().getLongExtra(EXTRA_EVENT_ID, 0);
		
		new lecturesLoader().execute(new Long(mEventId));
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
	           NavUtils.navigateUpTo(this, new Intent(this, ItemsListActivity.class));
	           return true;
	     }
	     return super.onOptionsItemSelected(item);
	  }
	
	////////////////////////////////
	//List Adapter
	////////////////////////////////
	private class lecturesAdapter extends CursorAdapter {

		private int idx_name;
		private int idx_description;

		public lecturesAdapter(Context context, Cursor c) {
			super(context, c,0);

			idx_name = c.getColumnIndex(Lecture.COLUMN_NAME_NAME);
			idx_description = c.getColumnIndex(Lecture.COLUMN_NAME_DESCRIPTION);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.name.setText(cursor.getString(idx_name));
			holder.desctiprion.setText(cursor.getString(idx_description));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup list) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.lecture_list_item, null);

			ViewHolder holder = new ViewHolder();
			holder.name  = (TextView) view.findViewById(R.id.lecture_name);
			holder.desctiprion = (TextView) view.findViewById(R.id.lecture_description);

			view.setTag(holder);

			return view;
		}
	}

	private class ViewHolder {
		public TextView name;
		public TextView desctiprion;
	}



	////////////////////////////////
	// Async task that queries the DB in background
	////////////////////////////////
	private class lecturesLoader extends AsyncTask<Long, Void, Cursor> {
		@Override
		protected Cursor doInBackground(Long... params) {
			SQLiteDatabase db = new DatabaseHelper(LecturesList.this.getApplicationContext(), DatabaseHelper.DB_NAME,null , DatabaseHelper.DB_VERSION).getReadableDatabase();
			return DBUtils.getLecturesByEventId(db,params[0]);
		}

		@Override
		protected void onPostExecute(Cursor result) {
			lecturesAdapter adapter = (lecturesAdapter) mList.getAdapter();
			if (null == adapter) {
				adapter = new lecturesAdapter(LecturesList.this.getApplicationContext(), result);
				mList.setAdapter(adapter);	
			}
			else {
				adapter.changeCursor(result);
			}
		}
	}
}


