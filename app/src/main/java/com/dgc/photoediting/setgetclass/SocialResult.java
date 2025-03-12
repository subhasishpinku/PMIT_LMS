package com.dgc.photoediting.setgetclass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SocialResult {
    @SerializedName("statusCode")
    @Expose
    private String statusCode;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("payload")
    @Expose

    private PayloadSocial payload;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PayloadSocial getPayload() {
        return payload;
    }

    public void setPayload(PayloadSocial payload) {
        this.payload = payload;
    }
}
