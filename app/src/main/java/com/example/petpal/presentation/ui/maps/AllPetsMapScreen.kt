package com.example.petpal.presentation.ui.maps

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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AllPetsMapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    // Listen to the list of all pets from ViewModel
    val allPets by viewModel.allPetsState.collectAsState()

    // Default location for the camera to point to when opening the map (example: Ho Chi Minh City)
    // ✅ 1. Add location permission handling logic
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // ✅ 2. Create a state to store the user's current location
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // ✅ 3. Use DisposableEffect to listen for location updates and automatically cancel when leaving the screen
    @SuppressLint("MissingPermission")
    DisposableEffect(locationPermissions.allPermissionsGranted) {
        if (!locationPermissions.allPermissionsGranted) {
            // If permission is not granted, do nothing
            return@DisposableEffect onDispose {}
        }

        // Configure request to update location every 5 seconds
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                // Update state with the latest location
                result.lastLocation?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }

        // Start listening for location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        // onDispose will be called when leaving this screen
        onDispose {
            // Stop listening to save battery
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
                title = { Text("Pets Map") },
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
            // If permission is granted, show the map
            if (locationPermissions.allPermissionsGranted) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    // Show pet markers
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
                                snippet = "Tap to view details",
                                icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                                onInfoWindowClick = {
                                    navController.navigate("pet_detail/${pet.id}")
                                }
                            )
                        }
                    }
                    // ✅ 4. Add marker for user's location
                    userLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Your Location",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE) // Blue color
                        )
                    }
                }
            } else {
                // If permission is not granted, show permission request screen
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("PetPal needs location access to display your position.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { locationPermissions.launchMultiplePermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}
