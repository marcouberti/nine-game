package com.marcouberti.ninegame.ui.board

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcouberti.ninegame.GameApplication
import com.marcouberti.ninegame.model.*
import com.marcouberti.ninegame.utils.LiveEvent
import com.marcouberti.ninegame.utils.OnSwipeListener
import com.marcouberti.ninegame.utils.RecordManager
import com.marcouberti.ninegame.utils.VibrationManager
import kotlin.random.Random

const val BOARD_KEY = "BOARD"
const val SCORE_KEY = "SCORE"
const val RECORD_KEY = "RECORD"
const val NEXT_CARD_KEY = "NEXT_CARD"
const val PLAYING_KEY = "PLAYING_KEY"

class BoardViewModel : ViewModel() {

    val playSfxRotate = LiveEvent<Boolean>()
    val playSfxMove = LiveEvent<Boolean>()
    val playSfxMerge = LiveEvent<Boolean>()
    val playSfxFullMerge = LiveEvent<Boolean>()
    val playSfxNoMove = LiveEvent<Boolean>()
    val playSfxNewGame = LiveEvent<Boolean>()
    val playSfxNewCard = LiveEvent<Boolean>()
    val playSfxGameOver = LiveEvent<Boolean>()

    val board: MutableLiveData<Board> by lazy {
        MutableLiveData<Board>()
    }

    val nextCard: MutableLiveData<Card> by lazy {
        MutableLiveData<Card>()
    }

    val score: MutableLiveData<Int?> by lazy {
        MutableLiveData<Int?>()
    }

    val record: MutableLiveData<Int?> by lazy {
        MutableLiveData<Int?>()
    }

    val playing: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val movements: MutableLiveData<MutableList<Move>> by lazy {
        MutableLiveData<MutableList<Move>>()
    }

    fun newGame() {
        if(board.value == null) {
            val newBoard = Board(6).apply {
                val newCards = init()
                movements.value = newCards.toMutableList()
            }
            setBoard(newBoard)
            nextCard.value = Card().apply { init() }
            record.value = RecordManager.saveRecordIfAny(GameApplication.context!!, 0)
            playSfxNewGame.value = true
        }
    }

    fun setSelection(pos: Pair<Int, Int>) {
        if(board.value?.gameOver() == true) return
        board.value?.let {
            it[pos]?.let{ card ->
                card.rotate()
                movements.value = mutableListOf(Move(MoveType.ROTATION, 0, pos, null))
                playSfxRotate.value = true
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

    fun setPlaying(isPlaying: Boolean) {
        playing.value = isPlaying
    }

    fun setRecord(restoredRecord: Int) {
        if(record.value == null) {
            record.value = restoredRecord
        }
    }

    fun setNextCard(restoredCard: Card?) {
        if(nextCard.value == null) {
            nextCard.value = restoredCard
        }
    }

    fun slide(position: Pair<Int, Int>, direction: OnSwipeListener.Direction) {
        val moves = mutableListOf<Move>()
        val move = board.value?.slide(position, direction) //null -> no move, 0 -> move, N -> merge
        move?.points?.let {
            when(it) {
                0 -> {
                    addNewCard()?.let { pos ->
                        moves.add(Move(type = MoveType.NEW, from = pos))
                        playSfxNewCard.value = true
                    }
                    playSfxMove.value = true
                }
                else -> {
                    addNewCard()?.let { pos ->
                        moves.add(Move(type = MoveType.NEW, from = pos))
                        playSfxNewCard.value = true
                    }
                    val newPoints = if(it < CARD_SIZE* CARD_SIZE) {
                        playSfxMerge.value = true
                        it
                    } else {
                        playSfxFullMerge.value = true
                        it*10
                    }
                    score.value = (score.value?:0).plus(newPoints)
                    record.value = RecordManager.saveRecordIfAny(GameApplication.context!!, score.value?:0)
                }
            }

        }
        if(move != null) moves.add(move)
        if(move?.points == null) VibrationManager.vibrate()
        movements.value = moves

        updateBoard()

        if(board.value?.gameOver() == true) playSfxGameOver.value = true
    }

    fun addNewCard(): Pair<Int, Int>? {
        val newCardPosition = board.value?.addCard(nextCard.value ?: Card().apply { check(Random.nextInt(1, this.width)) })
        nextCard.value = Card().apply { init() }
        return newCardPosition
    }
}
