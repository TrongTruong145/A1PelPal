package com.example.petpal.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.petpal.presentation.ui.AllPetsMapScreen
import com.example.petpal.presentation.ui.HomeScreen
import com.example.petpal.presentation.ui.MainScreen
import com.example.petpal.presentation.ui.MapSelectorScreen
import com.example.petpal.presentation.ui.PetDetailScreen
import com.example.petpal.presentation.ui.ReportFoundPetScreen
import com.example.petpal.presentation.ui.ReportLostPetScreen
import com.example.petpal.presentation.ui.SplashScreen

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

        // Trong file NavGraph.kt, bên trong NavHost
        composable("all_pets_map") {
            AllPetsMapScreen(navController = navController)
        }

        // Route này sẽ có dạng "pet_detail/{petId}"
        composable(
            route = "pet_detail/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Lấy petId từ arguments
            val petId = backStackEntry.arguments?.getString("petId")
            if (petId != null) {
                // Gọi PetDetailScreen và truyền petId vào
                PetDetailScreen(petId = petId, navController = navController)
            } else {
                // Xử lý trường hợp petId null (ví dụ: quay lại màn hình trước)
                navController.popBackStack()
            }
        }
    }
}