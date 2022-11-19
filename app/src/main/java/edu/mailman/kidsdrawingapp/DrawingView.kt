package edu.mailman.kidsdrawingapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView (context: Context, attrs: AttributeSet) : View(context, attrs){
    private lateinit var drawPath: CustomPath
    private lateinit var canvasBitmap: Bitmap
    private lateinit var drawPaint: Paint
    private lateinit var canvasPaint: Paint
    private var brushSize: Float = 0F
    private var color = Color.BLACK
    private lateinit var canvas: Canvas
    private val paths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        drawPaint = Paint()
        drawPath = CustomPath(color, brushSize)
        drawPaint.color = color
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)
        // brushSize = 20F  // No longer needed here
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Create a bitmap for the canvas
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)

        for (path in paths) {
            drawPaint.strokeWidth = path.brushThickness
            drawPaint.color = path.color
            canvas.drawPath(path, drawPaint)
        }

        if (!drawPath.isEmpty) {
            drawPaint.strokeWidth = drawPath.brushThickness
            drawPaint.color = drawPath.color
            canvas.drawPath(drawPath, drawPaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Start a new path
                drawPath.color = color
                drawPath.brushThickness = brushSize
                drawPath.reset()
                drawPath.moveTo(touchX, touchY)
            }

            MotionEvent.ACTION_MOVE -> {
                // Continue to draw the path
                drawPath.lineTo(touchX, touchY)
            }

            MotionEvent.ACTION_UP -> {
                // Store each path in an ArrayList
                paths.add(drawPath)

                // Draw the path
                drawPath = CustomPath(color, brushSize)
            }

            else -> return false
        }

        invalidate()

        return true
    }

    fun setSizeForBrush(newSize: Float) {
        brushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            newSize, resources.displayMetrics)
        drawPaint.strokeWidth = brushSize
    }

    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        drawPaint.color = color
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path()


}