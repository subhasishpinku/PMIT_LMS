package com.dgc.photoediting.setgetclass;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIRequests {
    @POST("auth/register")
    Call<Result> upgradeSchoolData(@Body Registration upgradeSchoolJson);

    @POST("auth/authenticate")
    Call<LoginResult> loginResultCall(@Body LoginValue loginResultCall);

    @GET("auth/check_user_email/{id}")
    Call<CheckuseremailResult> getCheckuseremailResultCall(@Path("id") String id);

    @POST("auth/updatePassword")
    Call<UpdatePasswordResult> UpdatePasswordResult(@Body UpdatePasswordValue loginResultCall);

    @GET("master/fetchStickers")
    Call<FetchStickers> doFetchStickersCall();

    @GET("master/fetchFrames")
    Call<FetchFrames> doFetchFrameCall();
    @GET("auth/getProfile")
   // @Headers("Content-Type: application/json")
//    Call<UserProfile> getUser(@Header("Authorization") String loginToken);
    Call<UserProfile> getUser();

    @POST("auth/updateProfile")
    Call<UpdateProfileResult> upgradeProfile(@Body UpdateProfileValue updateProfileValue);

    @POST("auth/socialLogin")
    Call<SocialResult> socialResultCall(@Body Socialvalue socialvalue);
}
