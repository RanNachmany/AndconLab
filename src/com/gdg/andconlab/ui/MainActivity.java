package com.gdg.andconlab.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.gdg.andconlab.CommunicationService;
import com.gdg.andconlab.R;

/**
 * Activity that displays a list of all lectures given in GDG
 * If no lectures were found in local DB - it will auto fetch lectures from the server,
 * displaying a wait dialog to the user. 
 * @author Ran Nachmany
 *
 */
public class MainActivity extends SherlockFragmentActivity implements LecturesListFragment.callbacks{

	private ProgressDialog mProgressDialog;
	private BroadcastReceiver mUpdateReceiver;
	private LecturesListFragment mLecturesFragment;
	private SingleLectureFragment mLectureFragment;
	private boolean mTwoPanes = false;
	private long currentLectureId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);

		mUpdateReceiver = new BroadcastReceiver() {
			//TODO: [Ran] handle network failure
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equalsIgnoreCase(CommunicationService.RESULTS_ARE_IN)) {
					mLecturesFragment.reloadLecturesFromDb();
				}

				if (null != mProgressDialog)
					mProgressDialog.dismiss();
			}
		};

		mLecturesFragment = (LecturesListFragment) getSupportFragmentManager().findFragmentById(R.id.lectures_fragment);
		if (null != findViewById(R.id.lecture_details_container)) {
			mTwoPanes = true;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.youtube_play:
			startActivity(new Intent(getApplicationContext(), YoutubePlayerActivity.class).putExtra("CURRENT_ASSET_ID", mLectureFragment.getYoutubeAssetId()));
			return true;
		}
		return true;
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

	////////////////////////////
	// Fragment interface
	///////////////////////////
	@Override
	public void onLectureClicked(long lectureId) {
		currentLectureId = lectureId;
		if (mTwoPanes) {
			mLectureFragment = new SingleLectureFragment();
			Bundle b = new Bundle();
			b.putLong(SingleLectureFragment.LECTURE_ID, lectureId);
			mLectureFragment.setArguments(b);
			getSupportFragmentManager().beginTransaction().replace(R.id.lecture_details_container, mLectureFragment).commit();
		}
		else {
			Intent i = new Intent(this,SingleLectureActivity.class);
			i.putExtra(SingleLectureActivity.EXTRA_LECTURE_ID, lectureId);
			startActivity(i);
		}

	}

	@Override
	public void fetchLecturesFromServer() {
		Intent i = new Intent(this,CommunicationService.class);
		startService(i);
		mProgressDialog = ProgressDialog.show(this, getString(R.string.progress_dialog_starting_title), getString(R.string.progress_dialog_starting_message));
	}

}
