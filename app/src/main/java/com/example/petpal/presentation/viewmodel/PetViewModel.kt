package com.example.petpal.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petpal.domain.model.PetRemote
import com.example.petpal.domain.repository.PetRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



// --- ViewModel ---
class PetViewModel(private val repository: PetRepository = PetRepository()) : ViewModel() {

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


    fun reportLostPet(pet: PetRemote, onDone: () -> Unit, onError: (Exception) -> Unit) {
        repository.addLostPet(pet, {
            loadAllPets()
            onDone()
        }, onError)
    }

    fun reportFoundPet(pet: PetRemote, onDone: () -> Unit, onError: (Exception) -> Unit) {
        repository.addFoundPet(pet, {
            loadAllPets()
            onDone()
        }, onError)
    }



    fun refresh(onDone: () -> Unit = {}) {
        loadAllPets()
        onDone()
    }
}
