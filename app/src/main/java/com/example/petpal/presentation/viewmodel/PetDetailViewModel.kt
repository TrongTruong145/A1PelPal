package com.example.petpal.presentation.viewmodel

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petpal.domain.model.PetRemote
import com.example.petpal.domain.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class PetDetailViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val application: Application // Inject Application context
) : ViewModel() {

    // Use PetRemote instead of Pet
    private val _petState = MutableStateFlow<PetRemote?>(null)
    val petState: StateFlow<PetRemote?> = _petState

    // Create a StateFlow to hold the reverse-geocoded address
    private val _addressState = MutableStateFlow("Looking up address...")
    val addressState: StateFlow<String> = _addressState

    fun getPetDetails(petId: String) {
        viewModelScope.launch {
            val result = petRepository.getPetById(petId)
            _petState.value = result

            // Fix logic: call reverseGeocodeLocation after we have the result
            result?.let { pet ->
                if (pet.latitude != 0.0 && pet.longitude != 0.0) {
                    reverseGeocodeLocation(pet.latitude, pet.longitude)
                } else {
                    _addressState.value = "No location information"
                }
            }
        }
    }

    // Perform Reverse Geocoding
    private fun reverseGeocodeLocation(lat: Double, lon: Double) {
        // Run on a background thread to avoid blocking the UI
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(application, Locale.getDefault())
                // New API for Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(lat, lon, 1) { addresses ->
                        _addressState.value = formatAddress(addresses.firstOrNull())
                    }
                } else {
                    // Legacy API for older Android versions
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    _addressState.value = formatAddress(addresses?.firstOrNull())
                }
            } catch (e: IOException) {
                // Error when there is no network or geocoding service issue
                _addressState.value = "Unable to find address"
            }
        }
    }

    // Helper to format the address nicely
    private fun formatAddress(address: Address?): String {
        if (address == null) {
            return "No specific address found"
        }
        // Join address components
        return listOfNotNull(
            address.getAddressLine(0) // The most complete address line
        ).joinToString(", ")
    }
}