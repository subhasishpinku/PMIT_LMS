package com.online.course.ui.frag.course

import com.online.course.model.AddToFav
import com.online.course.model.Course

class NormalCourseAddToFav(val course: Course) : BaseCourseAddToFav() {

    override fun getAddToFavItem(): AddToFav {
        val addToFav = AddToFav()
        addToFav.itemId = course.id
        addToFav.itemName = AddToFav.ItemType.WEBINAR.value
        return addToFav
    }
}