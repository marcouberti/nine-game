package com.marcouberti.ninegame

import com.marcouberti.ninegame.model.*
import org.junit.Test

import org.junit.Assert.*

class CardTest {
    @Test
    fun `a freshly new card contains "width*width" blocks`() {
        val c = Card(4)
        assertEquals(16, c.blocks.size)
    }

    @Test
    fun `a freshly new card contains only one filled block`() {
        val c = Card(5)
        assertEquals(1, c.blocks.count { it })
    }

    @Test
    fun `clear all uncheck all blocks`() {
        val c = Card(5)
        c[4] = true
        c[5] = true
        c.clearAll()
        assertEquals(0, c.blocks.count { it })
    }

    @Test
    fun `set operator overloading is working`() {
        val c = Card(5)
        c.clearAll()
        c[4] = true
        c[5] = true
        assertEquals(2, c.blocks.count { it })
        assertTrue(c[4])
        assertTrue(c[5])
    }

    @Test
    fun `isFull return true if all blocks are checked`() {
        val c = Card(5)
        c.clearAll()
        for(i in 1 .. (c.width*c.width)) c[i] = true
        assertTrue(c.isFull())
    }

    @Test
    fun `initRandom check only one block`() {
        val c = Card(50)
        c.initRandom()
        assertEquals(1, c.blocks.count { it })
    }

    @Test
    fun `two cards with different width cannot be merged`() {
        val card1 = Card(4).apply { clearAll() }
        val card2 = Card(5).apply { clearAll() }
        assertFalse(card1.mergeable(card2))
    }

    @Test
    fun `two cards with at least one shared flagged block cannot be merged`() {
        val card1 = Card(4).apply {
            clearAll()
            check(5)
        }
        val card2 = Card(4).apply {
            clearAll()
            check(5)
        }
        assertFalse(card1.mergeable(card2))
    }

    @Test
    fun `two cards with no shared flagged block can be merged`() {
        val card1 = Card(4).apply {
            clearAll()
            check(1)
            check(3)
        }
        val card2 = Card(4).apply {
            clearAll()
            check(5)
            check(2)
        }
        assertTrue(card1.mergeable(card2))
    }

    @Test
    fun `two cards can be merged`() {
        val card1 = Card(4).apply {
            clearAll()
            check(1)
            check(3)
        }
        val card2 = Card(4).apply {
            clearAll()
            check(5)
            check(2)
        }
        val merged = card1.merge(card2)
        assertTrue(merged!![1])
        assertTrue(merged[3])
        assertTrue(merged[5])
        assertTrue(merged[2])
    }

    @Test
    fun `two non mergeable cards when merged return null`() {
        val card1 = Card(4).apply { clearAll() }
        val card2 = Card(5).apply { clearAll() }
        val merged = card1.merge(card2)
        assertNull(merged)
    }

    @Test
    fun `weight returns the number of flagged blocks`() {
        val c = Card(4).apply { clearAll() }
        c.check(1)
        c.check(7)
        c.check(4)
        assertEquals(3, c.weight())
    }

    @Test
    fun `two complementary cards when merged result in a full card`() {
        val card1 = Card(3).apply {
            clearAll()
            check(1)
            check(2)
            check(3)
            check(4)
        }
        val card2 = Card(3).apply {
            clearAll()
            check(5)
            check(6)
            check(7)
            check(8)
            check(9)
        }
        val merged = card1.merge(card2)
        assertTrue(merged?.isFull() == true)
    }

    @Test
    fun `two not complementary cards when merged result in a NOT full card`() {
        val card1 = Card(3).apply {
            clearAll()
            check(1)
            check(3)
            check(4)
        }
        val card2 = Card(3).apply {
            clearAll()
            check(5)
            check(6)
            check(7)
            check(8)
            check(9)
        }
        val merged = card1.merge(card2)
        assertFalse(merged?.isFull() == true)
    }
}
