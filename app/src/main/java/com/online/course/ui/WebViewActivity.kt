package com.online.course.ui

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.online.course.databinding.ActivityWebViewBinding
import com.online.course.manager.App


class WebViewActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        init()
    }

    private fun init() {
        val webView = mBinding.webView
        webView.webViewClient = WebViewClient()

        val webViewSettings = webView.settings
        webViewSettings.javaScriptCanOpenWindowsAutomatically = true
        webViewSettings.mediaPlaybackRequiresUserGesture = false
        webViewSettings.javaScriptEnabled = true
        webViewSettings.domStorageEnabled = true
        webViewSettings.databaseEnabled = true
        webViewSettings.minimumFontSize = 1
        webViewSettings.minimumLogicalFontSize = 1
        webViewSettings.allowFileAccess = true
        webViewSettings.allowContentAccess = true

        val url = intent.getStringExtra(App.URL)!!
        if (url.contains("<iframe", true)) {
            webView.loadData(url, "text/html; charset=utf-8", "UTF-8")
        } else {
            webView.loadUrl(url)
        }
    }

    override fun onPause() {
        super.onPause()
        mBinding.webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mBinding.webView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.webView.destroy()
    }
}