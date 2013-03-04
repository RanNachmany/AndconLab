package com.gdg.andconlab.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        mLecturesFragment = (LecturesListFragment) getSupportFragmentManager().findFragmentById(R.id.lectures_fragment);
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

        final IntentFilter filter = new IntentFilter();
        filter.addAction(CommunicationService.RESULTS_ARE_IN);
        registerReceiver(mUpdateReceiver, filter);

    }

    ////////////////////////////
    // Fragment interface
    ///////////////////////////
    @Override
    public void onLectureClicked(long lectureId) {
        Intent i = new Intent(this,SingleLectureActivity.class);
        i.putExtra(SingleLectureActivity.EXTRA_LECTURE_ID, lectureId);
        startActivity(i);
    }

    @Override
    public void fetchLecturesFromServer() {
        Intent i = new Intent(this,CommunicationService.class);
        startService(i);
        mProgressDialog = ProgressDialog.show(this, getString(R.string.progress_dialog_starting_title), getString(R.string.progress_dialog_starting_message));
    }

}
