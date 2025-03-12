package com.online.course.manager.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.online.course.model.PlayerState
import java.io.File
import java.lang.NullPointerException

class FileVideoPlayerHelper(context: Context) : Player.Listener,
    PlayerHelper.Player {

    private var mVideoDuration: String? = null
    private var mHandler: Handler? = null
    private var mVideoWasPlayingAndStopped = false
    private var mUpdatePositionRunnable: Runnable? = null
    private var mUserVolume = 0f
    private var mCallback: PlayerHelper.Listener? = null
    val player get() = mPlayer!!
    private var mPlayer: ExoPlayer? = null

    init {
        mPlayer = ExoPlayer.Builder(context).build()
    }

    override fun setOnCallbackListener(listener: PlayerHelper.Listener) {
        mCallback = listener
    }

    override fun isPlaying(): Boolean {
        return mPlayer!!.isPlaying
    }

    override fun getCurrentPosition(): Long {
        return mPlayer!!.currentPosition
    }

    override fun initPlayer(id: String, start: Long?) {
        initPlayer(Uri.parse(id))
        if (start != null) {
            mPlayer!!.seekTo(start)
        }
    }

    override fun seekTo(position: Long) {
        mPlayer!!.seekTo(position)
    }

    fun initPlayer(file: File) {
        initPlayer(Uri.fromFile(file))
    }

    private fun initPlayer(uri: Uri) {
        // Build the media item.
        val mediaItem = MediaItem.fromUri(uri)
        // Set the media item to be played.
        mPlayer!!.setMediaItem(mediaItem)
        mPlayer!!.playWhenReady = false

        mPlayer!!.repeatMode = Player.REPEAT_MODE_OFF
        // Prepare the player.
        mPlayer!!.prepare()
        // Start the playback.
        mPlayer!!.addListener(this)
    }

    override fun initPlayerFromState(playerState: PlayerState) {
        val uri = if (playerState.isLocalFile) {
            Uri.fromFile(File(playerState.path))
        } else {
            Uri.parse(playerState.path)
        }

        // Build the media item.
        val mediaItem = MediaItem.fromUri(uri)

        // Set the media item to be played.
        mPlayer!!.setMediaItem(mediaItem)
        mPlayer!!.playWhenReady = playerState.isPlaying
        if (playerState.currentPosition > 0) {
            mPlayer!!.seekTo(playerState.currentPosition)
        }

        player.repeatMode = Player.REPEAT_MODE_OFF
        // Prepare the player.
        player.prepare()
    }

    override fun playPauseVideo() {
        if (mPlayer!!.isPlaying) {
            mCallback?.onVideoPaused()
            mPlayer!!.pause()
            stopUpdatingVideoPosition()
        } else {
            mVideoWasPlayingAndStopped = false
            mCallback?.onVideoPlayed()
            if (mPlayer!!.playbackState == Player.STATE_ENDED) {
                mPlayer!!.seekTo(0)
            }
            updateVideoPosition()
            mPlayer!!.play()
        }
    }

    fun updateVideoPosition() {
        if (mCallback == null) return

        if (mHandler == null) {
            mHandler = Handler(Looper.getMainLooper())
            if (mUpdatePositionRunnable == null) {
                mUpdatePositionRunnable = object : Runnable {
                    override fun run() {
                        setVideoPosition()
                        mHandler!!.postDelayed(this, 100)
                    }
                }
            }
            mHandler!!.postDelayed(mUpdatePositionRunnable!!, 100)
        }
    }

    fun stopUpdatingVideoPosition() {
        mUpdatePositionRunnable?.let { mHandler?.removeCallbacks(it) }
        mUpdatePositionRunnable = null
        mHandler = null
    }

    fun setVideoPosition() {
        if (mCallback == null) return
        try {
            if (mVideoDuration == null) {
                mVideoDuration = mCallback?.timeToString(mPlayer!!.duration)
            }

            val currentPosition = mCallback?.timeToString(mPlayer!!.currentPosition)

            currentPosition?.let { mCallback?.onUpdateCurrentPosition(it, mVideoDuration!!) }
        } catch (ex: NullPointerException) {
        }
    }

    override fun release() {
        mPlayer?.playWhenReady = false
        mPlayer?.stop()
        mPlayer?.release()
        mPlayer = null
    }

    override fun muteUnmuteVideo() {
        if (mPlayer!!.volume == 0f) {
            mCallback?.onMute(false)
            if (mUserVolume == 0f) {
                mPlayer!!.volume = 0.4f
            } else {
                mPlayer!!.volume = mUserVolume
            }
        } else {
            mCallback?.onMute(true)
            mUserVolume = mPlayer!!.volume
            mPlayer!!.volume = 0f
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        if (playbackState == ExoPlayer.STATE_READY && mVideoDuration == null) {
            setVideoPosition()
        } else if (playbackState == ExoPlayer.STATE_ENDED) {
            mCallback?.onFinished()
            stopUpdatingVideoPosition()
        }
    }

    override fun onSurfaceSizeChanged(width: Int, height: Int) {
        super.onSurfaceSizeChanged(width, height)
        if (width > 0 && height > 0) {
            mCallback?.onSurfaceSizeChanged(width, height)
        }
    }

    override fun savePlayerState() {
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mVideoWasPlayingAndStopped = true
            playPauseVideo()
        }
    }

    override fun restorePlayerState() {
        if (mVideoWasPlayingAndStopped) {
            mVideoWasPlayingAndStopped = false
            if (mPlayer != null) {
                playPauseVideo()
            }
        }
    }

    override fun getPlayerType(): PlayerHelper.Type {
        return PlayerHelper.Type.LOCAL
    }
}