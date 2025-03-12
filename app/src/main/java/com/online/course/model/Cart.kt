package com.online.course.model

import com.google.gson.annotations.SerializedName

class Cart {

    @SerializedName("items")
    lateinit var cartItems: List<CartItem>

    @SerializedName("amounts")
    lateinit var amounts: Amounts

    @SerializedName("user_group")
    var userGroup: UserGroup? = null
}