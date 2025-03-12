package com.online.course.manager.player

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.online.course.manager.Utils
import com.online.course.model.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.io.IOException
import kotlin.math.roundToLong

class YoutubeVideoPlayerHelper constructor(
    private val youTubePlayerView: YouTubePlayerView,
    private val youTubePlayer: YouTubePlayer
) : AbstractYouTubePlayerListener(), PlayerHelper.Player {

    companion object {
        private const val TAG = "YoutubeVideoPlayerHelpe"
    }

    // panel is used to intercept clicks on the WebView, I don't want the user to be able to click the WebView directly.
    private lateinit var mDuration: String

    private var listener: PlayerHelper.Listener? = null
    private var mute = false
    private var playing = false
    private var transparent = false
    private var videoWasPlayingAndStopped = false
    private var mCurrentPosition = 0L
    private var loadCount = 0
    private var injected = false


    override fun setOnCallbackListener(listener: PlayerHelper.Listener) {
        this.listener = listener
    }

    override fun onReady(youTubePlayer: YouTubePlayer) {
        Log.e("YoutubeVideoPlayerHelp", "onReady")
    }

    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
        super.onError(youTubePlayer, error)
        Log.e("YoutubeVideoPlayerHelp", "onError: name:${error.name}\nordinal:${error.ordinal}")
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        if (state == PlayerConstants.PlayerState.PLAYING) {
            listener?.onVideoPlayed()
        } else if (state == PlayerConstants.PlayerState.PAUSED) {
            listener?.onVideoPaused()
        }

        if (state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.PAUSED
            || state == PlayerConstants.PlayerState.VIDEO_CUED || state == PlayerConstants.PlayerState.BUFFERING
        ) {
            if (!transparent) {
                transparent = true
                listener?.changeVideoBg(transparent)
            }

        } else {
            if (transparent) {
                transparent = false
                listener?.changeVideoBg(transparent)
            }

            if (state == PlayerConstants.PlayerState.ENDED) {
                listener?.onFinished()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        mCurrentPosition = second.roundToLong()
        if (listener == null || !this::mDuration.isInitialized) return
        val currentPosition = listener?.timeToString((second * 1000).roundToLong())
        currentPosition?.let { listener?.onUpdateCurrentPosition(it, mDuration) }
    }

    @SuppressLint("SetTextI18n")
    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        if (listener == null) return
        mDuration = listener!!.timeToString((duration * 1000).roundToLong())!!
    }

    override fun muteUnmuteVideo() {
        mute = if (mute) {
            youTubePlayer.unMute()
            false
        } else {
            youTubePlayer.mute()
            true
        }

        listener?.onMute(mute)
    }

    override fun playPauseVideo() {
        playing = if (playing) {
            youTubePlayer.pause()
            listener?.onVideoPaused()
            false
        } else {
            youTubePlayer.play()
            listener?.onVideoPlayed()
            true
        }
    }

    override fun initPlayer(id: String, start: Long?) {
        if (start == null) {
            youTubePlayer.cueVideo(id, 0f)
        } else {
            youTubePlayer.cueVideo(id, start.toFloat())
        }
    }

    override fun savePlayerState() {
        if (playing) {
            videoWasPlayingAndStopped = true
            playPauseVideo()
        }
    }

    override fun restorePlayerState() {
        if (videoWasPlayingAndStopped) {
            videoWasPlayingAndStopped = false
            playPauseVideo()
        }
    }

    override fun getPlayerType(): PlayerHelper.Type {
        return PlayerHelper.Type.YOUTUBE
    }

    override fun seekTo(position: Long) {
        youTubePlayer.seekTo(position.toFloat())
    }

    override fun initPlayerFromState(playerState: PlayerState) {
        initPlayer(
            Utils.extractFileNameFromUrl(playerState.path),
            playerState.currentPosition
        )

        if (playerState.isPlaying) {
            playPauseVideo()
        }
    }

    override fun release() {
        youTubePlayerView.release()
    }

    override fun getCurrentPosition(): Long = mCurrentPosition

    override fun isPlaying(): Boolean = playing

    fun hideYouTubeShareBtn() {
        val player = youTubePlayer as WebView

        player.clearCache(true); //we will need to reload www-player.css later
        player.webViewClient = object : WebViewClient() {

            override fun shouldInterceptRequest(
                view: WebView?,
                wrr: WebResourceRequest
            ): WebResourceResponse? {
                val url = wrr.url.toString()
                val b = url.contains("youtube.com") && url.contains("www-player.css")
                if (b) {
                    // find iframe player css file
                    val resp = doInject(url)
                    if (resp != null) {
                        injected = true
                        Log.d(TAG, "shouldInterceptRequest: injected:$injected")
                        return resp
                    }
                }
                return super.shouldInterceptRequest(view, wrr)
            }

            override fun onPageFinished(view: WebView, url: String?) {
                Log.d(TAG, "onPageFinished: injected:$injected loadCount:$loadCount")

                if (!injected && loadCount++ < 3) // prevent loop
                    view.reload() // we need reload because we set WebViewClient
                // after WebView started loading the iframe
                // only once
            }

            private fun doInject(url: String): WebResourceResponse? {
                try {
                    val request = Request.Builder()
                        .url(url)
                        .build()

                    val response = OkHttpClient()
                        .newCall(request)
                        .execute()

                    val fin = response.body?.string()
                        .toString() + " .ytp-chrome-top-buttons {display: none !important;}"

                    Log.d(TAG, "doInject")

                    return WebResourceResponse(
                        "text/css",
                        "UTF-8",
                        ByteArrayInputStream(fin.toByteArray())
                    )
                } catch (e: IOException) {
                }
                return null
            }
        }
    }
}