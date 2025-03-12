package com.dgc.photoediting.setgetclass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfile {
    @SerializedName("statusCode")
    @Expose
    private String statusCode;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("payload")
    @Expose
    private payloaduser payloaduser;

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

    public com.dgc.photoediting.setgetclass.payloaduser getPayloaduser() {
        return payloaduser;
    }

    public void setPayloaduser(com.dgc.photoediting.setgetclass.payloaduser payloaduser) {
        this.payloaduser = payloaduser;
    }
}
