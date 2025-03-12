package com.online.course.ui.frag.course

import com.online.course.model.AddToCart
import com.online.course.model.PricingPlan

abstract class BasePricingPlan {
    abstract fun getAddToCartItem(plan: PricingPlan?) : AddToCart
}