package com.example.petpal.domain.model


// Lớp này dùng để kết hợp dữ liệu pet và địa chỉ đã được xử lý cho UI
data class PetWithAddress(
    val pet: PetRemote,
    val address: String = "Đang tải địa chỉ..." // Giá trị mặc định khi đang chờ
)