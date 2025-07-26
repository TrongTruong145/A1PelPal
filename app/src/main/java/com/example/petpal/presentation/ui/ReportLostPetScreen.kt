package com.example.petpal.presentation.ui


import android.R.attr.description
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
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


@Composable
fun ReportLostPetScreen(navController: NavHostController) {
    val context = LocalContext.current

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

            Text(
                text = "🐶 Report Lost Pet Form",
                fontSize = 28.sp,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(5.dp))



            // TextField mô tả
            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                label = { Text(
                    text = "Name",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.labelSmall
                ) },

                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TextField mô tả
            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Breed and Size (Large Labrador Dog)",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))        // TextField mô tả
            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color(s) and Markings (All white with a black spot on head)",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))        // TextField mô tả
            OutlinedTextField(
                value = features,
                onValueChange = { features = it },
                label = { Text("Physical Features (Tail has been clipped)",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            // TextField mô tả
            OutlinedTextField(
                value = personality,
                onValueChange = { personality = it },
                label = { Text("Personality (Intimidating - will bark strangers)",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))        // TextField mô tả
            OutlinedTextField(
                value = circumstances,
                onValueChange = { circumstances = it },
                label = { Text("Last Known Circumstances (Chasing mouse at the park)",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))        // TextField mô tả
            OutlinedTextField(
                value = accessories,
                onValueChange = { accessories = it },
                label = { Text("Identifying Accessories (Red Collar)",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))        // TextField mô tả


            // TextField vị trí
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Last Seen Location",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(16.dp))

            // TextField mô tả
            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text("Owner Contact",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.fillMaxWidth()
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
                    // TODO: Mở image picker
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
                    val db = Firebase.firestore

                    val petData = hashMapOf(
                        "petName" to petName,
                        "breed" to breed,
                        "color" to color,
                        "features" to features,
                        "personality" to personality,
                        "circumstances" to circumstances,
                        "accessories" to accessories,
                        "contact" to contact,
                        "location" to location,
                        "timestamp" to FieldValue.serverTimestamp()
                    )

                    db.collection("lost_pets")
                        .add(petData)
                        .addOnSuccessListener{
                            Log.d("Firestore", "Document successfully added!")
                            showDialog = true
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error adding document", e)
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

