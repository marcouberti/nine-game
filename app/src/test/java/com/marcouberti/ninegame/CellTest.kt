package com.marcouberti.ninegame

import com.marcouberti.ninegame.model.*
import org.junit.Test

import org.junit.Assert.*

class CellTest {
    @Test
    fun `a freshly new cell contains "width*width" blocks`() {
        val c = Cell(4)
        assertEquals(16, c.blocks.size)
    }

    @Test
    fun `a freshly new cell contains only one filled block`() {
        val c = Cell(5)
        assertEquals(1, c.blocks.count { it })
    }

    @Test
    fun `clear all uncheck all blocks`() {
        val c = Cell(5)
        c[4] = true
        c[5] = true
        c.clearAll()
        assertEquals(0, c.blocks.count { it })
    }

    @Test
    fun `set operator overloading is working`() {
        val c = Cell(5)
        c.clearAll()
        c[4] = true
        c[5] = true
        assertEquals(2, c.blocks.count { it })
        assertTrue(c[4])
        assertTrue(c[5])
    }

    @Test
    fun `isFull return true if all blocks are checked`() {
        val c = Cell(5)
        c.clearAll()
        for(i in 1 .. (5*5)) c[i] = true
        assertTrue(c.isFull())
    }
}
