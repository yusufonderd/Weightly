package com.yonder.weightly.uicomponents

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.*
import android.widget.Scroller
import androidx.appcompat.widget.TintTypedArray
import com.yonder.weightly.R
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

// Ref. https://github.com/szpnygo/RulerView

@SuppressLint("RestrictedApi")
class RulerView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : View(context, attrs, defStyleAttr) {
        private val mScroller: Scroller = Scroller(context)
        private val mVelocityTracker: VelocityTracker = VelocityTracker.obtain()
        private val mMinVelocity = ViewConfiguration.get(getContext()).scaledMinimumFlingVelocity
        private val mDensity = context.resources.displayMetrics.density

        private var mWidgetWidth: Int = 0
        private var mWidgetHeight: Int = 0

        private val mLinePaint = Paint()
        private val mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

        private val minViewHeight: Float
        private var perWidth: Float = 0.0f
        private var textHeight: Float

        private var mMove = 0f
        private var mLastX = 0

        // line width
        private var centerLineWidth = 4f
        private var sideLineWidth = 6f

        private var centerLineHeight = 0f
        private var sideLineHeight = 0f

        private var textSpaceHeight = 30f
        private var textSize: Float = 30f

        // value
        private var mValue = 50
        private var digits: Int = 0
        private var mMaxValue = 100
        private var mMinValue = 0

        private var numShow = 41
        private var unitStr = ""

        // color
        private var numTextColor: Int = Color.parseColor("#000000")
        private var sideLineColor: Int = Color.parseColor("#bcbcbc")
        private var centerLineColor: Int = Color.parseColor("#f24b16")

        private var valueListener: ((value: Float) -> Unit)? = null

        private var moveSpeed: Int = 600

        init {
            attrs?.let {
                val array =
                    TintTypedArray.obtainStyledAttributes(
                        getContext(),
                        attrs,
                        R.styleable.RulerView,
                        defStyleAttr,
                        0,
                    )

                with(array) {
                    numTextColor =
                        getColor(R.styleable.RulerView_numTextColor, Color.parseColor("#000000"))
                    sideLineColor =
                        getColor(R.styleable.RulerView_sideLineColor, Color.parseColor("#bcbcbc"))
                    centerLineColor =
                        getColor(R.styleable.RulerView_centerLineColor, Color.parseColor("#f24b16"))

                    mValue = getInt(R.styleable.RulerView_numValue, 50)
                    digits = getInt(R.styleable.RulerView_numDigits, 0)
                    mMaxValue = getInt(R.styleable.RulerView_numMaxValue, 100)
                    mMinValue = getInt(R.styleable.RulerView_numMinValue, 0)
                    numShow = getInt(R.styleable.RulerView_numShow, 41)

                    centerLineWidth = getDimension(R.styleable.RulerView_centerLineWidth, 6f)
                    sideLineWidth = getDimension(R.styleable.RulerView_sideLineWidth, 4f)
                    textSpaceHeight = getDimension(R.styleable.RulerView_textSpaceHeight, 30f)
                    textSize = getDimension(R.styleable.RulerView_textSize, 30f)

                    centerLineHeight = getDimension(R.styleable.RulerView_centerLineHeight, 0f)
                    sideLineHeight = getDimension(R.styleable.RulerView_sideLineHeight, 0f)

                    if (hasValue(R.styleable.RulerView_unitStr)) {
                        unitStr = getString(R.styleable.RulerView_unitStr)
                    }

                    moveSpeed = getInt(R.styleable.RulerView_moveSpeed, 600)

                    recycle()
                }
            }
            mValue *= 10.0.pow(digits.toDouble()).toInt()
            mMaxValue *= 10.0.pow(digits.toDouble()).toInt()
            mMinValue * 10.0.pow(digits.toDouble()).toInt()

            mTextPaint.color = numTextColor
            mTextPaint.textSize = textSize
            textHeight = mTextPaint.fontMetrics.descent - mTextPaint.fontMetrics.ascent

            if (centerLineHeight == 0f) {
                centerLineHeight = 2 * textHeight
            }
            if (sideLineHeight == 0f) {
                sideLineHeight = textHeight
            }

            minViewHeight = textHeight + centerLineHeight + textSpaceHeight + paddingTop + paddingBottom

            mLinePaint.strokeCap = Paint.Cap.ROUND
        }

        private fun getDigitsHelp(): Int = 10.0.pow(digits.toDouble()).toInt()

        fun setValueListener(listener: (value: Float) -> Unit) {
            this.valueListener = listener
        }

        fun setDigits(value: Int) {
            digits = value
            invalidate()
        }

        fun setMaxValue(max: Int) {
            mMaxValue = max * getDigitsHelp()
            invalidate()
        }

        fun setMinValue(min: Int) {
            mMinValue = min * getDigitsHelp()
            invalidate()
        }

        fun setValue(value: Float) {
            mValue = (value * getDigitsHelp()).toInt()
            invalidate()
        }

        fun getValue(): Float = (mValue.toDouble() / 10.0.pow(digits.toDouble())).nFormat(digits).toFloat()

        fun setUnitStr(str: String) {
            unitStr = str
            invalidate()
        }

        fun changeValue(value: Float) {
            mValue += (value * getDigitsHelp()).toInt()
            invalidate()
        }

        override fun onLayout(
            changed: Boolean,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
        ) {
            mWidgetHeight = height
            mWidgetWidth = width
            perWidth = mWidgetWidth / numShow.toFloat()
            super.onLayout(changed, left, top, right, bottom)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            drawScaleLine(canvas)
        }

