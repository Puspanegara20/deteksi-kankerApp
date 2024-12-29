package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val imageUri = intent.getStringExtra("imageUri")
        val prediction = intent.getStringExtra("prediction")
        val confidence = intent.getFloatExtra("confidence", 0.0f)

        val resultImageView = findViewById<ImageView>(R.id.result_image)
        imageUri?.let {
            resultImageView.setImageURI(Uri.parse(it))
        }

        val predictionTextView = findViewById<TextView>(R.id.result_text)

        prediction?.let {
            val confidenceText = getString(R.string.prediction_label, it) + "\n" +
                    String.format(getString(R.string.confidence_label), confidence * 100)
            predictionTextView.text = confidenceText
        }

    }
}
