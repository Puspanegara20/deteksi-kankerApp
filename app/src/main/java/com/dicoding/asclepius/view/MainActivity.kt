package com.dicoding.asclepius.view

import ImageClassifierHelper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.R
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {

    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val galleryButton: Button = findViewById(R.id.galleryButton)
        val analyzeButton: Button = findViewById(R.id.analyzeButton)

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    showToast(error)
                }

                override fun onResult(results: List<Classifications>?, inferenceTime: Long) {
                    moveToResult(results)
                }
            }
        )

        galleryButton.setOnClickListener {
            startGallery()
        }

        analyzeButton.setOnClickListener {
            analyzeImage()
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            showImage()
        } ?: showToast("Gambar tidak ditemukan.")
    }

    private fun startGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun showImage() {
        currentImageUri?.let {
            val previewImageView: ImageView = findViewById(R.id.previewImageView)
            previewImageView.setImageURI(it)
        } ?: showToast("Gambar tidak ditemukan.")
    }

    private fun analyzeImage() {
        currentImageUri?.let {
            imageClassifierHelper.classifyStaticImage(it)
        } ?: showToast("Pilih gambar terlebih dahulu.")
    }

    private fun moveToResult(results: List<Classifications>?) {
        val intent = Intent(this, ResultActivity::class.java)
        if (results != null && results.isNotEmpty()) {
            val categories = results.first().categories
            if (categories.isNotEmpty()) {
                val category = categories[0]
                val confidence = category.score
                intent.putExtra("prediction", category.label)
                intent.putExtra("confidence", confidence)
                intent.putExtra("imageUri", currentImageUri.toString()) // Pass the image URI
            } else {
                showToast("Tidak ada kategori yang ditemukan.")
            }
        } else {
            showToast("Gagal mengklasifikasikan gambar.")
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
