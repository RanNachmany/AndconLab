package com.gdg.andconlab.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.gdg.andconlab.DBUtils;
import com.gdg.andconlab.DatabaseHelper;
import com.gdg.andconlab.R;
import com.gdg.andconlab.models.Lecture;
import com.gdg.andconlab.models.Speaker;
import com.pagesuite.flowtext.FlowTextView;

/**
 * Activity that displays a single lecture with all its details.
 * This activity expects to get the lecture ID in the calling intent
 * @author Ran Nachmany
 *
 */
public class SingleLectureActivity extends SherlockFragmentActivity{

	public static final String EXTRA_LECTURE_ID = "lecture_id";
	
	private static final int ILLEGAL_ID = -1;
	
	private SingleLectureFragment mFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		setContentView(R.layout.single_lecture_activity);
		
		long id = getIntent().getLongExtra(EXTRA_LECTURE_ID, ILLEGAL_ID);
		if (ILLEGAL_ID == id) {
			throw new IllegalStateException("You should set lecture ID in the calling intent");
		}
		else {
			mFragment = (SingleLectureFragment) getSupportFragmentManager().findFragmentById(R.id.single_lecture_fragment);
			mFragment.setLectureId(id);
		}
	}
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	       MenuInflater inflater = getSupportMenuInflater();
	       inflater.inflate(R.menu.single_display_menu, menu);
	       return super.onCreateOptionsMenu(menu);
	    }
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	     switch (item.getItemId()) {
	        case android.R.id.home:
	           finish();
	           return true;
	           
	        case R.id.youtube_play:
	          	startActivity(new Intent(SingleLectureActivity.this.getApplicationContext(), YoutubePlayerActivity.class).putExtra("CURRENT_ASSET_ID", mFragment.getYoutubeAssetId()));
	              return true;
	     }
	     return super.onOptionsItemSelected(item);
	  }
}
