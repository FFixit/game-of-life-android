package de.ffluegel.gol

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit
import kotlin.math.floor
import kotlin.properties.Delegates

class GameOfLife : View {
    private val boxPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 0F
    }

    private val sizeX = 15
    private var sizeY = 20
    private var grid = HashSet<Coordinates>(100)
    private var boxSize by Delegates.notNull<Float>()
    private var offsetX = 0F
    private var offsetY = 0F
    private var isRunning = false

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapUp(event: MotionEvent): Boolean {
            if (isRunning) {
                return false
            }
            val x = floor(((event.x - offsetX) / boxSize)).toInt()
            val y = floor(((event.y - offsetY) / boxSize)).toInt()

            val cords = Coordinates(x, y)
            if (grid.contains(cords)) {
                grid.remove(cords)
            } else {
                grid.add(cords)
            }
            invalidate()
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            offsetX -= distanceX
            offsetY -= distanceY
            invalidate()
            return true
        }
    }
    private val gestureDetector: GestureDetector = GestureDetector(context, gestureListener)

    private val interval = Observable.interval(100, TimeUnit.MILLISECONDS)
    private lateinit var disposable: Disposable

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

    fun startGame() {
        if (!isRunning) {
            disposable = interval.subscribe { nextTick() }
            isRunning = true
        }
    }

    fun pauseGame() {
        disposable.dispose()
        isRunning = false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        drawGrid(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        drawBoxes(canvas)
        drawGridLines(canvas)
    }

    private fun drawBoxes(canvas: Canvas) {
        for (cords in grid) {
            val posX = cords.getX() * boxSize + offsetX
            val posY = cords.getY() * boxSize + offsetY
            canvas.drawRect(posX, posY, posX + boxSize, posY + boxSize, boxPaint)

        }
    }

    private fun drawGridLines(canvas: Canvas) {
        val nLines = sizeX + 1 + sizeY + 1
        val lines = FloatArray(nLines * 4)
        var i = 0
        val gridHeight = boxSize * sizeY
        for (x in 0..sizeX) {
            val posX = x * boxSize + (offsetX % boxSize)
            lines[i++] = posX
            lines[i++] = 0F
            lines[i++] = posX
            lines[i++] = gridHeight
        }
        val gridWidth = boxSize * sizeX
        for (y in 0..sizeY) {
            val posY = y * boxSize + (offsetY % boxSize)
            lines[i++] = 0F
            lines[i++] = posY
            lines[i++] = gridWidth
            lines[i++] = posY
        }
        canvas.drawLines(lines, linePaint)
    }

    fun nextTick() {
        val newGrid = HashSet<Coordinates>(100)

        for (cords in grid) {
            processBox(cords, newGrid)
            processNeighbours(cords, newGrid)
        }
        grid = newGrid
        invalidate()
    }

    private fun processBox(cords: Coordinates, newGrid: HashSet<Coordinates>) {
        if (newGrid.contains(cords)) {
            return
        }
        val nNeighbours = countNeighbours(cords.getX(), cords.getY())
        if (grid.contains(cords)) {
            if (nNeighbours in 2..3) {
                newGrid.add(Coordinates(cords.getX(), cords.getY()))
            }
        } else {
            if (nNeighbours == 3) {
                newGrid.add(Coordinates(cords.getX(), cords.getY()))
            }
        }

    }

    private fun processNeighbours(cords: Coordinates, newGrid: HashSet<Coordinates>) {
        processBox(Coordinates(cords.getX() - 1, cords.getY()), newGrid) // left
        processBox(Coordinates(cords.getX() - 1, cords.getY() - 1), newGrid) // top left
        processBox(Coordinates(cords.getX() - 1, cords.getY() + 1), newGrid) // bottom left
        processBox(Coordinates(cords.getX() + 1, cords.getY()), newGrid) // right
        processBox(Coordinates(cords.getX() + 1, cords.getY() - 1), newGrid) // top right
        processBox(Coordinates(cords.getX() + 1, cords.getY() + 1), newGrid) // bottom right
        processBox(Coordinates(cords.getX(), cords.getY() - 1), newGrid) // top
        processBox(Coordinates(cords.getX(), cords.getY() + 1), newGrid) // bottom
    }

    private fun countNeighbours(x: Int, y: Int): Int {
        var nNeighbours = 0

        nNeighbours += if (grid.contains(Coordinates(x - 1, y))) 1 else 0 // left
        nNeighbours += if (grid.contains(Coordinates(x - 1, y - 1))) 1 else 0 // top left
        nNeighbours += if (grid.contains(Coordinates(x - 1, y + 1))) 1 else 0 // bottom left
        nNeighbours += if (grid.contains(Coordinates(x + 1, y))) 1 else 0 // right
        nNeighbours += if (grid.contains(Coordinates(x + 1, y - 1))) 1 else 0 // top right
        nNeighbours += if (grid.contains(Coordinates(x + 1, y + 1))) 1 else 0 // bottom right
        nNeighbours += if (grid.contains(Coordinates(x, y - 1))) 1 else 0 // top
        nNeighbours += if (grid.contains(Coordinates(x, y + 1))) 1 else 0 // bottom

        return nNeighbours
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)

        boxSize = width.toFloat() / sizeX
        sizeY = (height.toFloat() / boxSize).toInt()

        setMeasuredDimension((boxSize * sizeX).toInt(), (boxSize * sizeY).toInt())
    }


}