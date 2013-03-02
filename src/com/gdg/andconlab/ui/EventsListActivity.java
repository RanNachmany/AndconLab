package com.gdg.andconlab.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.gdg.andconlab.DBUtils;
import com.gdg.andconlab.DatabaseHelper;
import com.gdg.andconlab.R;
import com.gdg.andconlab.ServerCommunicationManager;
import com.gdg.andconlab.models.Event;

/**
 * Activity that displays a list of all events. 
 * If no events were found in local DB - it will issue server update 
 * automatically, displaying a wait dialog to the user. 
 * @author Ran Nachmany
 *
 */
public class EventsListActivity extends SherlockActivity {

	private ListView mList;
	private ProgressDialog mProgressDialog;
	private BroadcastReceiver mUpdateReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_list_activity);
		mList = (ListView) findViewById(R.id.list);
		
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> list, View view, int position,
					long id) {
				Intent i = new Intent(EventsListActivity.this,EventLecturesList.class);
				i.putExtra(EventLecturesList.EXTRA_EVENT_ID, id);
				startActivity(i);
			}
		});
		
		new eventsLoader().execute((Void) null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main,  menu);
		
		return true;
	}

	private void refreshList(boolean firstLoad) {
		if (firstLoad)
			mProgressDialog = ProgressDialog.show(this, getString(R.string.progress_dialog_starting_title), getString(R.string.progress_dialog_starting_message));

		final IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ServerCommunicationManager.RESULTS_ARE_IN);

		mUpdateReceiver = new BroadcastReceiver() {
			//TODO: [Ran] handle network failure
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equalsIgnoreCase(ServerCommunicationManager.RESULTS_ARE_IN)) {
					new eventsLoader().execute((Void) null);
				}
				
				if (null != mProgressDialog)
					mProgressDialog.dismiss();
			}

		};
		registerReceiver(mUpdateReceiver, mFilter);
		ServerCommunicationManager.getInstance(getApplicationContext()).startSearch("Android", 1);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (null != mUpdateReceiver) {
			unregisterReceiver(mUpdateReceiver);
			mUpdateReceiver = null;
		}
	}

	////////////////////////////////
	//List Adapter
	////////////////////////////////
	private class eventsAdapter extends CursorAdapter {

		private int idx_name;
		private int idx_description;
		private int idx_image_url;

		public eventsAdapter(Context context, Cursor c) {
			super(context, c,0);

			idx_name = c.getColumnIndex(Event.COLUMN_NAME_NAME);
			idx_description = c.getColumnIndex(Event.COLUMN_NAME_DESCRIPTION);
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
			View view = inflater.inflate(R.layout.event_list_item, null);

			ViewHolder holder = new ViewHolder();
			holder.image = (ImageView) view.findViewById(R.id.event_image);
			holder.name  = (TextView) view.findViewById(R.id.event_name);
			holder.desctiprion = (TextView) view.findViewById(R.id.event_description);

			view.setTag(holder);
			
			return view;
		}
	}

	private class ViewHolder {
		public TextView name;
		public TextView desctiprion;
		public ImageView image;
	}

	////////////////////////////////
	// Async task that queries the DB in background
	////////////////////////////////
	private class eventsLoader extends AsyncTask<Void, Void, Cursor> {
		@Override
		protected Cursor doInBackground(Void... params) {


			SQLiteDatabase db = new DatabaseHelper(EventsListActivity.this.getApplicationContext(), DatabaseHelper.DB_NAME,null , DatabaseHelper.DB_VERSION).getReadableDatabase();
			return DBUtils.getEventsCurosr(db);
		}

		@Override
		protected void onPostExecute(Cursor result) {
			if (0 == result.getCount()) {
				//we don't have anythign in our DB, force network refresh
				refreshList(true);
			}
			else {
				eventsAdapter adapter = (eventsAdapter) mList.getAdapter();
				if (null == adapter) {
					adapter = new eventsAdapter(EventsListActivity.this.getApplicationContext(), result);
					mList.setAdapter(adapter);
				}
				else {
					adapter.changeCursor(result);
				}
			}
		}
	}
}
