package com.marcouberti.ninegame.ui.board

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcouberti.ninegame.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    val score: MutableLiveData<Int?> by lazy {
        MutableLiveData<Int?>()
    }

    fun newGame() {
        if(board.value == null) {
            setBoard(Board(5).apply { init() })
        }
    }

    fun setSelection(pos: Pair<Int, Int>) {
        if(firstSelection.value == null) firstSelection.value = pos
        else {
            if(firstSelection.value == pos) return // same position
            secondSelection.value = pos
            val points = board.value?.merge(firstSelection.value?: Pair(1,1), secondSelection.value?:Pair(1,1))
            if(points != null) {
                val newScore = points + (score.value?:0)
                score.value = newScore
            }
            else board.value?.move(firstSelection.value?: Pair(1,1), secondSelection.value?:Pair(1,1))
            if(board.value?.isFull() == false) {
                val updatedBoard = board.value?.copy()
                if(points == null) updatedBoard?.addCard() // add new card only when move, not when merge
                board.value = updatedBoard
                firstSelection.value = null
                secondSelection.value = null
            }
            else print("LOOOOSER")
        }
    }

    fun setBoard(restoredBoard: Board?) {
        if(board.value == null) {
            board.value = restoredBoard
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
