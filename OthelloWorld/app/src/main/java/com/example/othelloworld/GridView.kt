package com.example.othelloworld

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat

/**
 * TODO: document your custom view class.
 */
class GridView : View, GestureDetector.OnGestureListener {
    var turn = 1
    var score = IntArray(2)
    var touchCount = arrayOf<Array<Int>>()
    private val gridColor = Color.BLACK

    private var mWidth: Float = 0.0f
    private var mWidthInc: Float = 0.0F
    private var mHeight: Float = 0.0F
    private var mHeightInc: Float = 0.0F

    // lateinit means we can't (or don't want to) initialize the variable
    // in a constructor. It still has to be initialized before it's used, though.
    private lateinit var mDetector: GestureDetectorCompat


    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // we could hook up the gesture detector in the constructors, instead
        mDetector = GestureDetectorCompat(this.context, this)

        for (i in 0..7) {
            var array = arrayOf<Int>()
            for (j in 0..7) {
                array += 0
            }
            touchCount += array
        }
        touchCount[3][4] = 1
        touchCount[4][3] = 1
        touchCount[3][3] = 2
        touchCount[4][4] = 2
        score[0] = 2
        score[1] = 2
    }

    // called when the view changes size, including when it's created
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // caching these so they don't have to be recomputed
        mWidth = w.toFloat()
        mWidthInc = mWidth / 8.0F
        mHeight = h.toFloat()
        mHeightInc = mHeight / 8.0F
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paint = Paint()

        paint.color = gridColor
        paint.strokeWidth = 10.0f

        val mTextPaint = TextPaint()
        mTextPaint.color = Color.BLACK
        mTextPaint.style = Paint.Style.FILL_AND_STROKE
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize = 100f

        for (i in 0 until 9) {
            canvas.drawLine(i * mWidthInc, 0.0f, i * mWidthInc, mHeight, paint)
            canvas.drawLine(0.0f, i * mHeightInc, mWidth, i * mHeightInc, paint)
        }

        for (i in 0 until 8) {
            for (j in 0 until 8) {
                if (touchCount[i][j] != 0) {
                    paint.color = if (touchCount[i][j] == 1) Color.BLACK else Color.WHITE
                    canvas.drawCircle(
                        (mWidthInc * (i + 0.5F)),
                        (mHeightInc * (j + 0.5F)),
                        mWidthInc / 3,
                        paint
                    )
                }
//                canvas.drawText(
//                    touchCount[i][j].toString(),
//                    (mWidthInc * i) + (mWidthInc / 2.0F),
//                    (mHeightInc * j) + (mHeightInc / 1.3F),
//                    mTextPaint
//                )
            }
        }
    }

    // if we get a touch event, hand it to the gesture recognizer
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    // the following six methods are part of the GestureDetector.OnGestureListener interface
    // we have to implement all of them, but only onDown actually does anything. The
    // rest are just do-nothing implementations

    override fun onDown(p0: MotionEvent?): Boolean {
        if (p0 != null) {
            // Kotlin doesn't like null getting loose, but if it knows
            // you've checked for null, subsequent references are fine

            val row = p0.x.toInt() / mWidthInc.toInt()
            val col = p0.y.toInt() / mHeightInc.toInt()

            // touchCount[row][col] = if(touchCount[row][col] == 9) 0 else touchCount[row][col] + 1

            // If location is empty
            if(touchCount[row][col] == 0 && validateMove(row, col)) {
                touchCount[row][col] = turn
                takePieces(row, col)
                checkScore()
                turn = if (turn == 1) 2 else 1
            }

            invalidate()
        }
        return true
    }

    override fun onShowPress(p0: MotionEvent?) {
        // this just means the users pressed down without moving / releasing
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        return false
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun onLongPress(p0: MotionEvent?) {
        // triggered if a long press occurred on an onDown
    }

    private fun validateMove(row: Int, col: Int): Boolean {
        // Check if an opponent is adjacent to users move
        val opponent = if(turn == 1) 2 else 1

        // Check above
        if(row > 0) {
            if (touchCount[row - 1][col] == opponent)
                return true
        }
        // Check to right
        if(col < 7) {
            if (touchCount[row][col + 1] == opponent)
                return true
        }
        // Check below
        if(row < 7) {
            if (touchCount[row + 1][col] == opponent)
                return true
        }
        // Check to left
        if(col > 0) {
            if (touchCount[row][col - 1] == opponent)
                return true
        }
        // Check above right
        if(row > 0 && col < 7) {
            if (touchCount[row - 1][col + 1] == opponent)
                return true
        }
        // Check below right
        if(row < 7 && col < 7) {
            if (touchCount[row + 1][col + 1] == opponent)
                return true
        }
        // Check below left
        if(row < 7 && col > 0) {
            if (touchCount[row + 1][col - 1] == opponent)
                return true
        }
        // Check above left
        if(row > 0 && col > 0) {
            if (touchCount[row - 1][col - 1] == opponent)
                return true
        }

        return false

    }

    private fun checkScore() {
        score = IntArray(2)
        for(i in 0..7) {
            for(j in 0..7) {
                if(touchCount[i][j] == 1)
                    score[0] += 1
                else if(touchCount[i][j] == 2)
                    score[1] += 1
            }
        }
    }

    private fun takePieces(col: Int, row: Int) {
        // Row and column are reversed in this function for some reason [col][row]
        val opponent = if(turn == 1) 2 else 1

        // Convert north pieces
        var stop = row
        var change = false

        for(location in (row - 1) downTo 0) {
            if(touchCount[col][location] == opponent)
                stop = location
            else if(touchCount[col][location] == turn) {
                change = true
                break
            }
            else if(touchCount[col][location] == 0)
                break
        }

        if(change) {
            for(location in (row - 1) downTo stop) {
                touchCount[col][location] = turn
            }
        }

        // Convert east pieces
        stop = col
        change = false

        for(location in (col + 1) until 8) {
            if(touchCount[location][row] == opponent)
                stop = location + 1
            else if(touchCount[location][row] == turn) {
                change = true
                break
            }
            else if(touchCount[location][row] == 0)
                break
        }

        if(change) {
            for(location in (col + 1) until stop) {
                touchCount[location][row] = turn
            }
        }

        // Convert south pieces
        stop = row
        change = false

        for(location in (row + 1) until 8) {
            if(touchCount[col][location] == opponent)
                stop = location + 1
            else if(touchCount[col][location] == turn) {
                change = true
                break
            }
            else if(touchCount[col][location] == 0)
                break
        }

        if(change) {
            for(location in (row + 1) until stop) {
                touchCount[col][location] = turn
            }
        }

        // Convert west pieces
        stop = col
        change = false

        for(location in (col - 1) downTo 0) {
            if(touchCount[location][row] == opponent)
                stop = location
            else if(touchCount[location][row] == turn) {
                change = true
                break
            }
            else if(touchCount[location][row] == 0)
                break
        }

        if(change) {
            for(location in (col - 1) downTo stop) {
                touchCount[location][row] = turn
            }
        }

        // Convert northeast pieces
        var stopRow = row + 1
        var stopCol = col - 1
        change = false

        while(stopRow < 8 && stopCol >= 0) {
            if(touchCount[stopCol][stopRow] == opponent) {
                stopRow++
                stopCol--
            }
            else if(touchCount[stopCol][stopRow] == turn) {
                change = true
                break
            }
            else if(touchCount[stopCol][stopRow] == 0)
                break
        }
        if(change) {
            while(stopRow >= row && stopCol < col) {
                touchCount[stopCol][stopRow] = turn
                stopRow--
                stopCol++
            }
        }


        // Convert southeast pieces
        stopRow = row + 1
        stopCol = col + 1
        change = false

        while(stopCol < 8 && stopRow < 8) {
            if(touchCount[stopCol][stopRow] == opponent) {
                stopRow++
                stopCol++
            }
            else if(touchCount[stopCol][stopRow] == turn) {
                change = true
                break
            }
            else if(touchCount[stopCol][stopRow] == 0)
                break
        }
        if(change) {
            while(stopRow >= row && stopCol >= col) {
                touchCount[stopCol][stopRow] = turn
                stopRow--
                stopCol--
            }
        }

        // Convert southwest pieces
        stopRow = row - 1
        stopCol = col + 1
        change = false

        while(stopCol < 8 && stopRow >= 0) {
            if(touchCount[stopCol][stopRow] == opponent) {
                stopRow--
                stopCol++
            }
            else if(touchCount[stopCol][stopRow] == turn) {
                change = true
                break
            }
            else if(touchCount[stopCol][stopRow] == 0)
                break
        }
        if(change) {
            while(stopRow < row && stopCol >= col) {
                touchCount[stopCol][stopRow] = turn
                stopRow++
                stopCol--
            }
        }

        // Convert northwest pieces
        stopRow = row - 1
        stopCol = col - 1
        change = false

        while(stopCol >= 0 && stopRow >= 0) {
            if(touchCount[stopCol][stopRow] == opponent) {
                stopRow--
                stopCol--
            }
            else if(touchCount[stopCol][stopRow] == turn) {
                change = true
                break
            }
            else if(touchCount[stopCol][stopRow] == 0)
                break
        }
        if(change) {
            while(stopRow < row && stopCol < col) {
                touchCount[stopCol][stopRow] = turn
                stopRow++
                stopCol++
            }
        }
    }

    fun reset() {
        turn = 1
        touchCount = arrayOf<Array<Int>>()

        for (i in 0..7) {
            var array = arrayOf<Int>()
            for (j in 0..7) {
                array += 0
            }
            touchCount += array
        }
        touchCount[3][4] = 1
        touchCount[4][3] = 1
        touchCount[3][3] = 2
        touchCount[4][4] = 2
        score[0] = 2
        score[1] = 2
        invalidate()
    }
}
