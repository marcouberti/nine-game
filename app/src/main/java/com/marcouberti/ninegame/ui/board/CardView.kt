package com.marcouberti.ninegame.ui.board

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import com.marcouberti.ninegame.model.Card
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.view.GestureDetectorCompat
import com.marcouberti.ninegame.R
import com.marcouberti.ninegame.model.get
import com.marcouberti.ninegame.model.isFull


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
        borderPaint.alpha = 40
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val c = card

        // card border
        // canvas.drawRect(0F,0F, measuredWidth.toFloat(), measuredHeight.toFloat(), borderPaint)

        if(c != null) {
            val blockWidth = measuredWidth.toFloat() / c.width
            filledPaint.color = Color.BLACK
            //filledPaint.alpha = 255
            for(i in 1..c.width) {
                for(j in 1..c.width) {
                    //filledPaint.alpha -= 20
                    if(this.card?.isFull() == true) {
                        filledPaint.color = ctx.resources.getColor(R.color.colorAccent)
                        //filledPaint.alpha = 255
                    }
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

    fun animateRotation(animationEndBlock: () -> Unit) {
        val rot = ObjectAnimator.ofFloat(this, "rotation", 0f, -90f)
        val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1F, 0.5F, 1F)
        val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1F, 0.5F, 1F)

        AnimatorSet().apply {
            playTogether(rot, scaleX, scaleY)
            duration = 125
            doOnEnd {
                animationEndBlock()
                this@CardView.rotation = 0f
                this@CardView.scaleX = 1f
                this@CardView.scaleY = 1f

            }
            start()
        }
    }

    fun animateNothing(animationEndBlock: () -> Unit) {
        val oa = ObjectAnimator.ofFloat(this, "rotation", 0f, 0f).apply {
            duration = 125
        }
        oa.doOnEnd {
            animationEndBlock()
        }
        oa.start()
    }

    fun animateMovement(from: PointF, to: PointF, animationEndBlock: () -> Unit) {
        val animX = ObjectAnimator.ofFloat(this, "translationX", from.x, to.x)
        val animY = ObjectAnimator.ofFloat(this, "translationY", from.y, to.y)
        AnimatorSet().apply {
            playTogether(animX, animY)
            duration = 125
            doOnEnd {
                animationEndBlock()
                this@CardView.translationX = 0f
                this@CardView.translationY = 0f
            }
            start()
        }
    }

}