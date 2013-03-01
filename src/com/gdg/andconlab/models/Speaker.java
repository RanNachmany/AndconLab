
package com.gdg.andconlab.models;

import com.gdg.andconlab.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class Speaker implements Serializable {
    //////////////////////////////////////////
    // Members
    //////////////////////////////////////////
    private long id;
    private String bio;
    @JsonProperty("first_name") private String firstName;
    @JsonProperty("last_name") private String lastName;
    @JsonProperty("image_url") private String imageUrl;

    //////////////////////////////////////////
    // Getters & Setters
    //////////////////////////////////////////
    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return StringUtils.concat(firstName, " ", lastName);
    }
}
