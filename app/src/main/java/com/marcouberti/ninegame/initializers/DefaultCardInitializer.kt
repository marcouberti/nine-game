package com.marcouberti.ninegame.initializers

import com.marcouberti.ninegame.model.Card
import com.marcouberti.ninegame.model.clearAll
import com.marcouberti.ninegame.model.set
import kotlin.random.Random

val DefaultCardInitializer = { card:Card -> Unit
    card.clearAll()
    card[Random.nextInt(1, (card.width*card.width)+1)] = true
}

val a = { i: Int -> i + 1 }