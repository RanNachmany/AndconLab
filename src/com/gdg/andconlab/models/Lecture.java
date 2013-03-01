
package com.gdg.andconlab.models;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class Lecture implements Serializable {
    //////////////////////////////////////////
    // Members
    //////////////////////////////////////////
    private long id;
    private String name;
    private String description;
    private int duration;
    private List<Speaker> speakers;
    @JsonProperty("video_url") private String videoUrl;
    @JsonProperty("slides_url") private String slidesUrl;
    @JsonProperty("youtube_asset_id") private String youtubeAssetId;

    //////////////////////////////////////////
    // Getters & Setters
    //////////////////////////////////////////
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Speaker> getSpeakers() {
        return this.speakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getSlidesUrl() {
        return this.slidesUrl;
    }

    public void setSlidesUrl(String slidesUrl) {
        this.slidesUrl = slidesUrl;
    }

    public String getYoutubeAssetId() {
        return this.youtubeAssetId;
    }

    public void setYoutubeAssetId(String youtubeAssetId) {
        this.youtubeAssetId = youtubeAssetId;
    }
}
