package com.marcouberti.ninegame

import com.marcouberti.ninegame.model.*
import org.junit.Test

import org.junit.Assert.*

class BoardTest {
    @Test
    fun `a freshly new board contains no card`() {
        val b = Board(5)
        assertEquals(0, b.cards.size)
    }

    @Test
    fun `you can set a card in the board grid using set operator overloading`() {
        val b = Board(5)
        b[Pair(2, 2)] = Card(3)
        assertNotNull(b.cards[Pair(2,2)])
    }

    @Test
    fun `you can get a card from the board grid using get operator overloading`() {
        val b = Board(5)
        b[Pair(2, 2)] = Card(3)
        assertNotNull(b[Pair(2,2)])
        assertNull(b[Pair(3,3)])
    }

    @Test
    fun `if all cards are busy the board is full`() {
        val b = Board(5)
        for(i in 1..b.width) {
            for(j in 1..b.width) {
                b[Pair(i,j)] = Card(3)
            }
        }
        assertTrue(b.isFull())
    }

    @Test
    fun `if there is at least an empty card the board is not full`() {
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
    fun `two card on the same row are mergeable if there is no card in the middle`() {
        val b = Board(5)
        b[Pair(1,1)] = Card(3).apply { clearAll() }
        b[Pair(1,3)] = Card(3).apply { clearAll() }
        assertTrue(b.mergeable(Pair(1,1), Pair(1,3)))
    }

    @Test
    fun `a card can be moved to another position if there is no card in the middle`() {
        val b = Board(5)
        b[Pair(1,1)] = Card(3).apply { clearAll() }
        b[Pair(1,3)] = Card(3).apply { clearAll() }
        assertTrue(b.movable(Pair(1,1), Pair(1,2)))
    }

    @Test
    fun `a card can NOT be moved to another position if there is a card in the middle`() {
        val b = Board(5)
        b[Pair(1,1)] = Card(3).apply { clearAll() }
        b[Pair(1,3)] = Card(3).apply { clearAll() }
        assertFalse(b.movable(Pair(1,1), Pair(1,4)))
    }

    @Test
    fun `a card cannot be merged with itself`() {
        val b = Board(5)
        assertFalse(b.mergeable(Pair(1,1), Pair(1,1)))
    }

    @Test
    fun `two card on the same column are mergeable if there is no card in the middle`() {
        val b = Board(5)
        b[Pair(1,1)] = Card(3).apply { clearAll() }
        b[Pair(4,1)] = Card(3).apply { clearAll() }
        assertTrue(b.mergeable(Pair(1,1), Pair(4,1)))
    }

    @Test
    fun `two card on the same row are NOT mergeable if there is a card in the middle`() {
        val b = Board(5)
        b[Pair(1,1)] = Card(3).apply { clearAll() }
        b[Pair(1,2)] = Card(3).apply { clearAll() }
        b[Pair(1,5)] = Card(3).apply { clearAll() }
        assertFalse(b.mergeable(Pair(1,1), Pair(1,5)))
    }

    @Test
    fun `two card on the same column are NOT mergeable if there is a card in the middle`() {
        val b = Board(5)
        b[Pair(1,1)] = Card(3).apply { clearAll() }
        b[Pair(3,1)] = Card(3).apply { clearAll() }
        b[Pair(4,1)] = Card(3).apply { clearAll() }
        assertFalse(b.mergeable(Pair(1,1), Pair(4,1)))
    }

    @Test
    fun `when two card are merged the second one is replaced, the first one is cleared and the weight is returned`() {
        val b = Board(5)
        b[Pair(1,1)] = Card(3).apply {
            clearAll()
            check(1)
        }
        b[Pair(4,1)] = Card(3).apply {
            clearAll()
            check(2, 4)
        }
        assertEquals(3, b.merge(Pair(1,1), Pair(4,1)))
        assertNull(b[Pair(1,1)])
        assertNotNull(b[Pair(4,1)])
        assertEquals(Card(3).apply {
            clearAll()
            check(1, 2, 4)
        }, b[Pair(4,1)])
    }

    @Test
    fun `when two card are merged and the merged one if full both cards disappear`() {
        val b = Board(5)
        b[Pair(1,1)] = Card(3).apply {
            clearAll()
            check(1,2,3,4)
        }
        b[Pair(4,1)] = Card(3).apply {
            clearAll()
            check(5,6,7,8,9)
        }
        assertEquals(9, b.merge(Pair(1,1), Pair(4,1)))
        assertNull(b[Pair(1,1)])
        assertNull(b[Pair(4,1)])
    }

    @Test
    fun `two not mergeable cards returns null when merged and are not updated`() {
        val b = Board(5)
        b[Pair(1,1)] = Card(3).apply {
            clearAll()
            check(1, 2)
        }
        b[Pair(4,1)] = Card(3).apply {
            clearAll()
            check(2, 4)
        }
        assertNull(b.merge(Pair(1,1), Pair(4,1)))
        assertNotNull(b[Pair(1,1)])
        assertNotNull(b[Pair(4,1)])
        assertEquals(Card(3).apply {
            clearAll()
            check(1, 2)
        }, b[Pair(1,1)])
        assertEquals(Card(3).apply {
            clearAll()
            check(2, 4)
        }, b[Pair(4,1)])
    }

    @Test
    fun `when there is at least one possibile move the game is NOT over`() {
        val b = Board(2)
        b[Pair(1,1)] = Card(3).apply {
            clearAll()
            check(1, 2, 3, 4, 5, 6, 7, 8)
        }
        b[Pair(1,2)] = Card(3).apply {
            clearAll()
            check(1, 2, 3, 4, 5, 6, 7, 8)
        }
        b[Pair(2,1)] = Card(3).apply {
            clearAll()
            check(1, 2, 3, 4, 5, 6, 7, 8)
        }
        b[Pair(2,2)] = Card(3).apply {
            clearAll()
            check(1)
        }
        assertFalse(b.gameOver())
    }

    @Test
    fun `when there is no possibile move the game is over`() {
        val b = Board(2)
        b[Pair(1,1)] = Card(3).apply {
            clearAll()
            check(1, 2, 3, 4, 5, 6, 7, 8)
        }
        b[Pair(1,2)] = Card(3).apply {
            clearAll()
            check(1, 2, 3, 4, 5, 6, 7, 8)
        }
        b[Pair(2,1)] = Card(3).apply {
            clearAll()
            check(1, 2, 3, 4, 5, 6, 7, 8)
        }
        b[Pair(2,2)] = Card(3).apply {
            clearAll()
            check(1, 2)
        }
        assertTrue(b.gameOver())
    }
}
