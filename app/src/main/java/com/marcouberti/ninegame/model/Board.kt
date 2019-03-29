package com.marcouberti.ninegame.model

class Board(val width: Int, val cards: MutableMap<Pair<Int, Int>, Card?> = mutableMapOf())

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