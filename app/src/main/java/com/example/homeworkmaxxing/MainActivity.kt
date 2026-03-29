package com.example.homeworkmaxxing

import DashboardScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.example.homeworkmaxxing.data.model.source.RoutinesDatabase
import com.example.homeworkmaxxing.ui.dashboard.DashboardViewModel
import com.example.homeworkmaxxing.ui.theme.HomeworkMaxxingTheme

class MainActivity : ComponentActivity() {

    private val dashboardViewModel: DashboardViewModel by viewModels()

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            RoutinesDatabase::class.java,
            RoutinesDatabase.DATABASE_NAME
        ).build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HomeworkMaxxingTheme {
                //DashboardScreen already contains a Scaffold + FAB + TopBar
                DashboardScreen(
                    viewModel = dashboardViewModel
                )
            }
        }
    }
}