package com.marcouberti.ninegame.ui.board

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.marcouberti.ninegame.R
import com.marcouberti.ninegame.model.Board
import com.marcouberti.ninegame.model.Card
import com.marcouberti.ninegame.model.check
import com.marcouberti.ninegame.model.set
import kotlinx.android.synthetic.main.board_fragment.*

class BoardFragment : Fragment() {

    var init = false

    companion object {
        fun newInstance() = BoardFragment()
    }

    private lateinit var viewModel: BoardViewModel

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BOARD_KEY, viewModel.board.value)
        outState.putInt(SCORE_KEY, viewModel.score.value?:0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.board_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BoardViewModel::class.java)
        viewModel.board.observe(this, Observer<Board> { board: Board? ->
            boardView.board = board

            if(board != null && !init) {
                init = true

                boardView.children.forEach { l ->
                    if (l is LinearLayout) {
                        l.children.forEach { v ->
                            if (v is ConstraintLayout) {
                                v.setOnClickListener { v -> viewModel.setSelection((v.findViewById<CardView>(R.id.card)).position) }
                            }
                        }
                    }
                }
            }
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
        }

        init()
    }

    private fun init() {
        viewModel.newGame()
    }

}
