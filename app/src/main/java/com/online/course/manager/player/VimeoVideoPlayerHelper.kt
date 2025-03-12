package com.online.course.manager.player

import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerReadyListener
import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerStateListener
import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerTimeListener
import com.ct7ct7ct7.androidvimeoplayer.model.PlayerState
import com.ct7ct7ct7.androidvimeoplayer.view.DefaultControlPanelView
import com.ct7ct7ct7.androidvimeoplayer.view.VimeoPlayerView
import com.online.course.manager.Utils
import kotlin.math.roundToLong

class VimeoVideoPlayerHelper constructor(private val vimeoPlayer: VimeoPlayerView) :
    VimeoPlayerStateListener, VimeoPlayerTimeListener, PlayerHelper.Player, VimeoPlayerReadyListener {

    companion object {
        private const val TAG = "VimeoVideoPlayerHelper"
    }

    private var listener: PlayerHelper.Listener? = null
    private var mPlayerState: com.online.course.model.PlayerState? = null
    private var mute = false
    private var videoWasPlayingAndStopped = false
    private var mCurrentPosition = 0L

    init {
        vimeoPlayer.addReadyListener(this)
        vimeoPlayer.addStateListener(this)
        vimeoPlayer.addTimeListener(this)
    }

    override fun setOnCallbackListener(listener: PlayerHelper.Listener) {
        this.listener = listener
    }

    override fun onReady(title: String?, duration: Float) {
        if (mPlayerState != null) {
            if (mPlayerState!!.isPlaying) {
                playPauseVideo()
            }
            seekTo(mPlayerState!!.currentPosition)
        }
    }

    override fun onInitFailed() {
    }

    override fun onPlaying(duration: Float) {
        listener?.onVideoPlayed()
    }

    override fun onPaused(seconds: Float) {
        listener?.onVideoPaused()
    }

    override fun onEnded(duration: Float) {
        listener?.onFinished()
    }

    override fun onCurrentSecond(second: Float) {
        mCurrentPosition = second.roundToLong()
        if (listener == null) return
        val currentPosition = listener?.timeToString((second * 1000).roundToLong())
        currentPosition?.let { listener?.onUpdateCurrentPosition(it, "") }
    }

    override fun muteUnmuteVideo() {
        mute = if (mute) {
            vimeoPlayer.setVolume(0.7f)
            false
        } else {
            vimeoPlayer.setVolume(0f)
            true
        }

        listener?.onMute(mute)
    }

    override fun playPauseVideo() {
        when (vimeoPlayer.playerState) {
            PlayerState.PLAYING -> {
                vimeoPlayer.pause()
                listener?.onVideoPaused()
            }
            PlayerState.PAUSED, PlayerState.READY -> {
                vimeoPlayer.play()
                listener?.onVideoPlayed()
            }
            PlayerState.ENDED -> {
                vimeoPlayer.seekTo(0f)
                vimeoPlayer.play()
                listener?.onVideoPlayed()
            }
        }
    }

    override fun initPlayer(id: String, start: Long?) {
        vimeoPlayer.initialize(id.toInt())
    }

    override fun savePlayerState() {
        if (isPlaying()) {
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

    override fun release() {
        vimeoPlayer.onDestroy()
    }

    override fun getPlayerType(): PlayerHelper.Type {
        return PlayerHelper.Type.VIMEO
    }

    override fun seekTo(position: Long) {
        vimeoPlayer.seekTo(position.toFloat())
    }

    override fun initPlayerFromState(playerState: com.online.course.model.PlayerState) {
        mPlayerState = playerState
        initPlayer(Utils.extractFileNameFromUrl(playerState.path), null)
    }

    override fun getCurrentPosition(): Long = vimeoPlayer.currentTimeSeconds.roundToLong()
    override fun isPlaying(): Boolean = vimeoPlayer.playerState == PlayerState.PLAYING
}