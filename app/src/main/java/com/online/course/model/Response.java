package com.online.course.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response extends BaseResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("attach")
    private String attachment;

    @SerializedName("profile_completion")
    private List<String> profileState;

    @SerializedName("already_registered")
    private boolean alreadyRegistered;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("id")
    private int discountId;

    @SerializedName("link")
    private String link;

    public String getAttachment() {
        return attachment;
    }

    public List<String> getProfileState() {
        return profileState;
    }

    public String getToken() {
        return token;
    }

    public boolean isAlreadyRegistered() {
        return alreadyRegistered;
    }

    public int getUserId() {
        return userId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
