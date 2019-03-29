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
import kotlinx.android.synthetic.main.board_fragment.*

class BoardFragment : Fragment() {

    var init = false

    companion object {
        fun newInstance() = BoardFragment()
    }

    private lateinit var viewModel: BoardViewModel

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("BOARD", viewModel.board.value)
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

        if(savedInstanceState != null) {
            val restoredBoard = savedInstanceState.getParcelable<Board>("BOARD")
            if(restoredBoard != null) viewModel.setBoard(restoredBoard)
        }

        init()
    }

    private fun init() {
        viewModel.newGame()
    }

}
