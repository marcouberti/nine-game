package com.marcouberti.ninegame

import android.app.Application
import android.content.Context

class GameApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        var context: Context? = null
    }
}