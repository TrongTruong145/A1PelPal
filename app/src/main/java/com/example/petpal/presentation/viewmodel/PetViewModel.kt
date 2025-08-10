package com.example.petpal.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petpal.domain.model.PetRemote
import com.example.petpal.domain.model.PetWithAddress
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
class PetViewModel @Inject constructor(
    private val repository: PetRepository,
    private val application: Application // Inject Application to use Geocoder
) : ViewModel() {

    // Change StateFlow to hold a list of PetWithAddress
    private val _lostPets = MutableStateFlow<List<PetWithAddress>>(emptyList())
    val lostPets: StateFlow<List<PetWithAddress>> = _lostPets

    private val _foundPets = MutableStateFlow<List<PetWithAddress>>(emptyList())
    val foundPets: StateFlow<List<PetWithAddress>> = _foundPets

    private val geocoder by lazy { Geocoder(application, Locale.getDefault()) }

    fun loadAllPets() {
        viewModelScope.launch {
            val allPets = repository.getAllPets()

            // Classify accurately based on the 'status' field
            val lostPetList = allPets.filter { it.status == "LOST" }
            val foundPetList = allPets.filter { it.status == "FOUND" }

            // Update UI immediately with the "Loading address..." state
            _lostPets.value = lostPetList.map { PetWithAddress(pet = it) }
            _foundPets.value = foundPetList.map { PetWithAddress(pet = it) }

            // Start resolving addresses for each list
            geocodePetList(lostPetList) { updatedList -> _lostPets.value = updatedList }
            geocodePetList(foundPetList) { updatedList -> _foundPets.value = updatedList }
        }
    }

    // New function to resolve addresses for an entire list
    private fun geocodePetList(pets: List<PetRemote>, onUpdate: (List<PetWithAddress>) -> Unit) {
        val petsWithAddress = pets.map { PetWithAddress(pet = it) }
        onUpdate(petsWithAddress)

        viewModelScope.launch(Dispatchers.IO) {
            val updatedList = petsWithAddress.map { petWithAddr ->
                if (petWithAddr.pet.latitude != 0.0 && petWithAddr.pet.longitude != 0.0) {
                    try {
                        val address = getAddressFromCoordinates(petWithAddr.pet.latitude, petWithAddr.pet.longitude)
                        petWithAddr.copy(address = address)
                    } catch (e: Exception) {
                        petWithAddr.copy(address = "Unknown location")
                    }
                } else {
                    petWithAddr.copy(address = "No location")
                }
            }
            onUpdate(updatedList)
        }
    }

    // Extract logic for getting address into a separate function
    private fun getAddressFromCoordinates(lat: Double, lon: Double): String {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                var address: Address? = null
                geocoder.getFromLocation(lat, lon, 1) { addresses ->
                    address = addresses.firstOrNull()
                }
                // Wait briefly so the async callback can complete (simple approach)
                Thread.sleep(300)
                return formatAddress(address)
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                return formatAddress(addresses?.firstOrNull())
            }
        } catch (e: IOException) {
            Log.e("PetViewModel", "Geocoding failed", e)
            return "Network error"
        }
    }

    private fun formatAddress(address: Address?): String {
        if (address == null) return "Address not found"
        // Keep it short: street, district, city when possible
        return listOfNotNull(address.thoroughfare, address.subAdminArea, address.adminArea)
            .joinToString(", ")
            .ifEmpty { address.getAddressLine(0) ?: "Unknown address" }
    }

    fun reportLostPet(
        context: Context, // Add context parameter
        pet: PetRemote,
        imageUris: List<Uri>,
        onDone: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            repository.addLostPet(pet, onSuccess = { documentReference ->
                val newDocId = documentReference.id
                repository.uploadImages(context, imageUris, onSuccess = { imageUrls ->
                    repository.updatePetImageUrls("lost_pets", newDocId, imageUrls, onSuccess = {
                        loadAllPets()
                        onDone()
                    }, onFailure = onError)
                }, onFailure = onError)
            }, onFailure = onError)
        }
    }

    fun reportFoundPet(
        context: Context, // Add context parameter
        pet: PetRemote,
        imageUris: List<Uri>,
        onDone: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            repository.addFoundPet(pet, onSuccess = { documentReference ->
                val newDocId = documentReference.id
                repository.uploadImages(context, imageUris, onSuccess = { imageUrls ->
                    repository.updatePetImageUrls("found_pets", newDocId, imageUrls, onSuccess = {
                        loadAllPets()
                        onDone()
                    }, onFailure = onError)
                }, onFailure = onError)
            }, onFailure = onError)
        }
    }

    fun refresh(onDone: () -> Unit = {}) {
        loadAllPets()
        onDone()
    }
}