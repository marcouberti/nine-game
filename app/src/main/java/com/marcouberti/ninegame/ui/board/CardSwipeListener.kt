package com.marcouberti.ninegame.ui.board

import com.marcouberti.ninegame.utils.OnSwipeListener

interface CardSwipeListener {
    fun onSwipe(position: Pair<Int, Int>?, direction: OnSwipeListener.Direction)
    fun onTap(position: Pair<Int, Int>?)
}