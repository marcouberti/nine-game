package com.marcouberti.ninegame.model

import android.os.Parcelable
import com.marcouberti.ninegame.initializers.DefaultBoardInitializer
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Board(val width: Int, val cards: MutableMap<Pair<Int, Int>, Card?> = mutableMapOf()): Parcelable

operator fun Board.set(pair: Pair<Int,Int>, card: Card?) {
    cards[pair] = card
}

operator fun Board.get(pair: Pair<Int, Int>): Card? {
    return cards[pair]
}

fun Board.isFull(): Boolean {
    var full = true
    for(i in 1..width) {
        for(j in 1..width) {
            full = full && (this[Pair(i,j)] != null)
        }
    }
    return full
}

fun Board.clearAll() {
    cards.clear()
}

fun Board.init(initializer: (card: Board) -> Unit = DefaultBoardInitializer) {
    initializer(this)
}

fun Board.addCard() {
    val frees = mutableListOf<Pair<Int, Int>>()
    for(i in 1..width) {
        for(j in 1..width) {
            if(this[Pair(i,j)] == null) {
                //this[Pair(i,j)] = Card(3).apply { init() }
                //return
                frees.add(Pair(i,j))
            }
        }
    }
    frees.shuffle()
    this[frees[0]] = Card(3).apply { init() }
}

fun Board.mergeable(pos1: Pair<Int, Int>, pos2: Pair<Int, Int>): Boolean {
    if(pos1 == pos2) return false
    if(this[pos1] == null || this[pos2] == null) return false
    if(pos1.first == pos2.first) {//same row
        val middleCell = cards.keys.any { pos -> this[pos] != null && pos.between(pos1, pos2)}
        if(middleCell) return false
    }else if(pos1.second == pos2.second) {// same column
        val middleCell = cards.keys.any { pos -> this[pos] != null && pos.between(pos1, pos2)}
        if(middleCell) return false
    }else return false // no same row or column
    return this[pos1]?.mergeable(this[pos2]) == true
}

fun Board.merge(pos1: Pair<Int, Int>, pos2: Pair<Int, Int>): Int? {
    return if(mergeable(pos1, pos2)) {
        val cell1 = this[pos1]!!
        val cell2 = this[pos2]!!
        val weights = cell1.weight() + cell2.weight()
        if(weights == cell1.width*cell1.width) {// pouf!
            this[pos2] = null
        }else {
            val merged = cell1.merge(cell2)
            this[pos2] = merged
        }
        this[pos1] = null
        weights
    }else null
}

fun Board.move(pos1: Pair<Int, Int>, pos2: Pair<Int, Int>): Boolean {
    return if(movable(pos1, pos2)) {
        val cell1 = this[pos1]!!
        this[pos2] = cell1
        this[pos1] = null
        true
    }else false
}

fun Board.movable(pos1: Pair<Int, Int>, pos2: Pair<Int, Int>): Boolean {
    if(pos1 == pos2) return false
    if(this[pos1] == null || this[pos2] != null) return false
    if(pos1.first == pos2.first) {//same row
        val middleCell = cards.keys.any { pos -> this[pos] != null && pos.between(pos1, pos2)}
        if(middleCell) return false
    }else if(pos1.second == pos2.second) {// same column
        val middleCell = cards.keys.any { pos -> this[pos] != null && pos.between(pos1, pos2)}
        if(middleCell) return false
    }else return false // no same row or column
    return true
}

fun Pair<Int, Int>.between(pos1: Pair<Int, Int>, pos2: Pair<Int, Int>): Boolean {
    if(this.first == pos1.first && this.first == pos2.first) {//same row
        val column1 = if(pos1.second < pos2.second) pos1.second else pos2.second
        val column2 = if(pos1.second < pos2.second) pos2.second else pos1.second
        return this.second in (column1 + 1)..(column2 - 1)
    }else if(this.second == pos1.second && this.second == pos2.second) {// same column
        val row1 = if(pos1.first < pos2.first) pos1.first else pos2.first
        val row2 = if(pos1.first < pos2.first) pos2.first else pos1.first
        return this.first in (row1 + 1)..(row2 - 1)
    }else return false // no same row or column
}