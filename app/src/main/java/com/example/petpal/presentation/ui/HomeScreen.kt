package com.example.petpal.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.petpal.R

// Dữ liệu pet
data class Pet(
    val id: String,
    val imageResId: Int,
    val type: String,
    val description: String
)

// Dữ liệu mẫu với ảnh drawable
val lostPets = listOf(
    Pet("1", R.drawable.dog1, "Dog 1", "Lông nâu, đeo vòng"),
    Pet("2", R.drawable.dog2, "Dog 2", "Tai cụp, mắt tròn"),
    Pet("3", R.drawable.dog3, "Dog 3", "Lông vàng, nhỏ nhắn")
)

val foundPets = listOf(
    Pet("4", R.drawable.dog4, "Dog 4", "Tìm thấy ở công viên"),
    Pet("5", R.drawable.dog5, "Dog 5", "Đang đi lang thang gần siêu thị")
)

@Composable
fun HomeScreen(navController: NavController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

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

        Spacer(modifier = Modifier.height(8.dp))

        // Danh sách thú cưng bị mất
        Text("Thú cưng bị mất", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            lostPets.forEach { pet ->
                PetCard(pet)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Danh sách thú cưng được tìm thấy
        Text("Thú cưng được tìm thấy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            foundPets.forEach { pet ->
                PetCard(pet)
            }
        }
    }
}

@Composable
fun PetCard(pet: Pet) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .padding(end = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { /* Mở chi tiết nếu muốn */ }
    ) {
        Image(
            painter = painterResource(id = pet.imageResId),
            contentDescription = pet.type,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(pet.type, fontWeight = FontWeight.Bold)
        Text(pet.description, maxLines = 2, style = MaterialTheme.typography.bodySmall)
    }
}
