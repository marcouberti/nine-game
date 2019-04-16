package com.marcouberti.ninegame.utils

import android.content.Context
import android.os.VibrationEffect
import android.os.Build
import android.os.Vibrator
import com.marcouberti.ninegame.GameApplication

object VibrationManager {
    fun vibrate() {
        val v = GameApplication.context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v?.vibrate(60)
        }
    }
}