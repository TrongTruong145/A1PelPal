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
class MapViewModel @Inject constructor(
    private val repository: PetRepository
) : ViewModel() {

    // State private để chứa danh sách pets, chỉ ViewModel này được sửa
    private val _allPetsState = MutableStateFlow<List<PetRemote>>(emptyList())
    // State public để cho UI quan sát, không sửa được từ bên ngoài
    val allPetsState: StateFlow<List<PetRemote>> = _allPetsState

    // Khối init sẽ tự động chạy khi ViewModel được tạo
    init {
        fetchAllPets()
    }

    // Hàm gọi repository để lấy tất cả pets và cập nhật vào state
    private fun fetchAllPets() {
        viewModelScope.launch {
            _allPetsState.value = repository.getAllPets()
        }
    }
}