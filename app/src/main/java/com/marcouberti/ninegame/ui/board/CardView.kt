package com.marcouberti.ninegame.ui.board

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.marcouberti.ninegame.model.Card
import android.util.TypedValue
import androidx.core.animation.doOnEnd
import com.marcouberti.ninegame.model.get
import android.animation.ValueAnimator
import android.graphics.*
import android.animation.ArgbEvaluator
import android.view.animation.BounceInterpolator
import com.marcouberti.ninegame.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max


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

    //var percentageDelayX = 1.0f
    //var percentageDelayY = 1.0f
    var percentageZoom = 1.0f
    var percentage = 0.0f
    var orientation = 1.0f
    var left = 0.0f
    var top = 0.0f
    var right = 0.0f
    var bottom = 0.0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val c = card
        //var delayX: Float
        //var delayY: Float
        var zoom: Float
        val W = measuredWidth
        if(c != null) {
            val blockWidth = W / c.width
            var cont = 0
            for(i in 1..c.width) {
                for(j in 1..c.width) {
                    zoom = blockWidth/2f - ((blockWidth/2f) * abs(percentageZoom))

                    val contribution = max(cont*10, 1)

                    val delayX = - (translationX/contribution) * (1-percentage)
                    val delayY = - (translationY/contribution) * (1-percentage)

                    left = (j-1)*blockWidth - zoom + delayX
                    top = (i-1)*blockWidth - zoom + delayY
                    right = (j)*blockWidth + zoom + delayX
                    bottom = (i)*blockWidth + zoom +delayY

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
        val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1F, 1.5F, 1F)
        val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1F, 1.5F, 1F)

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

    fun animateNew(animationStartBlock: () -> Unit) {

        val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0.0F, 1F)
        val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0.0F, 1F)

        val animatorSet= AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            interpolator = BounceInterpolator()
            duration = DURATION

            doOnEnd {
                this@CardView.scaleX = 1f
                this@CardView.scaleY = 1f
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            delay(DURATION*2)
            animationStartBlock()
            animatorSet.start()
        }
    }

    fun animateMovement(from: PointF, to: PointF, animationEndBlock: () -> Unit, merge: Boolean = false) {
        val animX = ObjectAnimator.ofFloat(this, "translationX", from.x, to.x)
        val animY = ObjectAnimator.ofFloat(this, "translationY", from.y, to.y)

        orientation = when(from.x - to.x) {
            0.0f -> -1f
            else -> 1f
        }

        val colorFrom = resources.getColor(R.color.black)
        val colorTo = resources.getColor(R.color.colorPrimary)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo, colorFrom).apply {
            addUpdateListener {
                filledPaint.color = it.animatedValue as Int
                invalidate()
            }
        }

        val animZoom = ObjectAnimator.ofFloat(this, "percentageZoom", 1.0f, 0f, 1.0f).apply {
            addUpdateListener {
                invalidate()
            }
        }

        val percAnim = ObjectAnimator.ofFloat(this, "percentage",  0f, 1.0f)

        AnimatorSet().apply {
            play(animX).with(animY)
            play(animZoom).with(animX)
            play(percAnim).with(animX)
            if(merge) {
                play(colorAnimation).after(animX)
            }
            duration = DURATION
            doOnEnd {
                animationEndBlock()
                this@CardView.translationX = 0f
                this@CardView.translationY = 0f
            }
            start()
        }
    }

}