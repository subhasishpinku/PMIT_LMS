package com.online.course.ui.widget.onboarding.onboarding.entity

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.online.course.R

enum class OnBoardingPage(
    @StringRes val titleResource: Int,
    @StringRes val descriptionResource: Int,
    @DrawableRes val logoResource: Int,
    @RawRes val lottieResource : Int
) {

    FIRST(
        R.string.onboarding_slide1_title,
        R.string.onboarding_slide1_desc,
        R.drawable.ic_launcher_background,
        R.raw.onboarding1
    ),
    SECOND(
        R.string.onboarding_slide2_title,
        R.string.onboarding_slide2_desc,
        R.drawable.ic_launcher_background,
        R.raw.onboarding2
    ),
    THIRD(
        R.string.onboarding_slide3_title,
        R.string.onboarding_slide3_desc,
        R.drawable.ic_launcher_background,
        R.raw.onboarding3
    )

}