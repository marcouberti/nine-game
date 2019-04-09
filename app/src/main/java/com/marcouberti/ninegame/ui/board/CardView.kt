package com.marcouberti.ninegame.ui.board

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
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
import android.R.attr.inset
import android.animation.ValueAnimator
import androidx.core.view.ViewCompat.getClipBounds
import android.graphics.*
import kotlin.math.max
import kotlin.math.min


class CardView: FrameLayout {

    lateinit var ctx: Context

    private val DURATION = 250L

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

    var percentageDelayX = 0.0f
    var percentageDelayY = 0.0f
    var sign = 1.0f
    var left = 0.0f
    var top = 0.0f
    var right = 0.0f
    var bottom = 0.0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val c = card
        var delayX = 0.0f
        var delayY = 0.0f
        val W = measuredWidth
        if(c != null) {
            val blockWidth = measuredWidth.toFloat() / c.width
            var cont = 0
            for(i in 1..c.width) {
                for(j in 1..c.width) {
                    if(sign == -1.0f) {
                        delayX = max(cont * percentageDelayX * measuredWidth * 2f, -sign * translationX)
                        delayY = max(cont * percentageDelayY * measuredWidth * 2f, -sign * translationY)
                    }else {
                        delayX = min(cont * percentageDelayX * measuredWidth * 2f, sign * translationX)
                        delayY = min(cont * percentageDelayY * measuredWidth * 2f, sign * translationY)
                    }

                    left = (j-1)*blockWidth - delayX
                    top = (i-1)*blockWidth - delayY
                    right = (j)*blockWidth - delayX
                    bottom = (i)*blockWidth - delayY

                    if(c[c.width*(i-1)+j]) {
                        canvas.drawRect(left, top, right, bottom, filledPaint)
                        cont++
                    }
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
            duration = DURATION
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
            duration = DURATION
        }
        oa.doOnEnd {
            animationEndBlock()
        }
        oa.start()
    }

    fun animateMovement(from: PointF, to: PointF, animationEndBlock: () -> Unit) {
        val animX = ObjectAnimator.ofFloat(this, "translationX", from.x, to.x)
        val animY = ObjectAnimator.ofFloat(this, "translationY", from.y, to.y)

        val strPropertyToAnimate = when(from.x - to.x) {
            0.0f -> {
                sign = if(from.y - to.y < 0) 1.0f else -1.0f
                "percentageDelayY"
            }
            else -> {
                sign = if(from.x - to.x < 0) 1.0f else -1.0f
                "percentageDelayX"
            }
        }

        val animPercentage = ObjectAnimator.ofFloat(this, strPropertyToAnimate, sign*1.0f, 0.0f)
        AnimatorSet().apply {
            playTogether(animX, animY)
            duration = DURATION
            doOnEnd {
                animationEndBlock()
                this@CardView.translationX = 0f
                this@CardView.translationY = 0f
            }
            start()
        }

        animPercentage.apply {
            duration = DURATION
            addUpdateListener {
                invalidate()
            }
            doOnEnd {
                percentageDelayX = 0.0f
                percentageDelayY = 0.0f
            }
            start()
        }
    }

}