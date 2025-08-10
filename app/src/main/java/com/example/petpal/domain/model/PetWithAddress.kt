package com.example.petpal.domain.model

// This class is used to combine pet data and processed address for the UI
data class PetWithAddress(
    val pet: PetRemote,
    val address: String = "Loading address..." // Default value while waiting
)
