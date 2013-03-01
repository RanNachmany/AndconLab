
package com.gdg.andconlab.models;

import android.content.ContentProviderOperation;
import android.content.Context;
import com.gdg.andconlab.DBUtils;
import com.gdg.andconlab.ItemsProvider;
import com.gdg.andconlab.ItemsProvider.ItemColumns;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {
    //////////////////////////////////////////
    // Members
    //////////////////////////////////////////
    private long id;
    private String name;
    private String description;
    private List<Lecture> lectures;
    @JsonProperty("logo_url") private String logoUrl;
    @JsonProperty("website_url") private String websiteUrl;
    @JsonProperty("start_date") private String startDate;
    @JsonProperty("end_date") private String endDate;

    //////////////////////////////////////////
    // Public
    //////////////////////////////////////////

    /**
     * Save event in local database
     *
     * @param context
     */
    public void save(Context context) {
        if (context != null && lectures != null && !lectures.isEmpty()) {
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
        }
    }

    //////////////////////////////////////////
    // Getters & Setters
    //////////////////////////////////////////
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Lecture> getLectures() {
        return this.lectures;
    }

    public void setLectures(List<Lecture> lectures) {
        this.lectures = lectures;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getLogoUrl() {
        return this.logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
}
