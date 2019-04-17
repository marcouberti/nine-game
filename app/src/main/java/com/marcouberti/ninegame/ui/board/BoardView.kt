package com.marcouberti.ninegame.ui.board

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.forEach
import com.marcouberti.ninegame.R
import com.marcouberti.ninegame.model.*
import com.marcouberti.ninegame.utils.OnSwipeListener
import kotlinx.coroutines.*

class BoardView: LinearLayout, View.OnTouchListener {

    private val borderPaint = Paint()

    var selectedCard: CardView? = null

    var gestureDetector: GestureDetectorCompat? = null
    var swipeListener: CardSwipeListener? = null

    lateinit var ctx: Context
    var init = false
    var cardMap = mutableMapOf<Pair<Int, Int>, CardView>()

    var movements: MutableList<Move>? = null
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

        borderPaint.color = Color.BLACK
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = dpToPx(1f)
        borderPaint.isAntiAlias = true
        borderPaint.alpha = 40

        gestureDetector = GestureDetectorCompat(ctx, object : OnSwipeListener() {

            override fun onSwipe(direction: OnSwipeListener.Direction): Boolean {
                swipeListener?.onSwipe(selectedCard?.position, direction)
                return true
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                swipeListener?.onTap(selectedCard?.position)
                return super.onSingleTapConfirmed(e)
            }

        })
        setOnTouchListener(this)
    }

    private fun dpToPx(dp: Float): Float {
        val r = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.displayMetrics
        )
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_DOWN) {
            selectedCard = cardMap.values.firstOrNull {
                val pos = IntArray(2)
                it.getLocationOnScreen(pos)
                pos[0] < event.rawX && pos[1] < event.rawY &&
                        pos[0] + it.width > event.rawX && pos[1] + it.height > event.rawY
            }
        }
        return gestureDetector?.onTouchEvent(event)?:false
    }

    private fun setupBoard() {
        val b = board

        // first views setup
        if(b != null && !init) {
            init = true
            removeAllViews()
            for(i in 1..b.width) {
                val row = LinearLayout(ctx).apply {
                    orientation = HORIZONTAL
                    clipChildren = false
                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                }

                for(j in 1..b.width) {
                    val rootView = LayoutInflater.from(ctx).inflate(R.layout.card, row, false)
                    row.addView(rootView)
                    val cardView = rootView.findViewById(R.id.card) as CardView
                    cardView.position = Pair(i,j)
                    cardMap[Pair(i,j)] = cardView
                }

                addView(row)
            }
        }

        // animate
        if(b != null) {
            // deep copy cards map
            // and remove new cards
            val bNoNew = b.copy(cards = b.cards.toMutableMap()).apply {
                val newMovements = movements?.filter { it.type == MoveType.NEW }
                newMovements?.forEach { move -> this[move.from!!] = null }
            }

            movements?.reverse() // first move anims, then add new anim

            movements?.forEach { move ->

                val pos = move.from?:return@forEach // continue
                val view = cardMap[pos]?:return@forEach // continue

                when(move.type) {
                    MoveType.ROTATION -> {
                        val animationEndBlock = {
                            view.card = bNoNew[pos]
                        }
                        view.animateRotation(animationEndBlock)
                    }
                    MoveType.MOVE, MoveType.MOVE_AND_MERGE -> {

                        val animationEndBlock = {
                            view.card = bNoNew[pos]
                            cardMap[move.to!!]?.card = bNoNew[move.to]
                        }

                        val from = PointF(0f, 0f) // the move is relative to itself

                        val locationOnScreenThis = IntArray(2)
                        view.getLocationOnScreen(locationOnScreenThis)

                        val locationOnScreenThat = IntArray(2)
                        cardMap[move.to]?.let{
                            it.getLocationOnScreen(locationOnScreenThat)

                            val to = PointF(locationOnScreenThat[0].toFloat() - locationOnScreenThis[0].toFloat(),
                                locationOnScreenThat[1].toFloat() - locationOnScreenThis[1].toFloat())
                            if(move.type == MoveType.MOVE_AND_MERGE) view.animateMovement(from, to, animationEndBlock, true)
                            else view.animateMovement(from, to, animationEndBlock, false)
                        }
                    }
                    MoveType.NEW -> {
                        val animationStartBlock = {
                            view.card = b[pos]
                        }
                        view.animateNew(animationStartBlock)
                    }
                    MoveType.NONE -> {}
                }
            }

            movements?.clear()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

            val W = measuredWidth.toFloat()
            val CW = measuredWidth.toFloat() / (board?.width?:1)

            for(i in 0 .. (board?.width?:1)) {
                canvas.drawLine(i*CW, 0F, i*CW , W, borderPaint)

            }
            for(j in 0 .. (board?.width?:1)) {
                canvas.drawLine(0F, j*CW, W , j*CW, borderPaint)
            }
    }
}