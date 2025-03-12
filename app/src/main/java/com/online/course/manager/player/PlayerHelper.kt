package com.online.course.manager.player

import com.online.course.model.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import java.io.Serializable

object PlayerHelper {
    var playerState: PlayerState? = null

    enum class Type : Serializable {
        LOCAL, YOUTUBE, VIMEO
    }

    interface Listener {
        fun onMute(mute: Boolean) {}
        fun onVideoPaused() {}
        fun onVideoPlayed() {}
        fun onYouTubePlayerReady(youTubePlayer: YouTubePlayer) {}
        fun onUpdateCurrentPosition(currentPosition: String, videoDuration: String) {}
        fun onSurfaceSizeChanged(width: Int, height: Int) {}
        fun onFinished() {}
        fun changeVideoBg(transparent: Boolean) {}
        fun timeToString(millis: Long): String? {
            return null
        }
    }

    interface Player {
        fun initPlayer(id: String, start: Long?)
        fun playPauseVideo()
        fun release()
        fun muteUnmuteVideo()
        fun savePlayerState()
        fun restorePlayerState()
        fun getPlayerType(): Type
        fun setOnCallbackListener(listener: Listener)
        fun isPlaying(): Boolean
        fun getCurrentPosition(): Long
        fun seekTo(position: Long)
        fun initPlayerFromState(playerState: PlayerState)
    }
}