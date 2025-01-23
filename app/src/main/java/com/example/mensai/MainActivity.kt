package com.example.mensai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mensai.ui.navigation.MensAINavHost
import com.example.mensai.ui.theme.MensAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MensAITheme {
                MensAIApp()
            }
        }
    }
}

@Composable
fun MensAIApp(
    navController: NavHostController = rememberNavController()
) {
    MensAINavHost(navHostController = navController)
}