package com.example.mensai.ui.screens.camerascreen

import android.Manifest
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mensai.ui.components.CameraPreviewView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@Composable
fun CameraScreen(
    viewModel: CameraScreenViewModel = hiltViewModel<CameraScreenViewModel>()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val imageCapture = remember { ImageCapture.Builder().build() }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CameraPreviewView(imageCapture = imageCapture, modifier = Modifier.weight(0.9f))
        Button({ viewModel.takePhoto(imageCapture) }, modifier = Modifier.weight(0.1f)) {
            Text("Take Photo")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionScreen() {
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    if (permissionState.status.isGranted) {
        CameraScreen()
    } else {
        Text("Bitte Kamera-Berechtigung erlauben", modifier = Modifier.fillMaxSize())
    }
}