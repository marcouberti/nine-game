package com.marcouberti.ninegame.initializers

import com.marcouberti.ninegame.model.*
import kotlin.random.Random

val DefaultCardInitializer = { card:Card -> Unit
    card.clearAll()
    card[Random.nextInt(1, (card.width*card.width)+1)] = true
}

val RandomCardInitializer = { card:Card -> Unit
    card.clearAll()
    repeat(card.width) {
        card[Random.nextInt(1, (card.width * card.width) + 1)] = Random.nextInt(1,4) == 1
    }
    if(card.isEmpty()) card.init(DefaultCardInitializer)
}

val DefaultBoardInitializer = { board:Board -> Unit
    board.clearAll()
    val pos1 = Pair(Random.nextInt(1, board.width),Random.nextInt(1, board.width))
    var pos2 = Pair(Random.nextInt(1, board.width),Random.nextInt(1, board.width))
    while(pos2 == pos1) {
        pos2 = Pair(Random.nextInt(1, board.width),Random.nextInt(1, board.width))
    }
    board[pos1] = Card().apply{this.init()}
    board[pos2] = Card().apply{this.init()}
}