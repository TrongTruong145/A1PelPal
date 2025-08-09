package com.example.petpal.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.petpal.domain.model.PetRemote
import com.example.petpal.presentation.viewmodel.PetViewModel


@Composable
// ✅ THAY ĐỔI Ở DÒNG NÀY
fun HomeScreen(navController: NavController, viewModel: PetViewModel = hiltViewModel()) {
    var searchQuery by remember { mutableStateOf("") }

    val lostPets by viewModel.lostPets.collectAsState()
    val foundPets by viewModel.foundPets.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllPets()
    }

    fun filterPets(pets: List<PetRemote>, query: String): List<PetRemote> {
        if (query.isBlank()) return pets
        val lowerQuery = query.lowercase()
        return pets.filter {
            it.petName.lowercase().contains(lowerQuery) ||
                    it.breed.lowercase().contains(lowerQuery) ||
                    it.color.lowercase().contains(lowerQuery)
        }
    }

    val filteredLostPets = filterPets(lostPets, searchQuery)
    val filteredFoundPets = filterPets(foundPets, searchQuery)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { navController.navigate("main") },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                label = { Text("Search for a pet...") },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Text("Lost Pets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                filteredLostPets.forEach { pet ->
                    PetCard(pet = pet, navController = navController)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Found Pets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                filteredFoundPets.forEach { pet ->
                    PetCard(pet = pet, navController = navController)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CTASection(navController)
        }

        FloatingActionButton(
            onClick = { navController.navigate("map") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Open map",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CTASection(navController: NavController) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("report_lost") },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("❓ Can't find your pet?", fontWeight = FontWeight.Bold)
                Text("👉 Report it now so others can help!", style = MaterialTheme.typography.bodySmall)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("report_found") },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("👀 Just saw a lost pet?", fontWeight = FontWeight.Bold)
                Text("👉 Report it to help reconnect with its owner!", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun PetCard(pet: PetRemote, navController: NavController) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .padding(end = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                // Điều hướng đến màn hình chi tiết với ID của pet
                // pet.id không được rỗng để đảm bảo route hợp lệ
                if (pet.id.isNotBlank()) {
                    navController.navigate("pet_detail/${pet.id}")
                }
            }
    ) {
        Box(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            // ✅ Sửa đổi: hiển thị hình ảnh nếu có, ngược lại thì hiển thị icon mặc định
            if (pet.imageUrls.isNotEmpty()) {
                AsyncImage(
                    model = pet.imageUrls.first(), // Lấy ảnh đầu tiên trong danh sách
                    contentDescription = "Pet Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("🐾", fontSize = MaterialTheme.typography.headlineMedium.fontSize)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(pet.petName, fontWeight = FontWeight.Bold)
        Text(pet.breed + ", " + pet.color, maxLines = 2, style = MaterialTheme.typography.bodySmall)
        Text("👤 " + pet.contact, style = MaterialTheme.typography.labelSmall)
        Text("📍 " + pet.location, style = MaterialTheme.typography.labelSmall)
    }
}