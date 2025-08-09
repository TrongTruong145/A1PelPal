package com.example.petpal.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.petpal.presentation.viewmodel.PetDetailViewModel


// PetDetailScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    petId: String,
    navController: NavController,
    viewModel: PetDetailViewModel = hiltViewModel()
) {
    // Gọi fetch data khi màn hình được tạo lần đầu
    LaunchedEffect(key1 = petId) {
        viewModel.getPetDetails(petId)
    }

    // Lắng nghe state từ ViewModel
    val pet = viewModel.petState.collectAsState().value
    val address by viewModel.addressState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết Thú cưng") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (pet != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    // Hiển thị ảnh
                    if (pet.imageUrls.isNotEmpty()) {
                        AsyncImage(
                            model = pet.imageUrls.first(),
                            contentDescription = pet.petName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Hiển thị thông tin
                    Text(pet.petName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(
                        // Giả sử bạn có trường 'status' trong PetRemote, nếu không, bạn cần thêm nó
                        // hoặc xác định trạng thái dựa trên collection (việc này phức tạp hơn)
                        "Đang thất lạc", // Tạm thời hardcode
                        color = Color.Red,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Các thông tin chi tiết khác
                    InfoRow("Giống:", pet.breed)
                    InfoRow("Màu sắc:", pet.color)
                    InfoRow("Đặc điểm:", pet.features)
                    InfoRow("Tính cách:", pet.personality)
                    InfoRow("Phụ kiện:", pet.accessories)
                    InfoRow("Hoàn cảnh:", pet.circumstances)
                    InfoRow("Vị trí:", address) // Hiển thị địa chỉ đã chuyển đổi
                    InfoRow("Liên hệ:", pet.contact)
                }
            } else {
                // Hiển thị loading indicator khi đang tải
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    if (value.isNotBlank()) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Text(label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Text(value, style = MaterialTheme.typography.bodyLarge)
            Divider(modifier = Modifier.padding(top = 8.dp))
        }
    }
}