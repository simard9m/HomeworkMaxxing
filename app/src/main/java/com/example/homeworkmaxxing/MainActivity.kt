package com.example.homeworkmaxxing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.example.homeworkmaxxing.navigation.AppNavigation
import com.example.homeworkmaxxing.ui.theme.HomeworkMaxxingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(android.os.Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeworkMaxxingTheme {
                AppNavigation()
            }
        }
    }
}
