package com.example.petpal.domain.model

data class Pet(
    val id: String = "",
    val type: String = "", // "lost" hoặc "found"
    val name: String? = null,
    val description: String = "",
    val breed: String = "",
    val color: String = "",
    val markings: String = "",
    val temperament: String = "",
    val behavior: String = "",
    val accessories: String = "",
    val imageUrls: List<String> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String = ""
)
