package com.gdg.andconlab.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.gdg.andconlab.CommunicationService;
import com.gdg.andconlab.DBUtils;
import com.gdg.andconlab.DatabaseHelper;
import com.gdg.andconlab.R;

public class LecturesListFragment extends SherlockFragment implements OnItemClickListener{

	private ListView mList;
	private View mRootView;
	private BroadcastReceiver mUpdateReceiver;
	private callbacks mListener;
	
	public interface callbacks {
		public void onLectureClicked(long lectureId);
		public void fetchLecturesFromServer();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mUpdateReceiver = new BroadcastReceiver() {
			//TODO: [Ran] handle network failure
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equalsIgnoreCase(CommunicationService.RESULTS_ARE_IN)) {
					reloadLecturesFromDb();
				}
			}
		};
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.single_list_layout, null);
		mList = (ListView) mRootView.findViewById(R.id.list);
		mList.setOnItemClickListener(this);
		
		reloadLecturesFromDb();
		
		return mRootView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof callbacks)) {
			throw new IllegalStateException("Activity must implement callback interface in order to use this fragment");
		}
		mListener = (callbacks) activity;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (null != mUpdateReceiver) {
			getActivity().unregisterReceiver(mUpdateReceiver);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (null != mUpdateReceiver) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(CommunicationService.RESULTS_ARE_IN);
			getActivity().registerReceiver(mUpdateReceiver, filter);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> list, View view, int position, long id) {
		if (null != mListener) {
			mListener.onLectureClicked(id);
		}
	}
	
	public void reloadLecturesFromDb() {
		new lecturesLoader().execute((Void) null);
	}

	////////////////////////////////
	// Async task that queries the DB in background
	////////////////////////////////
	private class lecturesLoader extends AsyncTask<Void, Void, Cursor> {
		private SQLiteDatabase db;

		@Override
		protected Cursor doInBackground(Void... params) {
			db = new DatabaseHelper(getActivity().getApplicationContext(), DatabaseHelper.DB_NAME,null , DatabaseHelper.DB_VERSION).getReadableDatabase();
			return DBUtils.getAllLectures(db);
		}

		@Override
		protected void onPostExecute(Cursor result) {
			if (0 == result.getCount()) {
				//we don't have anything in our DB, force network refresh
				if (null != mListener) {
					mListener.fetchLecturesFromServer();
				}
			}
			else {
				LecturesAdapter adapter = (LecturesAdapter) mList.getAdapter();
				if (null == adapter) {
					adapter = new LecturesAdapter(getActivity().getApplicationContext(), result);
					mList.setAdapter(adapter);	
				}
				else {
					adapter.changeCursor(result);
				}
			}
			
			db.close();
		}
	}

}

