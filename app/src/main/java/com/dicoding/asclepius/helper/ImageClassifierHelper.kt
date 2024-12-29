import android.content.Context
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.dicoding.asclepius.R
import org.tensorflow.lite.task.core.BaseOptions

class ImageClassifierHelper(
    var thresholds: Float = 0.1f,
    var maxResult: Int = 3,
    val modelName: String = "cancer_classification.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {

    private var imageClassifier: ImageClassifier? = null

    interface ClassifierListener{
        fun onError(error: String)
        fun onResult(
            results: List<Classifications>?,
            interferenceTime: Long
        )
    }

    init{
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        // TODO: Menyiapkan Image Classifier untuk memproses gambar.
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(thresholds)
            .setMaxResults(maxResult)
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.image_classifivation_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    companion object {
        private val TAG = "ImageClassifierHelper"
    }

    fun classifyStaticImage(imageUri: Uri) {
        // TODO: Mengklasifikasikan imageUri dari gambar statis.
        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri)
            if (inputStream != null) {
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                if (bitmap != null) {
                    val startTime = System.currentTimeMillis()
                    val tensorImage = TensorImage.fromBitmap(bitmap)
                    val results = imageClassifier?.classify(tensorImage)
                    val inferenceTime = System.currentTimeMillis() - startTime
                    classifierListener?.onResult(results, inferenceTime)
                } else {
                    classifierListener?.onError("Gagal memuat gambar.")
                }
            } else {
                classifierListener?.onError("Gagal membuka gambar dari URI.")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error memproses gambar: ${e.message}")
            classifierListener?.onError("Error memproses gambar: ${e.message}")
        }
    }
}
