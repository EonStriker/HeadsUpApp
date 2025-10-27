package com.example.basicscodelab

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Settings : Screen("settings")
    object DataTypesSetup : Screen("data_types_setup")
    object Layout : Screen("layout")
    object Preview : Screen("preview")
    object Export : Screen("export")
    object WidgetSelection : Screen("widget_selection")
}