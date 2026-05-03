package dev.anmitali.uaqyt.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.anmitali.uaqyt.R
import dev.anmitali.uaqyt.service.TimerService
import dev.anmitali.uaqyt.ui.about.AboutScreen
import dev.anmitali.uaqyt.ui.settings.SettingsScreen
import dev.anmitali.uaqyt.ui.settings.SettingsViewModel
import dev.anmitali.uaqyt.ui.timer.TimerScreen

sealed class Screen(val route: String, val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Timer : Screen("timer", R.string.timer, Icons.Default.PlayArrow)
    object Settings : Screen("settings", R.string.settings, Icons.Default.Settings)
    object About : Screen("about", R.string.about, Icons.Default.Info)
}

val items = listOf(
    Screen.Timer,
    Screen.Settings,
    Screen.About
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UaqytApp(
    timerService: TimerService?
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.labelRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Timer.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Timer.route) {
                TimerScreen(timerService = timerService)
            }
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(viewModel = settingsViewModel)
            }
            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }
}
