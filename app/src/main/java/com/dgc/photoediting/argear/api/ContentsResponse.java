package com.dgc.photoediting.argear.api;

import com.google.gson.annotations.SerializedName;
import com.dgc.photoediting.argear.model.CategoryModel;

import java.util.List;

public class ContentsResponse {

    @SerializedName("api_key")
    public String apiKey;

    @SerializedName("name")
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName("status")
    public String status;

    @SerializedName("last_updated_at")
    public long lastUpdatedAt;

    @SerializedName("categories")
    public List<CategoryModel> categories;
}
