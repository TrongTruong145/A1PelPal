package com.example.petpal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petpal.domain.model.PetRemote
import com.example.petpal.domain.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetDetailViewModel @Inject constructor(
    private val petRepository: PetRepository
) : ViewModel() {

    // ✅ THAY ĐỔI: Sử dụng PetRemote thay vì Pet
    private val _petState = MutableStateFlow<PetRemote?>(null)
    val petState: StateFlow<PetRemote?> = _petState

    fun getPetDetails(petId: String) {
        viewModelScope.launch {
            // Không cần thay đổi ở đây, vì hàm trong repo đã được sửa
            val result = petRepository.getPetById(petId)
            _petState.value = result
        }
    }
}