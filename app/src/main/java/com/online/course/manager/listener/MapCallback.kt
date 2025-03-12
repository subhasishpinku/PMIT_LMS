package com.online.course.manager.listener

interface MapCallback<T, U> {
    fun onMapReceived(map: Map<T, U>)
}