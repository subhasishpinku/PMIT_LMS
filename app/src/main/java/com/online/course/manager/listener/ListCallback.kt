package com.online.course.manager.listener

interface ListCallback<T> {
    fun onMapReceived(items: List<T>)
}