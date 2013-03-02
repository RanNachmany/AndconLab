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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.gdg.andconlab.CommunicationService;
import com.gdg.andconlab.DBUtils;
import com.gdg.andconlab.DatabaseHelper;
import com.gdg.andconlab.R;

/**
 * Activity that displays a list of all lectures given in GDG
 * If no lectures were found in local DB - it will auto fetch lectures from the server,
 * displaying a wait dialog to the user. 
 * @author Ran Nachmany
 *
 */
public class LecturesListActivity extends SherlockActivity implements LecturesListFragment.callbacks{

	private ProgressDialog mProgressDialog;
	private BroadcastReceiver mUpdateReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.single_list_activity);

		mUpdateReceiver = new BroadcastReceiver() {
			//TODO: [Ran] handle network failure
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equalsIgnoreCase(CommunicationService.RESULTS_ARE_IN)) {
					
				}

				if (null != mProgressDialog)
					mProgressDialog.dismiss();
			}
		};
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (null != mUpdateReceiver) {
			unregisterReceiver(mUpdateReceiver);
			mUpdateReceiver = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		final IntentFilter filter = new IntentFilter();
		filter.addAction(CommunicationService.RESULTS_ARE_IN);
		registerReceiver(mUpdateReceiver, filter);
	}

	@Override
	public void onLectureClicked(long lectureId) {
		Intent i = new Intent(this,SingleLectureActivity.class);
		i.putExtra(SingleLectureActivity.EXTRA_LECTURE_ID, lectureId);
		startActivity(i);
		
	}

	@Override
	public void fetchLecturesFromServer() {
		Intent i = new Intent (this,CommunicationService.class);
		startService(i);
	}
}
