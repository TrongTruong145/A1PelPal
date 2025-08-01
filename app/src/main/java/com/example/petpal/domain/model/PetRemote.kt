package com.example.petpal.domain.model

import com.google.firebase.Timestamp

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
    val timestamp: Timestamp? = null
)