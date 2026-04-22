package com.example.homeworkmaxxing.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object CoursList : Screen("cours")
    data object Routines : Screen("routines")
    data object Settings : Screen("settings")

    data object RoutineForm : Screen("routine_form")

    data object RoutineDetail : Screen("routine_detail/{routineId}") {
        const val routineIdArg = "routineId"

        fun createRoute(routineId: Int): String = "routine_detail/$routineId"
    }

    data object EditRoutine : Screen("routine_form/{routineId}") {
        const val routineIdArg = "routineId"

        fun createRoute(routineId: Int): String = "routine_form/$routineId"
    }

    data object MesCours : Screen("cours?showSetupDialog={showSetupDialog}") {
        const val baseRoute = "cours"
        const val showSetupDialogArg = "showSetupDialog"

        fun createRoute(showSetupDialog: Boolean = false): String {
            return "$baseRoute?$showSetupDialogArg=$showSetupDialog"
        }
    }

    data object CoursForm : Screen("cours_form?coursId={coursId}") {
        const val baseRoute = "cours_form"
        const val coursIdArg = "coursId"

        fun createRoute(coursId: Long? = null): String {
            return if (coursId == null) {
                baseRoute
            } else {
                "$baseRoute?$coursIdArg=$coursId"
            }
        }
    }
}
