package com.marcouberti.ninegame.model

import kotlin.random.Random

data class Cell(val width: Int, val blocks: MutableList<Boolean> = mutableListOf()) {
    init {
        if(blocks.size != width*width) {
            blocks.clear()
            for (i in 1..width*width) blocks.add(false)
            initRandom()
        }
    }
}

operator fun Cell.set(pos: Int, value: Boolean) {
    blocks[pos-1] = value
}

operator fun Cell.get(pos: Int): Boolean {
    return blocks[pos-1]
}

fun Cell.check(pos: Int) {
    this[pos] = true
}

fun Cell.uncheck(pos: Int) {
    this[pos] = false
}

fun Cell.clearAll() {
    for(i in 1 .. width*width) uncheck(i)
}

fun Cell.initRandom() {
    clearAll()
    this[Random.nextInt(1, (width*width)+1)] = true
}

fun Cell.isFull(): Boolean {
    return blocks.all { it }
}

fun Cell.weight(): Int {
    return blocks.count { it }
}

fun Cell.merge(that: Cell): Cell? {
    return if(mergeable(that)) {
        val mergedBlocks = this.blocks.mapIndexed { index, b -> b || that.blocks[index] }
        Cell(width, mergedBlocks.toMutableList())
    }else null
}

fun Cell.mergeable(that: Cell): Boolean {
    if(this.width != that.width) return false
    return !this.blocks.zip(that.blocks).any { it.first && it.second }
}