package com.example.petpal.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation // Import cái này
import com.example.petpal.presentation.ui.HomeScreen
import com.example.petpal.presentation.ui.MainScreen
import com.example.petpal.presentation.ui.MapScreen
import com.example.petpal.presentation.ui.ReportFoundPetScreen
import com.example.petpal.presentation.ui.ReportLostPetScreen

// Biến thành một extension function của NavGraphBuilder
// Nó sẽ định nghĩa một đồ thị con (nested graph) cho các màn hình chính
@RequiresApi(Build.VERSION_CODES.S)
fun NavGraphBuilder.mainAppGraph(navController: NavHostController) {
    // Sử dụng navigation() để tạo một đồ thị con, giúp quản lý tốt hơn
    // Tất cả các màn hình chính sẽ nằm trong này
    navigation(startDestination = "main", route = "main_graph") {
        composable("main") { MainScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("report_lost") { ReportLostPetScreen(navController) }
        composable("report_found") { ReportFoundPetScreen(navController) }
        composable("map") { MapScreen(navController) }

        // Bạn có thể thêm các màn hình khác của ứng dụng vào đây
    }
}