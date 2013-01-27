package com.gdg.andconlab;

import android.content.Context;

public class Item {
	
	private String mTitle;
	
	private String mDescription;
	
	private String mThumbnailUrl;
	    
	
	public Item(String title, String description, String thumbnailUrl) {
		this.mTitle = title;
	    this.mDescription = description;
	    this.mThumbnailUrl= thumbnailUrl;  
	}
	
	
	public Item(Context mContext) {
		this.mTitle = mContext.getString(R.string.default_title);
	    this.mDescription = mContext.getString(R.string.default_description);
	    this.mThumbnailUrl= mContext.getString(R.string.default_thumbnail_url);
	}


	public String getTitle(){
		return mTitle;
	}
	
	public String getDescription(){
		return mDescription;
	}
	
	public String getThumbnailUrl(){
		return mThumbnailUrl;
	}

}
