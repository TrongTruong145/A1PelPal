package com.example.petpal.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.util.Log
import java.util.Locale // ✅ THÊM DÒNG NÀY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petpal.domain.model.PetRemote
import com.example.petpal.domain.model.PetWithAddress
import com.example.petpal.domain.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val repository: PetRepository,
    private val application: Application // ✅ 1. Inject Application để dùng Geocoder
) : ViewModel() {
    // ✅ 2. Thay đổi StateFlow để chứa danh sách PetWithAddress
    private val _lostPets = MutableStateFlow<List<PetWithAddress>>(emptyList())
    val lostPets: StateFlow<List<PetWithAddress>> = _lostPets

    private val _foundPets = MutableStateFlow<List<PetWithAddress>>(emptyList())
    val foundPets: StateFlow<List<PetWithAddress>> = _foundPets

    private val geocoder by lazy { Geocoder(application, Locale.getDefault()) }


    fun loadAllPets() {
        viewModelScope.launch {
            // Lấy danh sách thô từ repository
            val allPets = repository.getAllPets()
            val lostPetList = allPets.filter { it.petName.isNotEmpty() } // Tạm lọc, bạn có thể cần trường status
            val foundPetList = allPets.filter { it.petName.isEmpty() } // Giả sử petName trống là found pet

            // Cập nhật UI ngay lập tức với trạng thái "Đang tải địa chỉ..."
            _lostPets.value = lostPetList.map { PetWithAddress(pet = it) }
            _foundPets.value = foundPetList.map { PetWithAddress(pet = it) }

            // ✅ 3. Bắt đầu quá trình tìm địa chỉ cho từng pet
            geocodePetList(lostPetList) { updatedList -> _lostPets.value = updatedList }
            geocodePetList(foundPetList) { updatedList -> _foundPets.value = updatedList }
        }
    }

    // ✅ 4. Hàm mới để xử lý việc tìm địa chỉ cho cả danh sách
    private fun geocodePetList(pets: List<PetRemote>, onUpdate: (List<PetWithAddress>) -> Unit) {
        val petsWithAddress = pets.map { PetWithAddress(pet = it) }
        onUpdate(petsWithAddress)

        viewModelScope.launch(Dispatchers.IO) {
            val updatedList = petsWithAddress.map { petWithAddr ->
                if (petWithAddr.pet.latitude != 0.0 && petWithAddr.pet.longitude != 0.0) {
                    try {
                        val address = getAddressFromCoordinates(petWithAddr.pet.latitude, petWithAddr.pet.longitude)
                        petWithAddr.copy(address = address)
                    } catch (e: Exception) {
                        petWithAddr.copy(address = "Không rõ vị trí")
                    }
                } else {
                    petWithAddr.copy(address = "Không có vị trí")
                }
            }
            onUpdate(updatedList)
        }
    }

    // ✅ 5. Tách logic lấy địa chỉ ra một hàm riêng
    private fun getAddressFromCoordinates(lat: Double, lon: Double): String {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                var address: Address? = null
                geocoder.getFromLocation(lat, lon, 1) { addresses ->
                    address = addresses.firstOrNull()
                }
                // Chờ một chút để callback bất đồng bộ có thể hoàn thành (cách làm đơn giản)
                Thread.sleep(300)
                return formatAddress(address)
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                return formatAddress(addresses?.firstOrNull())
            }
        } catch (e: IOException) {
            Log.e("PetViewModel", "Geocoding failed", e)
            return "Lỗi mạng"
        }
    }

    private fun formatAddress(address: Address?): String {
        if (address == null) return "Không tìm thấy địa chỉ"
        // Lấy tên đường, quận, thành phố cho ngắn gọn
        return listOfNotNull(address.thoroughfare, address.subAdminArea, address.adminArea)
            .joinToString(", ")
            .ifEmpty { address.getAddressLine(0) ?: "Địa chỉ không rõ" }
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