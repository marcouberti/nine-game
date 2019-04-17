package com.marcouberti.ninegame.ui.board

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.marcouberti.ninegame.model.Board
import com.marcouberti.ninegame.model.Card
import kotlinx.android.synthetic.main.board_fragment.*
import com.marcouberti.ninegame.R
import com.marcouberti.ninegame.model.Move
import com.marcouberti.ninegame.utils.OnSwipeListener
import com.marcouberti.ninegame.utils.SoundManager


class BoardFragment : Fragment(), CardSwipeListener {

    var init = false
    private lateinit var soundManager: SoundManager

    companion object {
        fun newInstance() = BoardFragment()
    }

    private lateinit var viewModel: BoardViewModel

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BOARD_KEY, viewModel.board.value)
        outState.putParcelable(NEXT_CARD_KEY, viewModel.nextCard.value)
        outState.putInt(SCORE_KEY, viewModel.score.value?:0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.board_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        soundManager = SoundManager()
        this.lifecycle.addObserver(soundManager)

        viewModel = ViewModelProviders.of(this).get(BoardViewModel::class.java)
        viewModel.board.observe(this, Observer<Board> { board: Board? ->
            boardView.movements = viewModel.movements.value
            boardView.board = board

            if(board != null && !init) {
                init = true
                boardView.swipeListener = this
            }
        })

        viewModel.movements.observe(this, Observer<MutableList<Move>> { movements: MutableList<Move> ->
            boardView.movements = movements
        })

        viewModel.score.observe(this, Observer {s: Int? ->
            if(s!=null) score.text = s.toString()
        })

        viewModel.nextCard.observe(this, Observer {card: Card ->
            nextCard.card = card
        })

        if(savedInstanceState != null) {
            savedInstanceState.getParcelable<Board>(BOARD_KEY)?.let {
                viewModel.setBoard(it)
            }
            savedInstanceState.getInt(SCORE_KEY).let {
                viewModel.setScore(it)
            }
            savedInstanceState.getParcelable<Card>(NEXT_CARD_KEY).let {
                viewModel.setNextCard(it)
            }
        }

        init()
    }

    override fun onTap(position: Pair<Int, Int>?) {
        if(position != null) viewModel.setSelection(position)
    }

    override fun onSwipe(position: Pair<Int, Int>?, direction: OnSwipeListener.Direction) {
        if(position != null) viewModel.slide(position, direction)
    }

    private fun init() {
        viewModel.newGame()
    }

}
