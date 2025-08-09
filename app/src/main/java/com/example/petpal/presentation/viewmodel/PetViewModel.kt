package com.example.petpal.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petpal.domain.model.PetRemote
import com.example.petpal.domain.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // ✅ THÊM ANNOTATION NÀY
class PetViewModel @Inject constructor( // ✅ THÊM @Inject constructor
    private val repository: PetRepository // ✅ Bỏ phần khởi tạo mặc định
) : ViewModel() {
    private val _lostPets = MutableStateFlow<List<PetRemote>>(emptyList())
    val lostPets: StateFlow<List<PetRemote>> = _lostPets

    private val _foundPets = MutableStateFlow<List<PetRemote>>(emptyList())
    val foundPets: StateFlow<List<PetRemote>> = _foundPets

    fun loadAllPets() {
        viewModelScope.launch {
            repository.fetchLostPets {
                _lostPets.value = it
                println("🔥 LOST PETS LOADED: ${it.size} pets")
            }
            repository.fetchFoundPets {
                _foundPets.value = it
                println("🔥 FOUND PETS LOADED: ${it.size} pets")
            }
        }
    }

    fun reportLostPet(
        context: Context, // Thêm tham số context
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
        context: Context, // Thêm tham số context
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