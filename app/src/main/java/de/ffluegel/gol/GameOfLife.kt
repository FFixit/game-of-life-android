package de.ffluegel.gol

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min
import kotlin.properties.Delegates

class GameOfLife : View {
    private val boxPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 5F
    }


    private val sizeX = 15
    private val sizeY = 20
    private var grid = Array(sizeX) { BooleanArray(sizeY) { false } }
    private var boxSize by Delegates.notNull<Float>()

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
//        grid[1][2] = true
//        grid[2][2] = true
//        grid[3][2] = true
//        grid[3][1] = true
//        grid[2][0] = true


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
            val x = (event.x / boxSize).toInt()
            val y = (event.y / boxSize).toInt()

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
        drawBoxes(canvas)
        drawGridLines(canvas)
    }

    private fun drawBoxes(canvas: Canvas) {
        for (x in 0 until sizeX) {
            for (y in 0 until sizeY) {
                if (grid[x][y]) {
                    val posX = x * boxSize
                    val posY = y * boxSize
                    canvas.drawRect(posX, posY, posX + boxSize, posY + boxSize, boxPaint)
                }
            }
        }
    }

    private fun drawGridLines(canvas: Canvas) {
        val nLines = sizeX + 1 + sizeY + 1
        val lines = FloatArray(nLines * 4)
        var i = 0
        val gridHeight = boxSize * sizeY
        for (x in 0..sizeX) {
            val posX = x * boxSize
            lines[i++] = posX
            lines[i++] = 0F
            lines[i++] = posX
            lines[i++] = gridHeight
        }
        val gridWidth = boxSize * sizeX
        for (y in 0..sizeY) {
            val posY = y * boxSize
            lines[i++] = 0F
            lines[i++] = posY
            lines[i++] = gridWidth
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)

        boxSize = min(width.toFloat() / sizeX, height.toFloat() / sizeY)

        setMeasuredDimension((boxSize * sizeX).toInt(), (boxSize * sizeY).toInt())
    }


}