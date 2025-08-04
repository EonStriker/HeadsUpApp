package com.example.basicscodelab

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Settings : Screen("settings")
    object Layout : Screen("layout")
    object Preview : Screen("preview")
    object Export : Screen("export")
    object WidgetSelection : Screen("widget_selection")
}