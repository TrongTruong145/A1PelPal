package com.example.petpal.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PetRemote(
    val id: String = "",            // Firestore document ID
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
    @ServerTimestamp val timestamp: Date? = null
)