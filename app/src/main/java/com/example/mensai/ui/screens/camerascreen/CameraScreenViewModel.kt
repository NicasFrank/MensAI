package com.example.mensai.ui.screens.camerascreen

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mensai.data.ImageClassificationRepository
import com.example.mensai.domain.Foods
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

data class CameraScreenState(
    val loading: Boolean = false,
    val showBottomSheet: Boolean = false,
    val detectedFood: Foods = Foods.UNKNOWN
)

@HiltViewModel
class CameraScreenViewModel @Inject constructor(
    private val repository: ImageClassificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraScreenState())
    val uiState: StateFlow<CameraScreenState> = _uiState.asStateFlow()

    fun takePhoto(imageCapture: ImageCapture) {
        var detectedFood = Foods.UNKNOWN
        _uiState.update {
            it.copy(
                loading = true
            )
        }
        viewModelScope.launch {
            try {
                val imageProxy = captureImage(imageCapture)
                val bitmap = imageProxy.toBitmap()
                imageProxy.close()
                detectedFood = repository.classifyImage(bitmap)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            _uiState.update {
                it.copy(
                    loading = false,
                    showBottomSheet = true,
                    detectedFood = detectedFood
                )
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

    fun dismissBottomSheet(){
        _uiState.update {
            it.copy(
                showBottomSheet = false,
                detectedFood = Foods.UNKNOWN
            )
        }
    }
}