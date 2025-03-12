package com.dgc.photoediting.setgetclass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FetchFrames {
    @SerializedName("statusCode")
    @Expose
    private String statusCode;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("payload")
    @Expose
    private ArrayList<PayloadFetchFrames> payloadFetchFrames;

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

    public ArrayList<PayloadFetchFrames> getPayloadFetchFrames() {
        return payloadFetchFrames;
    }

    public void setPayloadFetchFrames(ArrayList<PayloadFetchFrames> payloadFetchFrames) {
        this.payloadFetchFrames = payloadFetchFrames;
    }
}
