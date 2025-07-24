package com.example.petpal.domain.usecase


import android.net.Uri
import com.example.petpal.domain.model.Pet
import com.example.petpal.domain.repository.PetRepository

class ReportLostPetUseCase(
    private val repository: PetRepository
) {
    suspend operator fun invoke(pet: Pet, imageUris: List<Uri>): Boolean {
        return repository.reportPet(pet, imageUris)
    }
}
