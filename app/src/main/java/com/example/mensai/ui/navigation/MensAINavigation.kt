package com.example.mensai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mensai.ui.screens.camerascreen.CameraPermissionScreen
import kotlinx.serialization.Serializable

@Composable
fun MensAINavHost(
    navHostController: NavHostController
){
    NavHost(navController = navHostController, startDestination = CameraScreen) {
        composable<CameraScreen> {
            CameraPermissionScreen()
        }
    }
}

@Serializable
object CameraScreen