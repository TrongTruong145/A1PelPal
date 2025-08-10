package com.example.petpal.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.petpal.presentation.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class) // ✅ Sửa Annotation
@Composable
fun AllPetsMapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    // Lắng nghe danh sách tất cả thú cưng từ ViewModel
    val allPets by viewModel.allPetsState.collectAsState()

    // Vị trí mặc định để camera hướng tới khi mở bản đồ (ví dụ: TP.HCM)
    // ✅ 1. Thêm logic xử lý quyền truy cập vị trí
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // ✅ 2. Tạo state để lưu vị trí hiện tại của người dùng
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }





    // ✅ 3. Sử dụng DisposableEffect để lắng nghe vị trí và tự động hủy khi thoát màn hình
    @SuppressLint("MissingPermission")
    DisposableEffect(locationPermissions.allPermissionsGranted) {
        if (!locationPermissions.allPermissionsGranted) {
            // Nếu chưa có quyền thì không làm gì cả
            return@DisposableEffect onDispose {}
        }

        // Cấu hình yêu cầu cập nhật vị trí mỗi 5 giây
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                // Cập nhật state với vị trí mới nhất
                result.lastLocation?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }

        // Bắt đầu lắng nghe
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        // onDispose sẽ được gọi khi thoát khỏi màn hình này
        onDispose {
            // Dừng lắng nghe để tiết kiệm pin
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

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
            // Nếu đã có quyền, hiển thị bản đồ
            if (locationPermissions.allPermissionsGranted) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    // Hiển thị các marker của thú cưng (như cũ)
                    allPets.forEach { pet ->
                        if (pet.latitude != 0.0 && pet.longitude != 0.0) {
                            val markerColor = when (pet.status) {
                                "LOST" -> BitmapDescriptorFactory.HUE_RED
                                "FOUND" -> BitmapDescriptorFactory.HUE_GREEN
                                else -> BitmapDescriptorFactory.HUE_YELLOW
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
                    // ✅ 4. Thêm marker cho vị trí của người dùng
                    userLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Vị trí của bạn",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE) // Màu xanh dương
                        )
                    }
                }
            } else {
                // Nếu chưa có quyền, hiển thị màn hình yêu cầu quyền
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("PetPal cần quyền truy cập vị trí để hiển thị vị trí của bạn.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { locationPermissions.launchMultiplePermissionRequest() }) {
                        Text("Cấp quyền")
                    }
                }
            }
        }
    }
}