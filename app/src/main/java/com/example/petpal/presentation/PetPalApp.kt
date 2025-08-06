package com.example.petpal.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // Import
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Import
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.petpal.app.LoginScreen
import com.example.petpal.presentation.navigation.mainAppGraph // Import hàm vừa sửa
import com.example.petpal.presentation.ui.SplashScreen
import com.example.petpal.presentation.viewmodel.AuthViewModel


@RequiresApi(Build.VERSION_CODES.S) // Thêm annotation này vì mainAppGraph yêu cầu
@Composable
fun PetPalApp(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()

    // Lấy trạng thái người dùng một cách chính xác từ ViewModel
    // Sử dụng collectAsStateWithLifecycle để an toàn với vòng đời
    val user by authViewModel.user.collectAsStateWithLifecycle()
    // startDestination luôn là "splash" để màn hình này quyết định luồng đi tiếp theo
    NavHost(navController = navController, startDestination = "splash") {

        // Route 1: Màn hình Splash
        composable("splash") {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    // Điều hướng đến đồ thị con của các màn hình chính
                    navController.navigate("main_graph") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        // Route 2: Màn hình Login
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                navController = navController
            )
        }

        // Route 3: Toàn bộ các màn hình chính của ứng dụng
        // Gọi hàm từ NavGraph.kt để xây dựng các route này
        mainAppGraph(navController = navController)
    }
}