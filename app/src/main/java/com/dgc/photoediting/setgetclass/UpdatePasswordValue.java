package com.dgc.photoediting.setgetclass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdatePasswordValue {
    @SerializedName("emailId")
    @Expose
    private String emailId;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("confirmPassword")
    @Expose
    private String confirmPassword;

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
