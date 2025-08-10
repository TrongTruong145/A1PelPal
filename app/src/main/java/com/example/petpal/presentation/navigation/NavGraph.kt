package com.example.petpal.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.petpal.presentation.ui.maps.AllPetsMapScreen
import com.example.petpal.presentation.ui.HomeScreen
import com.example.petpal.presentation.ui.MainScreen
import com.example.petpal.presentation.ui.maps.MapSelectorScreen
import com.example.petpal.presentation.ui.PetDetailScreen
import com.example.petpal.presentation.ui.reportforms.ReportFoundPetScreen
import com.example.petpal.presentation.ui.reportforms.ReportLostPetScreen
import com.example.petpal.presentation.ui.SplashScreen

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("main") { MainScreen(navController) }

        // ✅ Route for ReportLostPet screen, receives data returned from MapSelectorScreen
        composable("report_lost") { backStackEntry ->
            val locationResult = backStackEntry.savedStateHandle.get<String>("location_result")
            ReportLostPetScreen(navController, initialLocation = locationResult)
        }

        // ✅ Update: Add initialLocation parameter for ReportFoundPetScreen
        composable("report_found") { backStackEntry ->
            val locationResult = backStackEntry.savedStateHandle.get<String>("location_result")
            ReportFoundPetScreen(navController, initialLocation = locationResult)
        }

        composable("home") {
            HomeScreen(navController)
        }

        // ✅ Route for MapSelectorScreen
        composable("map_selector") {
            MapSelectorScreen(navController)
        }

        composable("all_pets_map") {
            AllPetsMapScreen(navController = navController)
        }

        // This route will be in the format "pet_detail/{petId}"
        composable(
            route = "pet_detail/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Get petId from arguments
            val petId = backStackEntry.arguments?.getString("petId")
            if (petId != null) {
                // Call PetDetailScreen and pass petId
                PetDetailScreen(petId = petId, navController = navController)
            } else {
                // Handle the case when petId is null (e.g., go back to the previous screen)
                navController.popBackStack()
            }
        }
    }
}
