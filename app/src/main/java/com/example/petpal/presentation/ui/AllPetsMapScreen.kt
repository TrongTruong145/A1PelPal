package com.example.petpal.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.petpal.presentation.viewmodel.MapViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
                allPets.forEach { pet ->
                    if (pet.latitude != 0.0 && pet.longitude != 0.0) {

                        // ✅ 1. Xác định màu sắc dựa vào status
                        val markerColor = when (pet.status) {
                            "LOST" -> BitmapDescriptorFactory.HUE_RED
                            "FOUND" -> BitmapDescriptorFactory.HUE_GREEN
                            else -> BitmapDescriptorFactory.HUE_YELLOW // Một màu mặc định
                        }

                        Marker(
                            state = MarkerState(position = LatLng(pet.latitude, pet.longitude)),
                            title = pet.petName,
                            snippet = "Nhấn để xem chi tiết",
                            // ✅ 2. Gán màu cho marker
                            icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                            onInfoWindowClick = {
                                navController.navigate("pet_detail/${pet.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}