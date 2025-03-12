package com.online.course.model

import com.google.gson.annotations.SerializedName

class Comments() {

    @SerializedName("my_comment")
    lateinit var myComments: MyComments

    @SerializedName("class_comment")
    lateinit var classComments: List<Comment>
}