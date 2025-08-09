package com.example.petpal.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.petpal.presentation.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllPetsMapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    // Lắng nghe danh sách tất cả thú cưng từ ViewModel
    val allPets by viewModel.allPetsState.collectAsState()

    // Vị trí mặc định để camera hướng tới khi mở bản đồ (ví dụ: TP.HCM)
    val defaultLocation = LatLng(10.762622, 106.660172)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 11f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bản đồ Thú cưng") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Lặp qua danh sách thú cưng và tạo Marker cho mỗi con
                allPets.forEach { pet ->
                    // Chỉ hiển thị marker nếu pet có tọa độ hợp lệ
                    if (pet.latitude != 0.0 && pet.longitude != 0.0) {
                        Marker(
                            state = MarkerState(position = LatLng(pet.latitude, pet.longitude)),
                            title = pet.petName,
                            snippet = "Nhấn để xem chi tiết", // Dòng chữ nhỏ hiện ra khi nhấn vào marker
                            onInfoWindowClick = {
                                // Khi người dùng nhấn vào ô thông tin, điều hướng đến màn hình chi tiết
                                navController.navigate("pet_detail/${pet.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}