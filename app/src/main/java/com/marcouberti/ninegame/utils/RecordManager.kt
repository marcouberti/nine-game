package com.marcouberti.ninegame.utils

import android.content.Context

object RecordManager {

    fun saveRecordIfAny(ctx: Context, points: Int): Int {
        val sp = ctx.getSharedPreferences("NINE_GAME", Context.MODE_PRIVATE)
        val record =  sp.getInt("RECORD", 0)
        if(record < points) {
            sp.edit().apply {
                putInt("RECORD", points)
                apply()
            }
            return points
        }
        return record
    }
}