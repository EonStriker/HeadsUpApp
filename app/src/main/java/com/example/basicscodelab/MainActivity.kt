package com.example.basicscodelab

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basicscodelab.ui.theme.BasicsCodelabTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        // Hides system bars?  ideally allows swipe to reveal  works lol!
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            BasicsCodelabTheme {
                val navController = rememberNavController()
                val selectedWidgets = remember { mutableStateListOf<String>() }
                val widgetStates = remember { mutableStateListOf<Widget>() }

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Welcome.route
                    ) {
                        composable(Screen.Welcome.route) {
                            WelcomeScreen(navController)
                        }
                        composable(Screen.WidgetSelection.route) {
                            WidgetSelectionScreen(
                                navController = navController,
                                selectedWidgets = selectedWidgets,
                                onWidgetsSelected = { selected: List<String> ->
                                    selectedWidgets.clear()
                                    selectedWidgets.addAll(selected)
                                }
                            )
                        }
                        composable(Screen.Layout.route) {
                            LayoutScreen(navController, selectedWidgets, widgetStates)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(navController)
                        }
                        composable(Screen.Preview.route) {
                            PreviewScreen(navController, widgetStates)
                        }
                        composable(Screen.Export.route) {
                            ExportScreen(navController, widgetStates)
                        }
                    }
                }
            }
        }
    }
}