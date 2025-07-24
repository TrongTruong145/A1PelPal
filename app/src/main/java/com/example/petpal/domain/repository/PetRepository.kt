package com.example.petpal.domain.repository

import android.net.Uri
import com.example.petpal.domain.model.Pet

interface PetRepository {
    suspend fun reportPet(pet: Pet, imageUris: List<Uri>): Boolean
    suspend fun getPetsByType(type: String): List<Pet>
}
