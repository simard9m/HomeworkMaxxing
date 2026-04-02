package com.example.homeworkmaxxing.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object CoursList : Screen("cours")

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
