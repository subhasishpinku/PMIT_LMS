package com.online.course.ui.frag.course

import com.online.course.model.AddToFav
import com.online.course.model.Course

class BundleCourseAddToFav(val course: Course) : BaseCourseAddToFav() {

    override fun getAddToFavItem(): AddToFav {
        val addToFav = AddToFav()
        addToFav.itemId = course.id
        addToFav.itemName = AddToFav.ItemType.BUNDLE.value
        return addToFav
    }
}