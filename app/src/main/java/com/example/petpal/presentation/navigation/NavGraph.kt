package com.example.petpal.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.petpal.presentation.ui.MainScreen
import com.example.petpal.presentation.ui.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("main") { MainScreen(navController) }
        // các màn khác thêm sau:
        // composable("home") { HomeScreen(...) }
        // composable("report_lost") { ReportLostPetScreen(...) }
        // composable("report_found") { ReportFoundPetScreen(...) }
    }
}
