package com.example.homeworkmaxxing.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.homeworkmaxxing.ui.dashboard.DashboardScreen
import com.example.homeworkmaxxing.ui.dashboard.DashboardViewModel
import com.example.homeworkmaxxing.ui.routine.RoutineFormScreen
import com.example.homeworkmaxxing.ui.routine.RoutineFormViewModel

// ─────────────────────────────────────────────
// Routes
// ─────────────────────────────────────────────

sealed class AppScreen(val route: String) {
    data object Dashboard : AppScreen("dashboard")
    data object AddRoutine : AppScreen("routine/add")
    data object EditRoutine : AppScreen("routine/edit/{routineId}") {
        fun createRoute(routineId: Int) = "routine/edit/$routineId"
    }
}

// ─────────────────────────────────────────────
// NavHost
// ─────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Shared ViewModel scoped to the NavGraph so the dashboard
    // state persists across back-navigation
    val dashboardViewModel: DashboardViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppScreen.Dashboard.route
    ) {

        // ── Dashboard ──────────────────────────────
        composable(AppScreen.Dashboard.route) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onAddRoutineClick = {
                    navController.navigate(AppScreen.AddRoutine.route)
                },
                onRoutineClick = { routine ->
                    routine.id?.let { id ->
                        navController.navigate(AppScreen.EditRoutine.createRoute(id))
                    }
                }
            )
        }

        // ── Ajout routine ────────────────────────────
        composable(AppScreen.AddRoutine.route) {
            val formViewModel: RoutineFormViewModel = viewModel()
            RoutineFormScreen(
                viewModel = formViewModel,
                existingRoutine = null,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        // ── Modif routine ───────────────────────────
        composable(
            route = AppScreen.EditRoutine.route,
            arguments = listOf(navArgument("routineId") { type = NavType.IntType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt("routineId")
            val dashboardState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
            val routine = dashboardState.routines.find { it.id == routineId }

            val formViewModel: RoutineFormViewModel = viewModel()
            RoutineFormScreen(
                viewModel = formViewModel,
                existingRoutine = routine,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                onDelete = { navController.popBackStack() }
            )
        }
    }
}
