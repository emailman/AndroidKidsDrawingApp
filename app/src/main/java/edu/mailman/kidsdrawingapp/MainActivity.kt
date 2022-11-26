package edu.mailman.kidsdrawingapp

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    private lateinit var imageButtonCurrentPaint: ImageButton

    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val imageBackground: ImageView = findViewById(R.id.iv_background)
                imageBackground.setImageURI(result.data?.data)
            }
        }

    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    Toast.makeText(
                        this,
                        "Permissions granted to read storage files",
                        Toast.LENGTH_LONG
                    ).show()

                    val pickIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(pickIntent)

                } else {
                    if (permissionName == android.Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(
                            this,
                            "Permissions DENIED to read storage files",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

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

        val ibGallery: ImageButton = findViewById(R.id.ib_gallery)
        ibGallery.setOnClickListener {
            requestStoragePermission()
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

    fun paintClicked(view: View) {
        if (view != imageButtonCurrentPaint) {
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawingView.setColor(colorTag)

            // Show the selected button as pressed
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed))

            // Show the prior button as normal
            imageButtonCurrentPaint.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_normal))

            // This is the new current button
            imageButtonCurrentPaint = view
        }
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
            this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showRationaleDialog("Kids Drawing App",
                "App needs access to external storage for background images")
        } else {
            requestPermission.launch(arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ))
        }
    }

    private fun showRationaleDialog (
        title: String,
        message: String
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }
}