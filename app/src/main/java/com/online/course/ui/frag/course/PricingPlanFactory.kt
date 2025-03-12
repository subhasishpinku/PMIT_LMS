package com.online.course.ui.frag.course

import com.online.course.model.Course

class PricingPlanFactory {
    companion object {
        fun getPlan(course: Course): BasePricingPlan {
            if (course.isBundle()) {
                return BundlePricingPlan(course)
            }

            return NormalPricingPlan(course)
        }
    }
}