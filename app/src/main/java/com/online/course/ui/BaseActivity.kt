package com.online.course.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.online.course.manager.App
import com.online.course.model.Language
import com.online.course.ui.frag.ErrorFrag
import java.util.*

open class BaseActivity : AppCompatActivity() {

    companion object {
        var language: Language? = null
    }

    override fun attachBaseContext(newBase: Context?) {
        try {
            super.attachBaseContext(newBase?.setAppLocale(language!!.code))
        } catch (ex: NullPointerException) {
            try {
                super.attachBaseContext(newBase)
            } catch (ex: NullPointerException) {
            }
        }
    }

    private fun Context.setAppLocale(language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return createConfigurationContext(config)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.currentActivity = this
    }

    override fun onResume() {
        super.onResume()
        App.currentActivity = this
    }

    override fun onBackPressed() {
        if (ErrorFrag.isFragVisible) {
            App.showExitDialog(this)
        } else {
            super.onBackPressed()
        }
    }
}