package com.gdg.andconlab.ui;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.gdg.andconlab.DBUtils;
import com.gdg.andconlab.DatabaseHelper;
import com.gdg.andconlab.R;
import com.gdg.andconlab.models.Lecture;
import com.gdg.andconlab.models.Speaker;
import com.pagesuite.flowtext.FlowTextView;

public class SingleLectureFragment extends SherlockFragment{

	public static final String LECTURE_ID = "lecture_id";
	private static final int ILLEGAL_ID = -1;

	private View mRootView;
	private String mYoutubeAssetId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.single_lecture_fragment, null);

		return mRootView;
	}
	
	public void setLectureId (long id) {
		//fetch the lecture from db
		SQLiteDatabase db = new DatabaseHelper(getActivity().getApplicationContext(), DatabaseHelper.DB_NAME, null, DatabaseHelper.DB_VERSION).getReadableDatabase();
		Lecture lecture = DBUtils.getLectureById(db, id);

		mYoutubeAssetId = lecture.getYoutubeAssetId();
		//set lecture name and description
		((TextView)mRootView.findViewById(R.id.lecture_name)).setText(lecture.getName());
		((TextView)mRootView.findViewById(R.id.lecture_description)).setText(lecture.getDescription());

		try{
			//get all speakers
			ArrayList<Speaker> speakers = DBUtils.getSpeakersByLectureId(db, id);

			//loop and add speakers.
			LinearLayout ll = (LinearLayout)mRootView.findViewById(R.id.lecture_container);
			FlowTextView ftv;
			ImageView img;
			for (Speaker speaker : speakers) {
				ftv = new FlowTextView(getActivity());
				img = new ImageView(getActivity());
				img.setImageResource(R.drawable.andconlablauncher);
				Spanned span = Html.fromHtml("<b>"+speaker.getFirstName() + " " + speaker.getLastName() +"</B><BR>" + speaker.getBio());
				ftv.addView(img);
				ftv.setText(span);
				ftv.setTextSize(getResources().getDimensionPixelSize(R.dimen.sub_title_size));
				ftv.invalidate();
				ll.addView(ftv);
			}
		}catch(Exception e){
			
		}
		
		db.close();
	}
	
	public String getYoutubeAssetId(){
		return mYoutubeAssetId;
	}

}