        private fun drawScaleLine(canvas: Canvas) =
            with(canvas) {
                save()

                val centerPosition = mWidgetWidth / 2F
                var xPosition: Float

                for (i in 0..(numShow + 2)) {
                    with(mLinePaint) {
                        if (i < 4) {
                            color = centerLineColor
                            strokeWidth = centerLineWidth
                            alpha = 255
                        } else {
                            color = sideLineColor
                            strokeWidth = sideLineWidth
                            alpha = (260 - 255.toFloat() * (i.toFloat() / numShow.toFloat() * 2f)).toInt()
                        }
                    }

                    val lineHeight = getSpaceHeight(i)

                    if (i == 0 && mValue % 10 == 0) {
                        mTextPaint.color = centerLineColor
                    } else {
                        mTextPaint.color = numTextColor
                    }

                    xPosition = centerPosition + i * perWidth - mMove
                    if (xPosition + paddingEnd < mWidgetWidth && mValue + i <= mMaxValue) {
                        drawLine(
                            xPosition,
                            lineHeight,
                            xPosition,
                            mWidgetHeight.toFloat() - paddingBottom,
                            mLinePaint,
                        )
                        if ((mValue + i) % 10 == 0) {
                            val textWidth = Layout.getDesiredWidth(getShowNumber(mValue + i), mTextPaint)
                            drawText(
                                getShowNumber(mValue + i),
                                xPosition - textWidth / 2f,
                                textHeight,
                                mTextPaint,
                            )
                        }
                    }

                    xPosition = centerPosition - i * perWidth - mMove
                    if (xPosition > paddingStart && mValue - i >= mMinValue) {
                        drawLine(
                            xPosition,
                            lineHeight,
                            xPosition,
                            mWidgetHeight.toFloat() - paddingBottom,
                            mLinePaint,
                        )
                        if ((mValue - i) % 10 == 0) {
                            val textWidth = Layout.getDesiredWidth(getShowNumber(mValue + i), mTextPaint)
                            drawText(
                                getShowNumber(mValue - i),
                                xPosition - textWidth / 2f,
                                textHeight,
                                mTextPaint,
                            )
                        }
                    }
                }
                restore()
            }

        private fun getShowNumber(value: Int): String {
            if (digits == 0) {
                return value.toString() + unitStr
            }
            return (value.toDouble() / 10.0.pow(digits.toDouble())).nFormat(digits).toString() + unitStr
        }

        private fun getSpaceHeight(i: Int): Float =
            when (i) {
                0 -> (mWidgetHeight - centerLineHeight) + 0.3f * (centerLineHeight - sideLineHeight)
                1 -> (mWidgetHeight - centerLineHeight) + 0.4f * (centerLineHeight - sideLineHeight)
                2 -> (mWidgetHeight - centerLineHeight) + 0.6f * (centerLineHeight - sideLineHeight)
                3 -> (mWidgetHeight - centerLineHeight) + 0.8f * (centerLineHeight - sideLineHeight)
                else -> mWidgetHeight - sideLineHeight
            }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val action = event.action
            val xPosition = event.x.toInt()

            mVelocityTracker.addMovement(event)
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    mScroller.forceFinished(true)
                    mLastX = xPosition
                    mMove = 0f
                }
                MotionEvent.ACTION_MOVE -> {
                    mMove += (mLastX - xPosition)
                    changeMoveAndValue()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    countMoveEnd()
                    countVelocityTracker()
                    return false
                }
            }

            mLastX = xPosition
            return true
        }

        private fun countVelocityTracker() {
            mVelocityTracker.computeCurrentVelocity(moveSpeed)
            val xVelocity = mVelocityTracker.xVelocity
            if (abs(xVelocity) > mMinVelocity) {
                mScroller.fling(0, 0, xVelocity.toInt(), 0, Int.MIN_VALUE, Int.MAX_VALUE, 0, 0)
            }
            postInvalidate()
        }

        private fun countMoveEnd() {
            val roundMove = (mMove / (perWidth * mDensity)).roundToInt()
            mValue += roundMove
            mValue = if (mValue <= 0) 0 else mValue
            mValue = if (mValue > mMaxValue) mMaxValue else mValue

            mLastX = 0
            mMove = 0F

            notifyValueChange()
            postInvalidate()
        }

        private fun changeMoveAndValue() {
            val fValue = mMove / perWidth
            var tValue = fValue.toInt()
            if (abs(tValue) < 1 && abs(fValue) > 0.4) {
                tValue = if (fValue < 0) -1 else 1
            }
            if (abs(tValue) > 0) {
                mValue += tValue
                mMove -= tValue * perWidth
                if (mValue <= mMinValue || mValue > mMaxValue) {
                    mValue = if (mValue <= mMinValue) mMinValue else mMaxValue
                    mMove = 0f
                    mScroller.forceFinished(true)
                }
                notifyValueChange()
            }
            postInvalidate()
        }

        private fun notifyValueChange() {
            valueListener?.invoke((mValue.toDouble() / 10.0.pow(digits.toDouble())).nFormat(digits).toFloat())
        }

        override fun computeScroll() {
            super.computeScroll()
            if (mScroller.computeScrollOffset()) {
                if (mScroller.isFinished) { // over
                    countMoveEnd()
                } else {
                    val xPosition = mScroller.currX
                    mMove += mLastX - xPosition
                    changeMoveAndValue()
                    mLastX = xPosition
                }
            }
        }

        override fun onMeasure(
            widthMeasureSpec: Int,
            heightMeasureSpec: Int,
        ) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), minViewHeight.toInt())
            }
        }
    }

fun Double.nFormat(decimal: Int = 1): Double = String.format(Locale.US, "%.${decimal}f", this).toDouble()
