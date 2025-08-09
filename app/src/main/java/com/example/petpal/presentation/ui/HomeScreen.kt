package com.example.petpal.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.petpal.domain.model.PetRemote
import com.example.petpal.presentation.viewmodel.PetViewModel
import coil.compose.AsyncImage // ✅ Thêm import này


@Composable
fun HomeScreen(navController: NavController, viewModel: PetViewModel = viewModel()) {
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
                    PetCard(pet)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Found Pets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                filteredFoundPets.forEach { pet ->
                    PetCard(pet)
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
fun PetCard(pet: PetRemote) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .padding(end = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { /* Navigate to detail if needed */ }
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