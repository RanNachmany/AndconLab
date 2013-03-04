package com.gdg.andconlab.ui;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.gdg.andconlab.DBUtils;
import com.gdg.andconlab.DatabaseHelper;
import com.gdg.andconlab.R;
import com.gdg.andconlab.R.id;
import com.gdg.andconlab.R.layout;
import com.gdg.andconlab.cache.ImageManager;
import com.gdg.andconlab.cache.ImageManager.ImageType;
import com.gdg.andconlab.models.Lecture;
import com.gdg.andconlab.models.Speaker;
import com.gdg.andconlab.utils.BrowserUtils;
import com.gdg.andconlab.utils.SLog;
import com.pagesuite.flowtext.FlowTextView;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class SingleLectureFragment extends SherlockFragment implements OnClickListener {

    private static final String TAG = "SingleLectureFragment";
    public static final String LECTURE_ID = "lecture_id";
    private static final int ILLEGAL_ID = -1;


    private View mRootView;
    private Lecture mLecture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.single_lecture_fragment, null);
        if (null != getArguments()) {
        	setLectureId(getArguments().getLong(LECTURE_ID));
        }
        return mRootView;
    }

    public void setLectureId(long id) {
        //fetch the lecture from db
        SQLiteDatabase db = new DatabaseHelper(getActivity().getApplicationContext(), DatabaseHelper.DB_NAME, null, DatabaseHelper.DB_VERSION).getReadableDatabase();
        Lecture lecture = DBUtils.getLectureById(db, id);

        //set lecture details
        getSherlockActivity().getSupportActionBar().setTitle(lecture.getName());
        String description = lecture.getDescription();
        if (description != null && !TextUtils.isEmpty(description.trim())) {
            ((TextView) mRootView.findViewById(R.id.lecture_description)).setText(description);
        } else {
            mRootView.findViewById(R.id.lecture_description).setVisibility(View.GONE);
        }

        String slidesUrl = lecture.getSlidesUrl();
        String videoUrl = lecture.getVideoUrl();
        boolean showButtons = false;
        if (!TextUtils.isEmpty(slidesUrl)) {
            View btnSlides = mRootView.findViewById(R.id.lecture_btn_slides);
            btnSlides.setVisibility(View.VISIBLE);
            btnSlides.setOnClickListener(this);
            showButtons = true;
        }

        if (!TextUtils.isEmpty(videoUrl)) {
            View btnVideo = mRootView.findViewById(R.id.lecture_btn_video);
            btnVideo.setVisibility(View.VISIBLE);
            btnVideo.setOnClickListener(this);
            showButtons = true;
        }

        if (showButtons) {
            mRootView.findViewById(R.id.lecture_btns_wrapper).setVisibility(View.VISIBLE);
        }

        //get all speakers
        ArrayList<Speaker> speakers = DBUtils.getSpeakersByLectureId(db, id);

        //loop and add speakers.
        LinearLayout ll = (LinearLayout) mRootView.findViewById(R.id.lecture_container);
        int speakerTxtSize = getResources().getDimensionPixelSize(R.dimen.sub_title_size);
        final LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        for (Speaker speaker : speakers) {
            FlowTextView ftv = (FlowTextView) layoutInflater.inflate(layout.speaker_item, ll, false);
            ImageView img = (ImageView) ftv.findViewById(R.id.speaker_item_img);

            ImageManager.loadImage(getActivity(), ImageType.SPEAKER, speaker.getImageUrl(), img);
            Spanned span = Html.fromHtml("<b>" + speaker.getFullName() + "</b><br/>" + speaker.getBio());
            ftv.setText(span);
            ftv.setTextSize(speakerTxtSize);
            ftv.invalidate();
            ll.addView(ftv);
        }

        db.close();
        mLecture = lecture;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case id.lecture_btn_slides:
                openWebsite(mLecture.getSlidesUrl());
                break;

            case id.lecture_btn_video:
                openWebsite(mLecture.getVideoUrl());
                break;
        }
    }


    //////////////////////////////////////////
    // Private
    //////////////////////////////////////////

    /**
     * Request to open the browser on given <code>url</code>
     *
     * @param url
     */
    private void openWebsite(String url) {
        try {
            BrowserUtils.openBrowserForUrl(getActivity(), url);
        } catch (URISyntaxException e) {
            SLog.e(TAG, e, "Couldn't open website: [%s]", url);
        }
    }

}
