package com.example.petpal.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.petpal.presentation.ui.MainScreen
import com.example.petpal.presentation.ui.ReportLostPetScreen
import com.example.petpal.presentation.ui.SplashScreen
import com.example.petpal.presentation.ui.ReportFoundPetScreen
import com.example.petpal.presentation.ui.HomeScreen
import com.example.petpal.presentation.ui.MapScreen


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("main") { MainScreen(navController) }

        // 👇 Route mới để mở màn hình báo mất thú cưng
        composable("report_lost") {
            ReportLostPetScreen(navController)
        }

        composable("report_found") {
            ReportFoundPetScreen(navController)
        }

        composable("home") {
            HomeScreen(navController)
        }


        composable("map") {
            MapScreen(navController)
        }

    // các màn khác thêm sau:
        // composable("home") { HomeScreen(...) }
        // composable("report_lost") { ReportLostPetScreen(...) }
        // composable("report_found") { ReportFoundPetScreen(...) }
    }
}
