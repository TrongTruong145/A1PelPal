package com.example.petpal.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.petpal.presentation.ui.*

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("main") { MainScreen(navController) }

        // ✅ Định tuyến cho màn hình ReportLostPet, nhận dữ liệu trả về từ MapSelectorScreen
        composable("report_lost") { backStackEntry ->
            val locationResult = backStackEntry.savedStateHandle.get<String>("location_result")
            ReportLostPetScreen(navController, initialLocation = locationResult)
        }

        // ✅ Cập nhật: Thêm tham số initialLocation cho ReportFoundPetScreen
        composable("report_found") { backStackEntry ->
            val locationResult = backStackEntry.savedStateHandle.get<String>("location_result")
            ReportFoundPetScreen(navController, initialLocation = locationResult)
        }

        composable("home") {
            HomeScreen(navController)
        }

        // ✅ Định tuyến cho màn hình MapSelectorScreen
        composable("map_selector") {
            MapSelectorScreen(navController)
        }
    }
}