package com.example.homeworkmaxxing.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")

    data object RoutineForm : Screen("routine_form")

    data object EditRoutine : Screen("routine_form/{routineId}") {
        const val routineIdArg = "routineId"

        fun createRoute(routineId: Int): String = "routine_form/$routineId"
    }

    data object MesCours : Screen("cours")

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
