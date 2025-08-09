package com.example.petpal.presentation.ui



import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.petpal.domain.model.PetRemote
import com.example.petpal.presentation.viewmodel.PetViewModel


@Composable
fun ReportFoundPetScreen(
    navController: NavHostController,
    initialLocation: String?,
    viewModel: PetViewModel = hiltViewModel() // ✅ SỬA Ở ĐÂY
) {
    val context = LocalContext.current // ✅ Lấy context ở đây


    var petName by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var features by remember { mutableStateOf("") }
    var personality by remember { mutableStateOf("") }
    var circumstances by remember { mutableStateOf("") }
    var accessories by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") } // Dùng để hiển thị trên UI
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // ✅ 2. Tạo state để lưu tọa độ đã chọn
    var selectedLatitude by remember { mutableStateOf(0.0) }
    var selectedLongitude by remember { mutableStateOf(0.0) }

    // ✅ 3. Dùng LaunchedEffect để xử lý dữ liệu vị trí nhận về từ bản đồ
    LaunchedEffect(initialLocation) {
        if (!initialLocation.isNullOrBlank()) {
            locationText = initialLocation // Hiển thị tọa độ trên TextField
            val parts = initialLocation.split(",")
            if (parts.size == 2) {
                selectedLatitude = parts[0].toDoubleOrNull() ?: 0.0
                selectedLongitude = parts[1].toDoubleOrNull() ?: 0.0
            }
        }
    }

    // ✅ Thêm image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedImages = uris.take(5)
    }


    Column(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(1f)  // ✅ Quan trọng: để phần này không chiếm hết chiều cao
                .padding(2.dp)
                .verticalScroll(scrollState) // thêm dòng này để cuộn
                .background(Color(0xFFFFFAEE)),
            verticalArrangement = Arrangement.Top
        ) {

            // Nút back
            IconButton(
                onClick = { navController.navigate("main") },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFE9B5), shape = RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🐶 Report Found Pet",
                    fontSize = 32.sp,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF6A3000)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))



            StyledTextField(
                value = petName,
                onValueChange = { petName = it },
                label = "Name",
            )

            StyledTextField(
                value = breed,
                onValueChange = { breed = it },
                label = "Breed and Size (Large Labrador Dog)"
            )



            StyledTextField(
                value = color,
                onValueChange = { color = it },
                label = "Color(s) and Markings (All white with a black spot on head)"
            )


            // 🧬 Features
            SectionTitle("🧬 Identifying Features")


            StyledTextField(
                value = features,
                onValueChange = { features = it },
                label =  "Physical Features (Tail has been clipped)"
            )

            Spacer(modifier = Modifier.height(5.dp))
            // TextField mô tả
            StyledTextField(
                value = personality,
                onValueChange = { personality = it },
                label = "Personality (Intimidating - will bark strangers)"
            )



            // 📍 Last Seen Info
            SectionTitle("📍 Last Seen Info")


            StyledTextField(
                value = circumstances,
                onValueChange = { circumstances = it },
                label = "Last Known Circumstances (Chasing mouse at the park)"
            )

            StyledTextField(
                value = accessories,
                onValueChange = { accessories = it },
                label = "Identifying Accessories (Red Collar)"
            )



            OutlinedTextField(
                value = locationText,
                onValueChange = { },
                label = { Text("Found Location") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { navController.navigate("map_selector") }) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Select location on map",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )


            // 📞 Contact Info
            SectionTitle("📞 Contact Info")

            // TextField mô tả
            StyledTextField(
                value = contact,
                onValueChange = { contact = it },
                label ="Owner Contact"
            )

            // LazyRow hiển thị ảnh đã chọn
            LazyRow(
                modifier = Modifier.height(100.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedImages.size) { index ->
                    Image(
                        painter = rememberAsyncImagePainter(selectedImages[index]),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Gray),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Nút chọn ảnh (sẽ thêm sau bằng Image Picker)
            // Nút chọn ảnh
            Button(
                onClick = {
                    imagePickerLauncher.launch("image/*") // ✅ Gọi launcher khi click
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Picture (Max 5)",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nút Submit
            Button(
                onClick = {
                    // ✅ 4. Cập nhật đối tượng PetRemote với đầy đủ tọa độ
                    val newPet = PetRemote(
                        petName = petName,
                        breed = breed,
                        color = color,
                        features = features,
                        personality = personality,
                        circumstances = circumstances,
                        accessories = accessories,
                        contact = contact,
                        location = locationText,      // Lưu tọa độ dạng chuỗi
                        latitude = selectedLatitude,   // Lưu vĩ độ
                        longitude = selectedLongitude,  // Lưu kinh độ
                        status = "FOUND" // ✅ THÊM DÒNG NÀY

                    )

                    // ✅ Sửa đổi: truyền context và imageUris vào ViewModel
                    viewModel.reportFoundPet(
                        context = context, // ✅ Truyền context
                        pet = newPet,
                        imageUris = selectedImages, // ✅ Truyền danh sách ảnh đã chọn
                        onDone = {
                            showDialog = true
                        },
                        onError = {
                            Log.e("ReportFoundPet", "Error submitting pet", it)
                        }
                    )

                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDA600))
            ) {
                Text("📤 Submit", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

        }


    }
    // Dialog xác nhận
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }) {
                    Text("OK")
                }
            },
            title = { Text("Report Submitted!") },
            text = { Text("Thank you for you're help") }
        )
    }

}

