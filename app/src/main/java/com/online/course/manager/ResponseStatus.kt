package com.online.course.manager

enum class ResponseStatus(private val type: String) {

    AUTH_GO_TO_STEP2("go_step_2"),
    AUTH_GO_TO_STEP3("go_step_3");

    fun value(): String {
        return type
    }
}