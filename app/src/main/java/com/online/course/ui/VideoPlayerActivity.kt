package com.online.course.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.online.course.databinding.ActivityVideoPlayerBinding
import com.online.course.manager.App
import com.online.course.manager.player.FileVideoPlayerHelper
import com.online.course.manager.player.PlayerHelper
import com.online.course.manager.player.VimeoVideoPlayerHelper
import com.online.course.manager.player.YoutubeVideoPlayerHelper
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class VideoPlayerActivity : AppCompatActivity() {
    private val hideHandler = Handler(Looper.getMainLooper())
    private lateinit var mBinding: ActivityVideoPlayerBinding
    private lateinit var mVideoHelper: PlayerHelper.Player
    private var mIsStopped = false

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, mBinding.videoPlayer).let { controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            mBinding.videoPlayer
        ).show(WindowInsetsCompat.Type.systemBars())
    }

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
//        mBinding.videoPlayer.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LOW_PROFILE or
//                    View.SYSTEM_UI_FLAG_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        hideSystemUI()
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        isFullscreen = true

        val playerState = PlayerHelper.playerState!!

        if (playerState.playerType == PlayerHelper.Type.LOCAL) {
            mVideoHelper = FileVideoPlayerHelper(this)
            mVideoHelper.initPlayerFromState(playerState)

            mBinding.videoPlayer.player = (mVideoHelper as FileVideoPlayerHelper).player
            mBinding.videoPlayer.visibility = View.VISIBLE


        } else if (playerState.playerType == PlayerHelper.Type.YOUTUBE) {
            val youTubePlayerView = mBinding.youtubePlayerView

            val listener: YouTubePlayerListener = object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    if (!this@VideoPlayerActivity::mVideoHelper.isInitialized) {
                        mVideoHelper = YoutubeVideoPlayerHelper(youTubePlayerView, youTubePlayer)
                    }
                    youTubePlayer.addListener(mVideoHelper as YoutubeVideoPlayerHelper)
                    (mVideoHelper as YoutubeVideoPlayerHelper).hideYouTubeShareBtn()
                    mVideoHelper.initPlayerFromState(playerState)
                }
            }

            youTubePlayerView.initialize(listener)
            youTubePlayerView.visibility = View.VISIBLE
        } else {
            mVideoHelper = VimeoVideoPlayerHelper(mBinding.vimeoPlayerView)
            mVideoHelper.initPlayerFromState(playerState)
            mBinding.vimeoPlayerView.visibility = View.VISIBLE
        }

        val requestLandscape = intent.getBooleanExtra(App.REQUEST_LANDSCAPE, false)
        if (requestLandscape) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        // Set up the user interaction to manually show or hide the system UI.
//        mBinding.videoPlayer.setOnClickListener { toggle() }
        mBinding.videoPlayer.setShowFastForwardButton(true)

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById<Button>(R.id.dummy_button).setOnTouchListener(delayHideTouchListener)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    override fun onPause() {
        savePlayerState()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        mIsStopped = true
        mVideoHelper.savePlayerState()
    }


    override fun onResume() {
        super.onResume()
        if (mIsStopped) {
            mIsStopped = false
            mVideoHelper.restorePlayerState()
        }
    }

    override fun onDestroy() {
        mVideoHelper.release()
        super.onDestroy()
    }

    private fun savePlayerState() {
        val playerState = PlayerHelper.playerState!!
        playerState.currentPosition = mVideoHelper.getCurrentPosition()
        playerState.isPlaying = mVideoHelper.isPlaying()
        PlayerHelper.playerState = playerState
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
//        mBinding.videoPlayer.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        showSystemUI()
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}