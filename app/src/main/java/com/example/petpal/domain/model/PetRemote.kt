package com.example.petpal.domain.model

import java.util.Date

data class PetRemote(
    val id: String = "",
    val petName: String = "",
    val breed: String = "",
    val color: String = "",
    val features: String = "",
    val personality: String = "",
    val circumstances: String = "",
    val accessories: String = "",
    val contact: String = "",
    val location: String = "",
    val imageUrls: List<String> = emptyList(),
    val timestamp: Date? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val status: String = "LOST"

)