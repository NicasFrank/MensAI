package com.example.mensai.ui.screens.camerascreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mensai.domain.Foods
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executors
import kotlin.coroutines.resumeWithException

data class CameraScreenState(
    val inference: Foods = Foods.UNKNOWN
)

class CameraScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CameraScreenState())
    val uiState: StateFlow<CameraScreenState> = _uiState.asStateFlow()

    fun takePhoto(imageCapture: ImageCapture) {
        viewModelScope.launch {
            try {
                val imageProxy = captureImage(imageCapture)
                val bitmap = processImage(imageProxy)
                imageProxy.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun captureImage(imageCapture: ImageCapture): ImageProxy =
        suspendCancellableCoroutine { continuation ->
            imageCapture.takePicture(
                Executors.newSingleThreadExecutor(),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        continuation.resume(image) { image.close() }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        continuation.resumeWithException(exception)
                    }
                }
            )
        }

    private fun processImage(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

}