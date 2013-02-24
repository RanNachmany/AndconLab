/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gdg.andconlab;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SingleItemDetailsScreen extends SherlockActivity {

	Item mCurrentItem;
	ImageView mLecturerImage;
	TextView mLecturerName;
	TextView mLectureTitle;
	TextView mLectureDescription;
	TextView mLinksSlides;
	TextView mLinksVideo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.single_item_details_activity);
    
    getSupportActionBar().setHomeButtonEnabled(true);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
    mLecturerImage = (ImageView)this.findViewById(R.id.single_details_lecturer_image);
    mLectureTitle = (TextView)this.findViewById(R.id.single_details_lecture_title);
    mLecturerName = (TextView)this.findViewById(R.id.single_details_lecturer_name);
    mLectureDescription = (TextView)this.findViewById(R.id.single_details_lecture_description);
    mLinksSlides = (TextView)this.findViewById(R.id.single_details_lecture_links_slides);
    mLinksVideo = (TextView)this.findViewById(R.id.single_details_lecture_links_video);
  }
  
  @Override
  protected void onStart() {
    super.onStart();

    mCurrentItem = (Item) getIntent().getExtras().get("CURRENT_ITEM");
    
    mLecturerImage.setTag(mCurrentItem.getLecturerProfileImageUrl()+"$sep$"+mCurrentItem.getLectureYoutubeAssetId());
    ServerCommunicationManager.getInstance(this.getApplicationContext()).getBitmap(mLecturerImage);
    
    mLectureTitle.setText(mCurrentItem.getLectureTitle());
    mLecturerName.setText(mCurrentItem.getLecturerName());
    mLectureDescription.setText(mCurrentItem.getLectureDescription());
    mLinksSlides.setText(mCurrentItem.getLectureSlidesUrl());
    mLinksVideo.setText(mCurrentItem.getLectureVideoUrl());
    
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

}