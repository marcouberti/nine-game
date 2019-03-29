package com.marcouberti.ninegame.model

import kotlin.random.Random

data class Card(val width: Int, val blocks: MutableList<Boolean> = mutableListOf()) {
    init {
        if(blocks.size != width*width) {
            blocks.clear()
            for (i in 1..width*width) blocks.add(false)
            initRandom()
        }
    }
}

operator fun Card.set(pos: Int, value: Boolean) {
    blocks[pos-1] = value
}

operator fun Card.get(pos: Int): Boolean {
    return blocks[pos-1]
}

fun Card.check(pos: Int) {
    this[pos] = true
}

fun Card.uncheck(pos: Int) {
    this[pos] = false
}

fun Card.clearAll() {
    for(i in 1 .. width*width) uncheck(i)
}

fun Card.initRandom() {
    clearAll()
    this[Random.nextInt(1, (width*width)+1)] = true
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
    if(this == that) return false
    if(this.width != that.width) return false
    return !this.blocks.zip(that.blocks).any { it.first && it.second }
}