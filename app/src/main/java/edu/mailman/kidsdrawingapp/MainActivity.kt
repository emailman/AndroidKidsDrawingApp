package edu.mailman.kidsdrawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    private lateinit var imageButtonCurrentPaint: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawing_view)
        // Set brush size based on screen metrics
        drawingView.setSizeForBrush(20F)

        val linearLayoutPaintColors = findViewById<LinearLayout>(R.id.ll_paint_colors)
        // Set current paint color to index 2 in the linear layout
        imageButtonCurrentPaint = linearLayoutPaintColors[2] as ImageButton
        imageButtonCurrentPaint.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        val ibBrush: ImageButton = findViewById(R.id.ib_brush)
        ibBrush.setOnClickListener {
            showBrushSizeChooserDialog()
        }
    }

    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size: ")

        val smallButton: ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        smallButton.setOnClickListener {
            drawingView.setSizeForBrush(10F)
            brushDialog.dismiss()
        }

        val mediumButton: ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumButton.setOnClickListener {
            drawingView.setSizeForBrush(20F)
            brushDialog.dismiss()
        }

        val largeButton: ImageButton = brushDialog.findViewById(R.id.ib_large_brush)
        largeButton.setOnClickListener {
            drawingView.setSizeForBrush(30F)
            brushDialog.dismiss()
        }

        brushDialog.show()
    }
}