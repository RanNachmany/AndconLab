
package com.gdg.andconlab.models;

import android.content.ContentValues;
import android.content.Context;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class Event implements Serializable {

    public static final String TABLE_NAME = "events";
    public static final String COLUMN_NAME_ID = "_id";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_DESCRIPTION = "description";
    public static final String COLUMN_NAME_LOGO_URL = "logo_url";
    public static final String COLUMN_NAME_WEBSITE_URL = "website_url";
    public static final String COLUMN_NAME_START_DATE = "start_date";
    public static final String COLUMN_NAME_END_DATE = "end_date";

    //////////////////////////////////////////
    // Members
    //////////////////////////////////////////
    @JsonProperty("id") private long mId;
    @JsonProperty("name") private String mName;
    @JsonProperty("description") private String mDescription;
    private List<Lecture> mLectures;
    @JsonProperty("logo_url") private String mLogoUrl;
    @JsonProperty("website_url") private String mWebsiteUrl;
    @JsonProperty("start_date") private String mStartDate;
    @JsonProperty("end_date") private String mEndDate;

    //////////////////////////////////////////
    // Public
    //////////////////////////////////////////

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_DESCRIPTION, mDescription);
        cv.put(COLUMN_NAME_END_DATE, mEndDate);
        cv.put(COLUMN_NAME_ID, mId);
        cv.put(COLUMN_NAME_LOGO_URL, mLogoUrl);
        cv.put(COLUMN_NAME_NAME, mName);
        cv.put(COLUMN_NAME_START_DATE, mStartDate);
        cv.put(COLUMN_NAME_WEBSITE_URL, mWebsiteUrl);
        return cv;
    }

    /**
     * Save event in local database
     *
     * @param context
     */
    public void save(Context context) {
      /*  if (context != null && lectures != null && !lectures.isEmpty()) {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            for (Lecture lecture : lectures) {
                ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ItemColumns.CONTENT_URI)
                        .withValue(ItemColumns.COLUMN_NAME_EVENT_NAME, name)
                        .withValue(ItemColumns.COLUMN_NAME_EVENT_DESCRIPTION, description)
                        .withValue(ItemColumns.COLUMN_NAME_EVENT_LOGO_URL, logoUrl)
                        .withValue(ItemColumns.COLUMN_NAME_EVENT_WEBSITE_URL, websiteUrl)
                        .withValue(ItemColumns.COLUMN_NAME_EVENT_START_DATE, startDate)
                        .withValue(ItemColumns.COLUMN_NAME_EVENT_END_DATE, endDate)
                        .withValue(ItemColumns.COLUMN_NAME_LECTURE_TITLE, lecture.getName())
                        .withValue(ItemColumns.COLUMN_NAME_LECTURE_VIDEO_URL, lecture.getVideoUrl())
                        .withValue(ItemColumns.COLUMN_NAME_LECTURE_SLIDES_URL, lecture.getSlidesUrl())
                        .withValue(ItemColumns.COLUMN_NAME_LECTURE_VIDEO_ID, lecture.getYoutubeAssetId())
                        .withValue(ItemColumns.COLUMN_NAME_LECTURE_DESCRIPTION, lecture.getDescription())
                        .withValue(ItemColumns.COLUMN_NAME_LECTURE_DURATION, lecture.getDuration());
                List<Speaker> speakers = lecture.getSpeakers();
                if (speakers != null && !speakers.isEmpty()) {
                    // TODO current implementation supports only one speaker
                    Speaker speaker = speakers.get(0);
                    builder.withValue(ItemColumns.COLUMN_NAME_LECTURER_NAME, speaker.getFullName())
                            .withValue(ItemColumns.COLUMN_NAME_LECTURER_IMAGE_URL, speaker.getImageUrl())
                            .withValue(ItemColumns.COLUMN_NAME_LECTURER_BIO, speaker.getBio());
                }

                ops.add(builder.build());
            }

            DBUtils.sApplyBatch(context, ItemsProvider.AUTHORITY, ops);
        }*/
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

    public String getEndDate() {
        return this.mEndDate;
    }

    public void setEndDate(String endDate) {
        this.mEndDate = endDate;
    }

    public long getId() {
        return this.mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public List<Lecture> getLectures() {
        return this.mLectures;
    }

    public void setLectures(List<Lecture> lectures) {
        this.mLectures = lectures;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getStartDate() {
        return this.mStartDate;
    }

    public void setStartDate(String startDate) {
        this.mStartDate = startDate;
    }

    public String getLogoUrl() {
        return this.mLogoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.mLogoUrl = logoUrl;
    }

    public String getWebsiteUrl() {
        return mWebsiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.mWebsiteUrl = websiteUrl;
    }
}
