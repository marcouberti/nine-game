package com.marcouberti.ninegame.model

import android.os.Parcelable
import com.marcouberti.ninegame.initializers.DefaultCardInitializer
import com.marcouberti.ninegame.initializers.RandomCardInitializer
import kotlinx.android.parcel.Parcelize

const val CARD_SIZE = 3

@Parcelize
data class Card(val width: Int = CARD_SIZE, val blocks: MutableList<Boolean> = mutableListOf()): Parcelable {
    init {
        if(blocks.size != width*width) {
            blocks.clear()
            for (i in 1..width*width) blocks.add(false)
        }
    }
}

operator fun Card.set(pos: Int, value: Boolean) {
    blocks[pos-1] = value
}

operator fun Card.get(pos: Int): Boolean {
    return blocks[pos-1]
}

fun Card.rotate() {

    var mat = arrayOf<Array<Boolean>>()
    for (i in 1..width) {
        var row = arrayOf<Boolean>()
        for (j in 1..width) {
            row += (blocks[(i-1)*width + (j-1)])
        }
        mat += row
    }

    // Consider all squares one by one
    for (x in 0 until width / 2) {
        // Consider elements in group of 4 in current square
        for (y in x until width - x - 1) {
            // store current cell in temp variable
            val temp = mat[x][y]

            // move values from right to top
            mat[x][y] = mat[y][width - 1 - x]

            // move values from bottom to right
            mat[y][width - 1 - x] = mat[width - 1 - x][width - 1 - y]

            // move values from left to bottom
            mat[width - 1 - x][width - 1 - y] = mat[width - 1 - y][x]

            // assign temp to left
            mat[width - 1 - y][x] = temp
        }
    }

    for (i in 0 until width) {
        for (j in 0 until width) {
            blocks[i*width + j] = mat[i][j]
        }
    }
}

fun Card.check(vararg pos: Int) {
    for(p in pos) this[p] = true
}

fun Card.uncheck(vararg pos: Int) {
    for(p in pos) this[p] = false
}

fun Card.clearAll() {
    for(i in 1 .. width*width) uncheck(i)
}

fun Card.init(initializer: (card: Card) -> Unit = RandomCardInitializer) {
    initializer(this)
}

fun Card.isFull(): Boolean {
    return blocks.all { it }
}

fun Card.isEmpty(): Boolean {
    return blocks.none { it }
}

fun Card.weight(): Int {
    return blocks.count { it }
}

fun Card.merge(that: Card): Card? {
    return if(mergeable(that)) {
        val mergedBlocks = this.blocks.mapIndexed { index, b -> b || that.blocks[index] }
        Card(width, mergedBlocks.toMutableList())
    }else null
}

fun Card.mergeable(that: Card?): Boolean {
    if(that == null) return false
    if(this.width != that.width) return false
    return !this.blocks.zip(that.blocks).any { it.first && it.second }
}