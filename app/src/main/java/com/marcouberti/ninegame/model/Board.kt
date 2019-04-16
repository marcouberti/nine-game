package com.marcouberti.ninegame.model

import android.os.Parcelable
import com.marcouberti.ninegame.initializers.DefaultBoardInitializer
import com.marcouberti.ninegame.utils.OnSwipeListener
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

fun Board.addCard(card: Card): Pair<Int,Int> {
    val frees = mutableListOf<Pair<Int, Int>>()
    for(i in 1..width) {
        for(j in 1..width) {
            if(this[Pair(i,j)] == null) {
                //this[Pair(i,j)] = Card().apply { init() }
                //return
                frees.add(Pair(i,j))
            }
        }
    }
    frees.shuffle()
    this[frees[0]] = card
    return frees[0]
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
        val merged = cell1.merge(cell2)
        if(merged?.isFull() == true) {// pouf!
            this[pos2] = null
        }else {
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
    if(pos1.first < 1 || pos1.first > width) return false
    if(pos2.first < 1 || pos2.first > width) return false
    if(pos1.second < 1 || pos1.second > width) return false
    if(pos2.second < 1 || pos2.second > width) return false
    if(pos1.first == pos2.first) {//same row
        val middleCell = cards.keys.any { pos -> this[pos] != null && pos.between(pos1, pos2)}
        if(middleCell) return false
    }else if(pos1.second == pos2.second) {// same column
        val middleCell = cards.keys.any { pos -> this[pos] != null && pos.between(pos1, pos2)}
        if(middleCell) return false
    }else return false // no same row or column
    return true
}

fun Board.slide(pos: Pair<Int, Int>, direction: OnSwipeListener.Direction): Move {
    if(this[pos] == null) return Move()
    // check if the card can be merged
    val p = firstCardInDirection(pos, direction)
    if(p != null) {
        if (this.mergeable(pos, p)) return Move(MoveType.MOVE_AND_MERGE, this.merge(pos,p), pos, p)
        else {
            // otherwise check if we can move it
            val p = lastPositionInDirection(pos, direction)
            if(p != null) return if(this.move(pos, p)) Move(MoveType.MOVE, 0, pos, p) else Move()
        }
    }else {
        // otherwise check if we can move it
        val p = lastPositionInDirection(pos, direction)
        if(p != null) return if(this.move(pos, p)) Move(MoveType.MOVE, 0, pos, p) else Move()
    }
    return Move()
}

fun Board.firstCardInDirection(pos: Pair<Int, Int>, direction: OnSwipeListener.Direction): Pair<Int, Int>? {
    var p = pos
    while(p == pos || p.first in (1..width) && p.second in (1..width)) {
        p = when(direction) {
            OnSwipeListener.Direction.up -> {
                Pair(p.first - 1, p.second)
            }
            OnSwipeListener.Direction.down -> {
                Pair(p.first + 1, p.second)
            }
            OnSwipeListener.Direction.left -> {
                Pair(p.first, p.second - 1)
            }
            OnSwipeListener.Direction.right -> {
                Pair(p.first, p.second + 1)
            }
        }
        if(this[p] != null) return p
    }
    return null
}

fun Board.lastPositionInDirection(pos: Pair<Int, Int>, direction: OnSwipeListener.Direction): Pair<Int, Int>? {
    var p = pos
    var pre: Pair<Int, Int>? = null
    while(p == pos || p.first in (1..width) && p.second in (1..width)) {
        p = when(direction) {
            OnSwipeListener.Direction.up -> {
                Pair(p.first - 1, p.second)
            }
            OnSwipeListener.Direction.down -> {
                Pair(p.first + 1, p.second)
            }
            OnSwipeListener.Direction.left -> {
                Pair(p.first, p.second - 1)
            }
            OnSwipeListener.Direction.right -> {
                Pair(p.first, p.second + 1)
            }
        }
        if(this[p] != null) {
            return pre
        }
        if(p.first in (1..width) && p.second in (1..width)) pre = p
    }
    return pre
}

fun Board.gameOver(): Boolean {
    if(!this.isFull()) return false
    // for each card
    for(i in 1..width) {
        for(j in 1..width) {
            val card    = this[Pair(i,j)]

            val top     = this[Pair(i-1,j)]
            val right   = this[Pair(i,j+1)]
            val bottom  = this[Pair(i+1,j)]
            val left    = this[Pair(i,j-1)]

            if(card != null) {
                var existsMove = false
                repeat(4) {
                    card.rotate()
                    existsMove = existsMove ||
                            (card.mergeable(top)     ||
                            card.mergeable(right)   ||
                            card.mergeable(bottom)  ||
                            card.mergeable(left))
                }
                if(existsMove) return false
            }
        }
    }
    return true
}

fun Pair<Int, Int>.between(pos1: Pair<Int, Int>, pos2: Pair<Int, Int>): Boolean {
    return if(this.first == pos1.first && this.first == pos2.first) {//same row
        val column1 = if(pos1.second < pos2.second) pos1.second else pos2.second
        val column2 = if(pos1.second < pos2.second) pos2.second else pos1.second
        this.second in (column1 + 1) until column2
    }else if(this.second == pos1.second && this.second == pos2.second) {// same column
        val row1 = if(pos1.first < pos2.first) pos1.first else pos2.first
        val row2 = if(pos1.first < pos2.first) pos2.first else pos1.first
        this.first in (row1 + 1) until row2
    }else false // no same row or column
}