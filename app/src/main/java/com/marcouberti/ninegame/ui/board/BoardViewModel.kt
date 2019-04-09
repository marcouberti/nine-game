package com.marcouberti.ninegame.ui.board

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcouberti.ninegame.model.*
import com.marcouberti.ninegame.utils.OnSwipeListener
import kotlin.random.Random

const val BOARD_KEY = "BOARD"
const val SCORE_KEY = "SCORE"
const val NEXT_CARD_KEY = "NEXT_CARD"

class BoardViewModel : ViewModel() {

    val board: MutableLiveData<Board> by lazy {
        MutableLiveData<Board>()
    }

    val nextCard: MutableLiveData<Card> by lazy {
        MutableLiveData<Card>()
    }

    val score: MutableLiveData<Int?> by lazy {
        MutableLiveData<Int?>()
    }

    val movements: MutableLiveData<MutableList<Move>> by lazy {
        MutableLiveData<MutableList<Move>>()
    }

    fun newGame() {
        if(board.value == null) {
            setBoard(Board(6).apply { init() })
            nextCard.value = Card().apply { init() }
        }
    }

    fun setSelection(pos: Pair<Int, Int>) {
        if(board.value?.gameOver() == true) return
        board.value?.let {
            it[pos]?.let{ card ->
                card.rotate()
                movements.value = mutableListOf(Move(MoveType.ROTATION, 0, pos, null))
            }
        }
        updateBoard()
    }

    private fun updateBoard() {
        board.value = board.value?.copy()
    }

    fun setBoard(restoredBoard: Board?) {
        if(board.value == null) {
            board.value = restoredBoard
        }
    }

    fun setScore(restoredScore: Int) {
        if(score.value == null) {
            score.value = restoredScore
        }
    }

    fun setNextCard(restoredCard: Card?) {
        if(nextCard.value == null) {
            nextCard.value = restoredCard
        }
    }

    fun slide(position: Pair<Int, Int>, direction: OnSwipeListener.Direction) {
        val move = board.value?.slide(position, direction) //null -> no move, 0 -> move, N -> merge
        move?.points?.let {
            when(it) {
                0 -> addNewCard()
                else -> {
                    addNewCard()
                    val newPoints = if(it < CARD_SIZE* CARD_SIZE) it else it*10
                    score.value = (score.value?:0).plus(newPoints)
                }
            }

        }
        if(move != null) movements.value = mutableListOf(move)

        updateBoard()
    }

    fun addNewCard() {
        board.value?.addCard(nextCard.value ?: Card().apply { check(Random.nextInt(1, this.width)) })
        nextCard.value = Card().apply { init() }
    }
}
