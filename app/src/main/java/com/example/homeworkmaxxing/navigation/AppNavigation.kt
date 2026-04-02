package com.example.homeworkmaxxing.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.homeworkmaxxing.ui.cours.AjoutModificationCoursPage
import com.example.homeworkmaxxing.ui.cours.MesCoursPage
import com.example.homeworkmaxxing.ui.dashboard.DashboardScreen
import com.example.homeworkmaxxing.ui.dashboard.DashboardViewModel
import com.example.homeworkmaxxing.ui.routine.RoutineFormScreen
import com.example.homeworkmaxxing.ui.routine.RoutineFormViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel = dashboardViewModel,
                onMesCoursClick = { navController.navigate(Screen.MesCours.route) },
                onAddRoutineClick = { navController.navigate(Screen.RoutineForm.route) },
                onRoutineClick = { routine ->
                    routine.id?.let { routineId ->
                        navController.navigate(Screen.EditRoutine.createRoute(routineId))
                    }
                }
            )
        }

        composable(Screen.RoutineForm.route) {
            val routineFormViewModel: RoutineFormViewModel = hiltViewModel()
            RoutineFormScreen(
                viewModel = routineFormViewModel,
                existingRoutine = null,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditRoutine.route,
            arguments = listOf(
                navArgument(Screen.EditRoutine.routineIdArg) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt(Screen.EditRoutine.routineIdArg)
            val dashboardEntry = navController.getBackStackEntry(Screen.Dashboard.route)
            val dashboardViewModel: DashboardViewModel = hiltViewModel(dashboardEntry)
            val dashboardState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
            val existingRoutine = dashboardState.routines.find { it.id == routineId }
            val routineFormViewModel: RoutineFormViewModel = hiltViewModel()

            RoutineFormScreen(
                viewModel = routineFormViewModel,
                existingRoutine = existingRoutine,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                onDelete = { navController.popBackStack() }
            )
        }

        composable(Screen.MesCours.route) {
            MesCoursPage(
                viewModel = hiltViewModel(),
                onBackClick = { navController.popBackStack() },
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
