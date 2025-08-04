package com.example.petpal.presentation.ui


import android.R.attr.description
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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.petpal.presentation.viewmodel.PetViewModel
import com.example.petpal.domain.model.PetRemote
import com.example.petpal.uploadImagesToFirebase
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope



@Composable
fun ReportLostPetScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: PetViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()


    var petName by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var features by remember { mutableStateOf("") }
    var personality by remember { mutableStateOf("") }
    var circumstances by remember { mutableStateOf("") }
    var accessories by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedImages = uris.take(5) // chỉ giữ tối đa 5 ảnh
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

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFE9B5), shape = RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🐶 Report Lost Pet",
                    fontSize = 32.sp,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF6A3000)
                )
            }


            Spacer(modifier = Modifier.height(5.dp))



            SectionTitle("📌 Basic Info")

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



            // TextField vị trí
            StyledTextField(
                value = location,
                onValueChange = { location = it },
                label = "Last Seen Location"
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
            Button(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Picture (Max 5)",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.labelSmall)
            }


            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val imageUrls = uploadImagesToFirebase(selectedImages, context)

                            val newPet = PetRemote(
                                petName = petName,
                                breed = breed,
                                color = color,
                                features = features,
                                personality = personality,
                                circumstances = circumstances,
                                accessories = accessories,
                                contact = contact,
                                location = location,
                                imageUrls = imageUrls // 🔥 lưu link ảnh
                            )

                            viewModel.reportLostPet(
                                pet = newPet,
                                onDone = { showDialog = true },
                                onError = { Log.e("ReportLostPet", "Error submitting pet", it) }
                            )

                        } catch (e: Exception) {
                            Log.e("ImageUpload", "Failed to upload images", e)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDA600))
            ) {
                Text("📤 Submit", color = Color.White)
            }
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
            text = { Text("Hope you'll find your pet") }
        )
    }

}

@Composable
fun SectionTitle(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFAD320C),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
    }
}


@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFEDA600),
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
            focusedLabelColor = Color(0xFFAD320C)
        ),
        leadingIcon = leadingIcon
    )
}


