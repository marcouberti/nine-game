package com.marcouberti.ninegame.utils

import android.media.MediaPlayer
import android.media.SoundPool
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.marcouberti.ninegame.GameApplication
import com.marcouberti.ninegame.R
import android.media.AudioManager



class SoundManager: LifecycleObserver {

    var player: MediaPlayer? = MediaPlayer.create(GameApplication.context, R.raw.soundtrack)
    var soundPool: SoundPool? = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
    val sfx1: Int
    val sfx2: Int
    val sfx3: Int

    init {
        sfx1 = soundPool?.load(GameApplication.context, R.raw.sfx1, 1)?:0
        sfx2 = soundPool?.load(GameApplication.context, R.raw.sfx2, 1)?:0
        sfx3 = soundPool?.load(GameApplication.context, R.raw.sfx3, 1)?:0
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun connectListener() {
        player?.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun disconnectListener() {
        player?.pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroyListener() {
        player?.stop()
        player?.release()
        player = null

        soundPool?.release()
        soundPool = null
    }

    fun playSfx1() {
        soundPool?.play(sfx1, 1f, 1f, 0, 0, 1f)
    }

    fun playSfx2() {
        soundPool?.play(sfx2, 1f, 1f, 0, 0, 1f)
    }

    fun playSfx3() {
        soundPool?.play(sfx3, 1f, 1f, 0, 0, 1f)
    }
}