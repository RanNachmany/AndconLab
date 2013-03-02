
package com.gdg.andconlab.models;

import android.content.ContentValues;

import com.gdg.andconlab.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class Speaker implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME 				= "speakers";
	public static final String COLUMN_NAME_ID 			= "speaker_id";
	public static final String COLUMN_NAME_BIO 			= "bio";
	public static final String COLUMN_FIRST_NAME 		= "first_name";
	public static final String COLUMN_LAST_NAME 		= "last_name";
    public static final String COLUMN_NAME_IMAGE_URL 	= "image_url";
	
    //////////////////////////////////////////
    // Members
    //////////////////////////////////////////
	@JsonProperty("id") private long mId;
    @JsonProperty("bio") private String mBio;
    @JsonProperty("first_name") private String mFirstName;
    @JsonProperty("last_name") private String mLastName;
    @JsonProperty("image_url") private String mImageUrl;

    
    public ContentValues getContentValues() {
    	ContentValues cv = new ContentValues();
    	cv.put(COLUMN_FIRST_NAME, mFirstName);
    	cv.put(COLUMN_LAST_NAME, mLastName);
    	cv.put(COLUMN_NAME_BIO, mBio);
    	cv.put(COLUMN_NAME_IMAGE_URL, mImageUrl);
    	
    	return cv;
    }
    
    //////////////////////////////////////////
    // Getters & Setters
    //////////////////////////////////////////
    public String getBio() {
        return this.mBio;
    }

    public void setBio(String bio) {
        this.mBio = bio;
    }

    public String getFirstName() {
        return this.mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public long getId() {
        return this.mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getImageUrl() {
        return this.mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    public String getLastName() {
        return this.mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public String getFullName() {
        return StringUtils.concat(mFirstName, " ", mLastName);
    }
}
