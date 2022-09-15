package de.ffluegel.gol

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class GameOfLife : View {
    private val boxPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 5F
    }


    private val sizeX = 10
    private val sizeY = 15
    private var grid = Array(sizeX) { BooleanArray(sizeY) { false } }

    private val boxWidth get() = width.toFloat() / sizeX
    private val boxHeight get() = height.toFloat() / sizeY

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        grid[1][2] = true
        grid[2][2] = true
        grid[3][2] = true
        grid[3][1] = true
        grid[2][0] = true

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GameOfLife,
            0, 0
        ).apply {
            try {
                boxPaint.color = getColor(R.styleable.GameOfLife_colorPrimary, Color.BLACK)
                linePaint.color = getColor(R.styleable.GameOfLife_colorGridLines, Color.WHITE)
            } finally {
                recycle()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_DOWN) {
            val x = (event.x / boxWidth).toInt()
            val y = (event.y / boxHeight).toInt()

            grid[x][y] = !grid[x][y]
            invalidate()
        }
        return true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawGrid(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        drawBoxes(canvas, boxWidth, boxHeight)
        drawGridLines(canvas, boxWidth, boxHeight)
    }

    private fun drawBoxes(canvas: Canvas, boxWidth: Float, boxHeight: Float) {
        for (x in 0 until sizeX) {
            for (y in 0 until sizeY) {
                if (grid[x][y]) {
                    val posX = x * boxWidth
                    val posY = y * boxHeight
                    canvas.drawRect(posX, posY, posX + boxWidth, posY + boxHeight, boxPaint)
                }
            }
        }
    }

    private fun drawGridLines(canvas: Canvas, boxWidth: Float, boxHeight: Float) {
        val nLines = sizeX + 1 + sizeY + 1
        val lines = FloatArray(nLines * 4)
        var i = 0
        for (x in 0..sizeX) {
            val posX = x * boxWidth
            lines[i++] = posX
            lines[i++] = 0F
            lines[i++] = posX
            lines[i++] = height.toFloat()
        }
        for (y in 0..sizeY) {
            val posY = y * boxHeight
            lines[i++] = 0F
            lines[i++] = posY
            lines[i++] = width.toFloat()
            lines[i++] = posY
        }
        canvas.drawLines(lines, linePaint)
    }

    fun nextTick() {
        val newGrid = Array(sizeX) { BooleanArray(sizeY) { false } }

        for (x in 0 until sizeX) {
            for (y in 0 until sizeY) {
                val nNeighbours = countNeighbours(x, y)
                if (grid[x][y]) {
                    if (nNeighbours in 2..3) {
                        newGrid[x][y] = true
                    }
                } else {
                    if (nNeighbours == 3) {
                        newGrid[x][y] = true
                    }
                }
            }
        }
        grid = newGrid
        invalidate()
    }

    private fun countNeighbours(x: Int, y: Int): Int {
        var nNeighbours = 0

        if (x > 0) { // left
            nNeighbours += if (grid[x - 1][y]) 1 else 0
        }
        if (x > 0 && y > 0) { // top left
            nNeighbours += if (grid[x - 1][y - 1]) 1 else 0
        }
        if (x > 0 && y < sizeY - 1) { // bottom left
            nNeighbours += if (grid[x - 1][y + 1]) 1 else 0
        }
        if (x < sizeX - 1) { // right
            nNeighbours += if (grid[x + 1][y]) 1 else 0
        }
        if (x < sizeX - 1 && y > 0) { // top right
            nNeighbours += if (grid[x + 1][y - 1]) 1 else 0
        }
        if (x < sizeX - 1 && y < sizeY - 1) { // bottom right
            nNeighbours += if (grid[x + 1][y + 1]) 1 else 0
        }
        if (y > 0) {
            nNeighbours += if (grid[x][y - 1]) 1 else 0
        }
        if (y < sizeY - 1) {
            nNeighbours += if (grid[x][y + 1]) 1 else 0
        }
        return nNeighbours
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        // Try for a width based on our minimum
//        val minw: Int = paddingLeft + suggestedMinimumWidth
//        val w: Int = View.resolveSizeAndState(minw, widthMeasureSpec, 1)
//
//        // Whatever the width ends up being, ask for a height that would let the pie
//        // get as big as it can
//        val minh: Int = View.MeasureSpec.getSize(w) + paddingBottom + paddingTop
//        val h: Int = View.resolveSizeAndState(minh, heightMeasureSpec, 0)
//
//        setMeasuredDimension(w, h)
//    }


}