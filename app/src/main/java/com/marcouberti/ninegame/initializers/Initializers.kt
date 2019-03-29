package com.marcouberti.ninegame.initializers

import com.marcouberti.ninegame.model.*
import kotlin.random.Random

val DefaultCardInitializer = { card:Card -> Unit
    card.clearAll()
    card[Random.nextInt(1, (card.width*card.width)+1)] = true
}

val DefaultBoardInitializer = { board:Board -> Unit
    board.clearAll()
    board[Pair(1,1)] = Card(3).apply{this.init()}
    board[Pair(1,board.width)] = Card(3).apply{this.init()}
}