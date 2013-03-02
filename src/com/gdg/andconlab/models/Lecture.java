
package com.gdg.andconlab.models;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import android.content.ContentValues;

public class Lecture implements Serializable {

	public static final String TABLE_NAME					= "lectures";
	public static final String COLUMN_NAME_ID 				= "_id";
	public static final String COLUMN_NAME_NAME 			= "name";
	public static final String COLUMN_NAME_DESCRIPTION 		= "description";
	public static final String COLUMN_NAME_DURATION 		= "duration";
	public static final String COLUMN_NAME_VIDEO_URL 		= "video_url";
    public static final String COLUMN_NAME_SLIDES_URL 		= "slides_url";
    public static final String COLUMN_NAME_YOOUTBE_ASSET_ID = "youtube_asset";
    public static final String COLUMN_NAME_EVENT_ID 		= "event_id";
    
	
	private static final long serialVersionUID = 1L;
	//////////////////////////////////////////
    // Members
    //////////////////////////////////////////
	@JsonProperty("id") private long mId;
	@JsonProperty("name") private String mName;
	@JsonProperty("description") private String mDescription;
	@JsonProperty("duration") private int mDuration;
    @JsonProperty("video_url") private String mVideoUrl;
    @JsonProperty("slides_url") private String mSlidesUrl;
    @JsonProperty("youtube_asset_id") private String mYoutubeAssetId;
    private List<Speaker> mSpeakers;
    private long mEventId;
    
    public ContentValues getContentValues() {
    	ContentValues cv = new ContentValues();
    	cv.put(COLUMN_NAME_DESCRIPTION, mDescription);
    	cv.put(COLUMN_NAME_DURATION, mDuration);
    	cv.put(COLUMN_NAME_EVENT_ID, mEventId);
    	cv.put(COLUMN_NAME_ID, mId);
    	cv.put(COLUMN_NAME_NAME, mName);
    	cv.put(COLUMN_NAME_SLIDES_URL, mSlidesUrl);
    	cv.put(COLUMN_NAME_VIDEO_URL, mVideoUrl);
    	cv.put(COLUMN_NAME_YOOUTBE_ASSET_ID, mYoutubeAssetId);
    	return cv;
    }
    
    
    //////////////////////////////////////////
    // Getters & Setters
    //////////////////////////////////////////
    public String getDescription() {
        return this.mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public long getId() {
        return this.mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public List<Speaker> getSpeakers() {
        return this.mSpeakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.mSpeakers = speakers;
    }

    public String getVideoUrl() {
        return this.mVideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.mVideoUrl = videoUrl;
    }

    public String getSlidesUrl() {
        return this.mSlidesUrl;
    }

    public void setSlidesUrl(String slidesUrl) {
        this.mSlidesUrl = slidesUrl;
    }

    public String getYoutubeAssetId() {
        return this.mYoutubeAssetId;
    }

    public void setYoutubeAssetId(String youtubeAssetId) {
        this.mYoutubeAssetId = youtubeAssetId;
    }

	public long getEventId() {
		return mEventId;
	}

	public void setEventId(long eventId) {
		mEventId = eventId;
	}
}
