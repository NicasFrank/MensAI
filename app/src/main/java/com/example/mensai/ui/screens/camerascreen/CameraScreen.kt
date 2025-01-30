package com.example.mensai.ui.screens.camerascreen

import android.Manifest
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mensai.R
import com.example.mensai.domain.Foods
import com.example.mensai.domain.getFullName
import com.example.mensai.domain.getPrice
import com.example.mensai.ui.components.CameraPreviewView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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
        CameraPreviewView(imageCapture = imageCapture, modifier = Modifier.weight(1f))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(100.dp)
                .background(MaterialTheme.colorScheme.scrim)
                .fillMaxSize()
        ) {
            IconButton(
                { viewModel.takePhoto(imageCapture) },
                modifier = Modifier.size(50.dp),
                colors = IconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.Gray
                ),
                enabled = !uiState.loading
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                    contentDescription = null
                )
            }
        }
    }
    if (uiState.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissBottomSheet() }
        ) {
            if (uiState.detectedFood == Foods.UNKNOWN) {
                NoFoodDetectedText { viewModel.dismissBottomSheet() }
            } else {
                FoodDetectedText(uiState) { viewModel.dismissBottomSheet() }
            }
        }
    }
}

@Composable
fun FoodDetectedText(uiState: CameraScreenState, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "${uiState.detectedFood.getFullName()} - ${
                String.format(
                    Locale.getDefault(),
                    "%.2f",
                    uiState.detectedFood.getPrice()
                )
            }€"
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onClick) {
            Text("Zum Checkout")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onClick, colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.Gray
            )
        ) {
            Text("Neu Versuchen")
        }
    }
}

@Composable
fun NoFoodDetectedText(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Erkennung fehlgeschlagen. Bitte erneut versuchen!")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onClick) {
            Text("Schließen")
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
        Text("Please allow camera usage", modifier = Modifier.fillMaxSize())
    }
}