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

    fun newGame() {
        if(board.value == null) {
            setBoard(Board(5).apply { init() })
            nextCard.value = Card().apply { init() }
        }
    }

    fun setSelection(pos: Pair<Int, Int>) {
        if(board.value?.gameOver() == true) return
        board.value?.let {
            it[pos]?.let{ card ->
                card.rotate()
                // TODO save movements first
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
        val points = board.value?.slide(position, direction) //null -> no move, 0 -> move, N -> merge
        points?.let {
            when(it) {
                0 -> addNewCard()
                else -> {
                    var points = 0
                    points = if(it < CARD_SIZE* CARD_SIZE) it else it*10
                    score.value = (score.value?:0).plus(points)
                }
            }

        }
        // TODO save movements first
        updateBoard()
    }

    fun addNewCard() {
        board.value?.addCard(nextCard.value ?: Card().apply { check(Random.nextInt(1, this.width)) })
        nextCard.value = Card().apply { init() }
    }


    /*
    fun launchDataLoad() {
        viewModelScope.launch {
            sortList()
            // Modify UI
        }
    }

    suspend fun sortList() = withContext(Dispatchers.Default) {
        // Heavy work
    }
    */
}
