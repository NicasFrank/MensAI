package com.example.mensai.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.mensai.domain.Foods
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.exp


class CnnModel @Inject constructor(private val context: Context) {

    private val module = loadModel()

    fun inference(bitmap: Bitmap): Foods {
        val inputTensor = transformImage(loadTestImage() ?: bitmap)
        Log.d(
            "CnnModel",
            "Input Data: ${
                inputTensor.shape().contentToString()
            } ${inputTensor.dataAsFloatArray.contentToString()}"
        )
        val outputTensor =
            module.forward(IValue.from(inputTensor))?.toTensor()
                ?: return Foods.UNKNOWN
        Log.d("CnnModel", "OutputData: ${outputTensor.dataAsFloatArray.contentToString()}")
        val probabilities = softmax1D(outputTensor)
        Log.d("CnnModel", "Probabilities: ${probabilities.contentToString()}")
        val suggestion = findMaxProbabilityIndex(probabilities)
        Log.d("CnnModel", "Suggestion: $suggestion")
        return suggestion
    }

    private fun loadModel(): Module {
        val module = LiteModuleLoader.load(assetFilePath())
        return module
    }

    private fun assetFilePath(): String {
        val file = File(context.filesDir, "ownNet_mobile")
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }

        context.assets.open("ownNet_mobile").use { `is` ->
            FileOutputStream(file).use { os ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while ((`is`.read(buffer).also { read = it }) != -1) {
                    os.write(buffer, 0, read)
                }
                os.flush()
            }
            return file.absolutePath
        }
    }

    private fun transformImage(bitmap: Bitmap): Tensor {
        val resizedBitmap =
            Bitmap.createScaledBitmap(bitmap, 256, 256, true)
        val croppedBitmap = Bitmap.createBitmap(
            resizedBitmap,
            (resizedBitmap.width - 224) / 2,
            (resizedBitmap.height - 224) / 2,
            224,
            224
        )
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            croppedBitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        return inputTensor
    }

    private fun loadTestImage(): Bitmap? {
        return BitmapFactory.decodeStream(context.assets.open("TestBild_MangoSpargel.jpg"))
    }

    private fun softmax1D(tensor: Tensor): FloatArray {
        val data = tensor.dataAsFloatArray
        val maxLogit = data.maxOrNull() ?: 0f
        val expLogits = data.map { exp(it - maxLogit) }
        val sumExpLogits = expLogits.sum()
        return expLogits.map { it / sumExpLogits }.toFloatArray()
    }

    private fun findMaxProbabilityIndex(probabilities: FloatArray): Foods {
        val index = probabilities.indices.maxBy { probabilities[it] }
        return if (probabilities[index] >= 0.8f) Foods.entries[index + 1] else Foods.UNKNOWN
    }

}