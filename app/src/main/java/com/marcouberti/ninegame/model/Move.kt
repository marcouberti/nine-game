package com.marcouberti.ninegame.model

enum class MoveType{
    NONE,
    ROTATION,
    MOVE,
    MOVE_AND_MERGE,
    NEW
}

data class Move(val type: MoveType = MoveType.NONE, val points: Int? = null, val from: Pair<Int, Int>? = null, val to: Pair<Int, Int>? = null)