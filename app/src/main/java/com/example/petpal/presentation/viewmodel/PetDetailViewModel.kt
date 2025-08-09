package com.example.petpal.presentation.viewmodel

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petpal.domain.model.PetRemote
import com.example.petpal.domain.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class PetDetailViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val application: Application // ✅ 1. Inject Application context
) : ViewModel() {

    // ✅ THAY ĐỔI: Sử dụng PetRemote thay vì Pet
    private val _petState = MutableStateFlow<PetRemote?>(null)
    val petState: StateFlow<PetRemote?> = _petState

    // ✅ 2. Tạo StateFlow mới để chứa địa chỉ đã được chuyển đổi
    private val _addressState = MutableStateFlow("Đang tìm địa chỉ...")
    val addressState: StateFlow<String> = _addressState

    fun getPetDetails(petId: String) {
        viewModelScope.launch {
            val result = petRepository.getPetById(petId)
            _petState.value = result

            // ✅ SỬA LỖI LOGIC Ở ĐÂY: Gọi hàm reverseGeocodeLocation sau khi có kết quả
            result?.let { pet ->
                if (pet.latitude != 0.0 && pet.longitude != 0.0) {
                    reverseGeocodeLocation(pet.latitude, pet.longitude)
                } else {
                    _addressState.value = "Không có thông tin vị trí"
                }
            }
        }
    }

    // ✅ 3. Thêm hàm mới để thực hiện Reverse Geocoding
    private fun reverseGeocodeLocation(lat: Double, lon: Double) {
        // Chạy trên một luồng riêng để không block UI
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(application, Locale.getDefault())
                // API mới cho Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(lat, lon, 1) { addresses ->
                        _addressState.value = formatAddress(addresses.firstOrNull())
                    }
                } else {
                    // API cũ hơn cho các phiên bản Android trước
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    _addressState.value = formatAddress(addresses?.firstOrNull())
                }
            } catch (e: IOException) {
                // Lỗi khi không có kết nối mạng hoặc lỗi dịch vụ
                _addressState.value = "Không thể tìm thấy địa chỉ"
            }
        }
    }

    // ✅ 4. Hàm helper để định dạng địa chỉ cho đẹp
    private fun formatAddress(address: Address?): String {
        if (address == null) {
            return "Không tìm thấy địa chỉ cụ thể"
        }
        // Ghép các thành phần của địa chỉ lại với nhau
        return listOfNotNull(
            address.getAddressLine(0) // Lấy dòng địa chỉ đầy đủ nhất
        ).joinToString(", ")
    }
}