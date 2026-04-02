package com.example.homeworkmaxxing

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
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
import com.example.homeworkmaxxing.ui.dashboard.DashboardScreen
import androidx.annotation.RequiresApi
import com.example.homeworkmaxxing.navigation.AppNavigation
import com.example.homeworkmaxxing.ui.theme.HomeworkMaxxingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val askedBefore = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getBoolean(KEY_NOTIFICATION_PERMISSION_ASKED, false)
            if (askedBefore &&
                !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            ) {
                openNotificationSettings()
            }
        }
    }

    @RequiresApi(android.os.Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()
        requestExactAlarmPermissionIfNeeded()
        enableEdgeToEdge()
        setContent {
            HomeworkMaxxingTheme {
                AppNavigation()
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val askedBefore = prefs.getBoolean(KEY_NOTIFICATION_PERMISSION_ASKED, false)
        val blocked = askedBefore &&
            !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)

        if (blocked) {
            openNotificationSettings()
            return
        }

        prefs.edit().putBoolean(KEY_NOTIFICATION_PERMISSION_ASKED, true).apply()
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
    }

    private fun requestExactAlarmPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

        val alarmManager = getSystemService(AlarmManager::class.java)
        if (alarmManager.canScheduleExactAlarms()) return

        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:$packageName")
        }
        startActivity(intent)
    }

    companion object {
        private const val PREFS_NAME = "homeworkmaxxing_permissions"
        private const val KEY_NOTIFICATION_PERMISSION_ASKED = "notification_permission_asked"
    }
}

@Composable
private fun HomeworkMaxxingNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = hiltViewModel(),
                onMenuClick = {
                    navController.navigate(Screen.CoursList.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.CoursList.route)
                }
            )
        }

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
