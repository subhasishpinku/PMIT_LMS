package com.online.course.manager

import android.content.Context
import android.content.SharedPreferences
import com.online.course.model.Language

class PrefManager (_context: Context) {

    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor


    var isFirstTimeLaunch: Boolean
        get() {
            return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        }
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.apply()
        }

    var skipLogin: Boolean
        get() {
            return pref.getBoolean(SKIP_LOGIN, false)
        }
        set(skipLogin) {
            editor.putBoolean(SKIP_LOGIN, skipLogin)
            editor.apply()
        }

    var notifId: Int
        get() {
            return pref.getInt(NOTIF_ID, 10)
        }
        set(notifId) {
            editor.putInt(NOTIF_ID, notifId)
            editor.apply()
        }

    var language: Language?
        get() {
            val name = pref.getString(LANGUAGE_NAME, null)
            val code = pref.getString(LANGUAGE_CODE, null)

            if (name != null && code != null){
                val lng = Language()
                lng.name = name
                lng.code = code
                return lng
            }

            return null
        }
        set(language) {
            editor.putString(LANGUAGE_NAME, language?.name)
            editor.putString(LANGUAGE_CODE, language?.code)
            editor.apply()
        }

    init {
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()
    }

    companion object {
        private const val IS_FIRST_TIME_LAUNCH = "IS_FIRST_TIME_LAUNCH"
        private const val SKIP_LOGIN = "SKIP_LOGIN"
        private const val LANGUAGE_NAME = "LANGUAGE_NAME"
        private const val LANGUAGE_CODE= "LANGUAGE_CODE"
        private const val PREF_NAME = "APP_CONFIG"
        private const val NOTIF_ID = "NOTIF_ID"
    }

}