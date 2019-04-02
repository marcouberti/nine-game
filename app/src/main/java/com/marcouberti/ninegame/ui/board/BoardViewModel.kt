package com.marcouberti.ninegame.ui.board

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcouberti.ninegame.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

const val BOARD_KEY = "BOARD"
const val SCORE_KEY = "SCORE"

class BoardViewModel : ViewModel() {

    val firstSelection: MutableLiveData<Pair<Int, Int>?> by lazy {
        MutableLiveData<Pair<Int, Int>?>()
    }

    val secondSelection: MutableLiveData<Pair<Int, Int>?> by lazy {
        MutableLiveData<Pair<Int, Int>?>()
    }

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
            setBoard(Board(4).apply { init() })
            nextCard.value = Card(3).apply { init() }
        }
    }

    fun setSelection(pos: Pair<Int, Int>) {
        //if(board.value?.isFull() == true) return
        if(firstSelection.value == null) {
            if(board.value != null && board.value!![pos] != null) firstSelection.value = pos
        }
        else {
            if(firstSelection.value == pos) {
                board.value?.let {
                    it[pos]?.rotate()
                }
            } // same position
            else {
                secondSelection.value = pos
                val points = board.value?.merge(firstSelection.value ?: Pair(1, 1), secondSelection.value ?: Pair(1, 1))
                if (points != null) {
                    board.value?.addCard(nextCard.value ?: Card(3).apply { check(Random.nextInt(1, this.width)) })
                    nextCard.value = Card(3).apply { init() }
                    val newScore = points + (score.value ?: 0)
                    score.value = newScore
                } else {
                    val moved =
                        (board.value?.move(firstSelection.value ?: Pair(1, 1), secondSelection.value ?: Pair(1, 1)))
                            ?: false
                    if (moved) {
                        board.value?.addCard(nextCard.value ?: Card(3).apply { check(Random.nextInt(1, this.width)) })
                        nextCard.value = Card(3).apply { init() }
                    }
                }
            }

            val updatedBoard = board.value?.copy()
            board.value = updatedBoard
            firstSelection.value = null
            secondSelection.value = null

            /*
            if(board.value?.isFull() == true) {
                Log.d("Board", "LOOSER")
            }*/
        }
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
