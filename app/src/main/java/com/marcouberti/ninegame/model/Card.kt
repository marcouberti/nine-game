package com.marcouberti.ninegame.model

import android.os.Parcelable
import com.marcouberti.ninegame.initializers.DefaultCardInitializer
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Card(val width: Int, val blocks: MutableList<Boolean> = mutableListOf()): Parcelable {
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
    val old = mutableListOf<Boolean>().apply {
        addAll(blocks)
    }
    for(i in 0 until blocks.size) {
        when(i) {
            0 -> blocks[2] = old[0]
            1 -> blocks[5] = old[1]
            2 -> blocks[8] = old[2]
            3 -> blocks[1] = old[3]
            4 -> blocks[4] = old[4]
            5 -> blocks[7] = old[5]
            6 -> blocks[0] = old[6]
            7 -> blocks[3] = old[7]
            8 -> blocks[6] = old[8]
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

fun Card.init(initializer: (card: Card) -> Unit = DefaultCardInitializer) {
    initializer(this)
}

fun Card.isFull(): Boolean {
    return blocks.all { it }
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