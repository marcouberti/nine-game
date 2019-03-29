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