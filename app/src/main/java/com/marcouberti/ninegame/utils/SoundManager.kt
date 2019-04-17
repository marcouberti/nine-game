package com.marcouberti.ninegame.utils

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.marcouberti.ninegame.GameApplication
import com.marcouberti.ninegame.R

class SoundManager: LifecycleObserver {

    val player = MediaPlayer.create(GameApplication.context,R.raw.soundtrack)

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun connectListener() {
        player.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun disconnectListener() {
        player.pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroyListener() {
        player.stop()
        player.release()
    }

}