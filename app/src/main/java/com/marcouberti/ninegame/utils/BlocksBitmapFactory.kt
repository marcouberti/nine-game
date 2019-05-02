package com.marcouberti.ninegame.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.marcouberti.ninegame.R

object BlocksBitmapFactory {

    val map = mutableMapOf<Int, Bitmap?>()

    fun blockAt(ctx: Context, i: Int): Bitmap {
        if(map[i] == null) {
            map[i] = when(i % 9) {
                0 -> BitmapFactory.decodeResource(ctx.resources, R.drawable.block_1)
                else -> BitmapFactory.decodeResource(ctx.resources, R.drawable.block_1)
            }

        }
        return map[i]!!
    }
}