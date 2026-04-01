package com.example.homeworkmaxxing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.homeworkmaxxing.navigation.Screen
import com.example.homeworkmaxxing.ui.cours.AjoutModificationCoursPage
import com.example.homeworkmaxxing.ui.cours.MesCoursPage
import com.example.homeworkmaxxing.ui.theme.HomeworkMaxxingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HomeworkMaxxingTheme {
                HomeworkMaxxingNavHost()
            }
        }
    }
}

@Composable
private fun HomeworkMaxxingNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.CoursList.route
    ) {
        composable(Screen.CoursList.route) {
            MesCoursPage(
                viewModel = hiltViewModel(),
                onAddCoursClick = {
                    navController.navigate(Screen.CoursForm.createRoute())
                },
                onEditCoursClick = { coursId ->
                    navController.navigate(Screen.CoursForm.createRoute(coursId))
                }
            )
        }

        composable(
            route = Screen.CoursForm.route,
            arguments = listOf(
                navArgument(Screen.CoursForm.coursIdArg) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val coursId = backStackEntry.arguments?.getLong(Screen.CoursForm.coursIdArg) ?: -1L

            AjoutModificationCoursPage(
                viewModel = hiltViewModel(),
                coursId = coursId,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}
