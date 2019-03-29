package com.marcouberti.ninegame.ui.board

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.marcouberti.ninegame.R
import com.marcouberti.ninegame.model.Board
import com.marcouberti.ninegame.model.get

class BoardView: LinearLayout {

    lateinit var ctx: Context
    var init = false
    var cardMap = mutableMapOf<Pair<Int, Int>, CardView>()

    var board: Board? = null
        set(b) {
            if(b != null) {
                field = b
                setupBoard()
            }
        }

    constructor(context: Context) : super(context){
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet):    super(context, attrs){
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        setWillNotDraw(false)
        ctx = context
        orientation = VERTICAL
    }

    private fun setupBoard() {
        val b = board
        if(b != null && !init) {
            init = true
            removeAllViews()
            for(i in 1..b.width) {
                val row = LinearLayout(ctx).apply {
                    orientation = HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                }

                for(j in 1..b.width) {
                    val rootView = LayoutInflater.from(ctx).inflate(R.layout.card, row, false)
                    row.addView(rootView)
                    val cardView = rootView.findViewById(R.id.card) as CardView
                    cardView.card = b[Pair(i,j)]
                    cardView.position = Pair(i,j)
                    cardMap[Pair(i,j)] = cardView
                }

                addView(row)
            }
        }else if(b!=null) {
            for(i in 1..b.width) {
                for(j in 1..b.width) {
                    cardMap[Pair(i,j)]?.card = b[Pair(i,j)]
                }
            }
        }
    }

}