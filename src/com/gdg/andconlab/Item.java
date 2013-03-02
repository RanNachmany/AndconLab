package com.gdg.andconlab;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable{
	
	private String mEventName;
	private String mLectureTitle;
	private String mLectureVideoUrl;
	private String mLectureSlidesUrl;
	private String mYoutubeAssetId;
	private String mDescription;
	private String mLecturerName;	
	private String mLecturerProfileImageUrl;
	
	public static final String JSON_OBJECT_ITEM_EVENT_NAME = "event_name";
	public static final String JSON_OBJECT_ITEM_LECTURE_TITLE = "lecture_title";
	public static final String JSON_OBJECT_ITEM_VIDEO = "lecture_video_url";
	public static final String JSON_OBJECT_ITEM_SLIDES = "lecture_slides_url";
	public static final String JSON_OBJECT_ITEM_YOUTUBE_ID = "lecture_youtube_asset_id";
	public static final String JSON_OBJECT_ITEM_DESCRIPTION = "description";
	public static final String JSON_OBJECT_ITEM_LECTURER_NAME = "lecturer_name";
	public static final String JSON_OBJECT_ITEM_LECTURER_IMAGE = "lecturer_profile_image_url";
	public static final Parcelable.Creator<Item> CREATOR = new Creator<Item>() {
	    public Item createFromParcel(Parcel source) {return new Item(source);}
	    public Item[] newArray(int size) {return new Item[size];}
	};
	public Item(Parcel source) {
		this.mEventName = source.readString();
		this.mLectureTitle = source.readString();
		this.mLectureVideoUrl = source.readString();
		this.mLectureSlidesUrl = source.readString();
		this.mYoutubeAssetId = source.readString();
	    this.mDescription = source.readString();
	    this.mLecturerName = source.readString();
	    this.mLecturerProfileImageUrl= source.readString();
	}
	@Override
	public int describeContents() {return 0;}
	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeString(mEventName);
		parcel.writeString(mLectureTitle);
		parcel.writeString(mLectureVideoUrl);
		parcel.writeString(mLectureSlidesUrl);
		parcel.writeString(mYoutubeAssetId);
		parcel.writeString(mDescription);
		parcel.writeString(mLecturerName);
		parcel.writeString(mLecturerProfileImageUrl);
		
	}

	public Item(String event_name, String lecture_title, String lecture_video, String lecture_slides, String asset_id, String description, String lecturer_name, String lecturer_image) {
		this.mEventName = event_name;
		this.mLectureTitle = lecture_title;
		this.mLectureVideoUrl = lecture_video;
		this.mLectureSlidesUrl = lecture_slides;
		this.mYoutubeAssetId = asset_id;
	    this.mDescription = description;
	    this.mLecturerName = lecturer_name;
	    this.mLecturerProfileImageUrl= lecturer_image;  
	}
	
	
	public Item(Context mContext) {
		this.mEventName = mContext.getString(R.string.default_text);
		this.mLectureTitle = mContext.getString(R.string.default_text);
		this.mLectureVideoUrl = mContext.getString(R.string.default_url);
		this.mLectureSlidesUrl = mContext.getString(R.string.default_url);
		this.mYoutubeAssetId = mContext.getString(R.string.default_url);
	    this.mDescription = mContext.getString(R.string.default_text);
	    this.mLecturerName = mContext.getString(R.string.default_text);
	    this.mLecturerProfileImageUrl= mContext.getString(R.string.default_url);
	}

	public String getEventName(){
		return mEventName;
	}
	
	public String getLectureTitle(){
		return mLectureTitle;
	}
	
	public String getLectureVideoUrl(){
		return mLectureVideoUrl;
	}
	
	public String getLectureSlidesUrl(){
		return mLectureSlidesUrl;
	}
	
	public String getLectureYoutubeAssetId(){
		return mYoutubeAssetId;
	}
	
	public String getLectureDescription(){
		return mDescription;
	}
	
	public String getLecturerName(){
		return mLecturerName;
	}
	
	public String getLecturerProfileImageUrl(){
		return mLecturerProfileImageUrl;
	}
}
