package com.example.mensai.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.mensai.domain.Foods
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.math.exp

class CnnModel @Inject constructor(private val context: Context) {

    private val module = loadModel()

    fun inference(bitmap: Bitmap): Foods {
        val testImage = loadTestImage()
        val resizedBitmap =
            Bitmap.createScaledBitmap(testImage ?: bitmap, 224, 224, true)
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            resizedBitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        val outputTensor =
            module?.forward(IValue.from(inputTensor))?.toDictStringKey()?.get("logits")?.toTensor()
                ?: return Foods.UNKNOWN
        val suggestion = findMaxProbabilityIndex(softmax1D(outputTensor))
        Log.d("CnnModel", "Output Dictionary: $suggestion")
        return suggestion
    }

    private fun loadModel(): Module? {
        var module: Module? = null
        try {
            val assetManager = context.assets
            val inputStream = assetManager.open("efficient_net_food101_mobile.ptl")
            val tempFile = File(context.filesDir, "efficient_net_food101_mobile.ptl")
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            module = Module.load(tempFile.absolutePath)
            tempFile.delete()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return module
    }

    private fun loadTestImage(): Bitmap? {
        var image: Bitmap? = null
        try {
            val assetManager = context.assets
            val inputStream = assetManager.open("TestBild.jpg")
            image = BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    private fun softmax1D(tensor: Tensor): FloatArray {
        val data = tensor.dataAsFloatArray
        Log.d("CnnModel", "Output Dictionary: ${data.contentToString()}")
        val maxLogit = data.maxOrNull() ?: 0f
        val expLogits = data.map { exp(it - maxLogit) }
        val sumExpLogits = expLogits.sum()
        return expLogits.map { it / sumExpLogits }.toFloatArray()
    }

    private fun findMaxProbabilityIndex(probabilities: FloatArray): Foods {
        Log.d("CnnModel", "Output Dictionary: ${probabilities.contentToString()}")
        val index = probabilities.indices.maxBy { probabilities[it] }
        return if (probabilities[index] >= 0.8f) Foods.entries[index + 1] else Foods.UNKNOWN
    }

}