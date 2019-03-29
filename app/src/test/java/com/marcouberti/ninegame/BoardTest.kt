package com.marcouberti.ninegame

import com.marcouberti.ninegame.model.*
import org.junit.Test

import org.junit.Assert.*

class BoardTest {
    @Test
    fun `a freshly new board contains no cell`() {
        val b = Board(5)
        assertEquals(0, b.cards.size)
    }

    @Test
    fun `you can set a cell in the board grid using set operator overloading`() {
        val b = Board(5)
        b[Pair(2, 2)] = Card(3)
        assertNotNull(b.cards[Pair(2,2)])
    }

    @Test
    fun `you can get a cell from the board grid using get operator overloading`() {
        val b = Board(5)
        b[Pair(2, 2)] = Card(3)
        assertNotNull(b[Pair(2,2)])
        assertNull(b[Pair(3,3)])
    }

    @Test
    fun `if all cells are busy the board is full`() {
        val b = Board(5)
        for(i in 1..b.width) {
            for(j in 1..b.width) {
                b[Pair(i,j)] = Card(3)
            }
        }
        assertTrue(b.isFull())
    }

    @Test
    fun `if there is at least an empty cell the board is not full`() {
        val b = Board(5)
        for(i in 1..b.width) {
            for(j in 1..b.width) {
                b[Pair(i,j)] = Card(3)
            }
        }
        b[Pair(1,2)] = null
        assertFalse(b.isFull())
    }

    @Test
    fun `two cell on the same row are mergeable if there is no cell in the middle`() {
        val b = Board(5)
        b[Pair(1,1)] = Card(3)
        b[Pair(1,3)] = Card(3)
        assertTrue(b.mergeable(Pair(1,1), Pair(1,3)))
    }

    @Test
    fun `a cell cannot be merged with itself`() {
        val b = Board(5)
        assertFalse(b.mergeable(Pair(1,1), Pair(1,1)))
    }

    @Test
    fun `two cell on the same column are mergeable if there is no cell in the middle`() {
        val b = Board(5)
        b[Pair(1,1)] = Card(3)
        b[Pair(4,1)] = Card(3)
        assertTrue(b.mergeable(Pair(1,1), Pair(4,1)))
    }

    @Test
    fun `two cell on the same row are NOT mergeable if there is a cell in the middle`() {
        val b = Board(5)
        b[Pair(1,1)] = Card(3)
        b[Pair(1,2)] = Card(3)
        b[Pair(1,5)] = Card(3)
        assertFalse(b.mergeable(Pair(1,1), Pair(1,5)))
    }

    @Test
    fun `two cell on the same column are NOT mergeable if there is a cell in the middle`() {
        val b = Board(5)
        b[Pair(1,1)] = Card(3)
        b[Pair(3,1)] = Card(3)
        b[Pair(4,1)] = Card(3)
        assertFalse(b.mergeable(Pair(1,1), Pair(4,1)))
    }
}
