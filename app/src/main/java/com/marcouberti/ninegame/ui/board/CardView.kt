package com.marcouberti.ninegame.ui.board

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import com.marcouberti.ninegame.model.Card
import android.util.TypedValue
import com.marcouberti.ninegame.model.get


class CardView: FrameLayout {

    lateinit var ctx: Context
    private val filledPaint = Paint()
    private val borderPaint = Paint()

    lateinit var position: Pair<Int, Int>

    var card: Card? = null
        set(c) {
            field = c
            invalidate()
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

        filledPaint.color = Color.BLACK
        filledPaint.style = Paint.Style.FILL
        filledPaint.isAntiAlias = true

        borderPaint.color = Color.BLACK
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = dpToPx(2f)
        borderPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val c = card

        // card border
        canvas.drawRect(0F,0F, measuredWidth.toFloat(), measuredHeight.toFloat(), borderPaint)

        if(c != null) {
            val blockWidth = measuredWidth.toFloat() / c.width
            for(i in 1..c.width) {
                for(j in 1..c.width) {
                    //val paint = if(!c[c.width*(i-1)+j]) borderPaint else filledPaint
                    if(c[c.width*(i-1)+j]) canvas.drawRect((j-1)*blockWidth, (i-1)*blockWidth, (j)*blockWidth, (i)*blockWidth, filledPaint)
                }
            }
        }
    }

    private fun dpToPx(dp: Float): Float {
        val r = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.displayMetrics
        )
    }

}