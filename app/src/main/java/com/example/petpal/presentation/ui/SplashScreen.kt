package com.example.petpal.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petpal.R
import com.example.petpal.presentation.viewmodel.AuthViewModel

@Composable
fun SplashScreen(
    // Thay đổi 1: Thay đổi hoàn toàn tham số của hàm.
    // Không còn dùng NavController trực tiếp.
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // Thay đổi 2: Lấy trạng thái người dùng một cách an toàn từ ViewModel
    val user by authViewModel.user.collectAsStateWithLifecycle()

    // Thay đổi 3: LaunchedEffect giờ sẽ quyết định điều hướng dựa trên trạng thái user
    LaunchedEffect(key1 = user) {
        if (user == null) {
            // Nếu chưa đăng nhập, gọi callback để điều hướng tới Login
            onNavigateToLogin()
        } else {
            // Nếu đã đăng nhập, gọi callback để điều hướng tới Home (màn hình chính)
            onNavigateToHome()
        }
    }

    // Giao diện của SplashScreen không thay đổi
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFAEE)), // màu nền kem
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_petpal), // thêm logo vào drawable
            contentDescription = "PetPal Logo",
            modifier = Modifier.size(600.dp)
        )
    }
}