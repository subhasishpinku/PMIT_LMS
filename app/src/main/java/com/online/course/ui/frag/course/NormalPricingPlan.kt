package com.online.course.ui.frag.course

import com.online.course.model.AddToCart
import com.online.course.model.Course
import com.online.course.model.PricingPlan

class NormalPricingPlan(val course: Course) : BasePricingPlan() {

    override fun getAddToCartItem(plan: PricingPlan?): AddToCart {
        val addToCart = AddToCart()
        plan?.let { addToCart.pricingPlanId = it.id }
        addToCart.itemId = course.id
        addToCart.itemName = AddToCart.ItemType.WEBINAR.value
        return addToCart
    }

}