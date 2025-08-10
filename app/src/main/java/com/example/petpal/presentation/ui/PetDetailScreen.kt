package com.example.petpal.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.petpal.presentation.viewmodel.PetDetailViewModel

// --- Brand colors (remove if you've defined them globally already) ---
private val Orange = Color(0xFFFF9B19)
private val DeepRed = Color(0xFFC1280F)
private val Cream = Color(0xFFFFF2D7)
private val DarkBrown = Color(0xFF561D03)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    petId: String,
    navController: NavController,
    viewModel: PetDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(petId) { viewModel.getPetDetails(petId) }

    val pet = viewModel.petState.collectAsState().value
    val address by viewModel.addressState.collectAsState()
    val clipboard = LocalClipboardManager.current
    val scroll = rememberScrollState()
    var selectedImageIndex by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Cream,
        topBar = {
            // Gradient header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Orange, DeepRed.copy(alpha = 0.88f)))
                    )
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Pet Details", color = Color.White, style = MaterialTheme.typography.titleLarge)
                            Text(
                                "Find • Report • Reunite",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (pet == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(scroll)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- HERO IMAGE + STATUS TAG ---
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box {
                            val imageUrls = pet.imageUrls
                            val mainImage = imageUrls.getOrNull(selectedImageIndex)

                            if (mainImage != null) {
                                AsyncImage(
                                    model = mainImage,
                                    contentDescription = pet.petName,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(280.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(280.dp)
                                        .background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🐾", fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                                }
                            }

                            // Status chip
                            val statusColor = when (pet.status) {
                                "LOST" -> DeepRed
                                "FOUND" -> Color(0xFF2E7D32)
                                else -> Color.Gray
                            }
                            Surface(
                                color = statusColor,
                                shape = RoundedCornerShape(bottomEnd = 12.dp),
                                tonalElevation = 2.dp,
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                Text(
                                    pet.status.ifBlank { "UNKNOWN" },
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                            // Bottom gradient overlay + name
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0xCC000000))
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    pet.petName,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (pet.breed.isNotBlank() || pet.color.isNotBlank()) {
                                    Text(
                                        listOf(pet.breed, pet.color).filter { it.isNotBlank() }.joinToString(", "),
                                        color = Color.White.copy(alpha = .9f),
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // --- THUMBNAILS (if >1) ---
                    if (pet.imageUrls.size > 1) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            pet.imageUrls.forEachIndexed { index, url ->
                                val selected = index == selectedImageIndex
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) Orange else Color.Gray.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedImageIndex = index }
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }

                    // --- INFO CARD ---
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 1.dp,
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            InfoRowPretty("Breed", pet.breed)
                            InfoRowPretty("Color", pet.color)
                            InfoRowPretty("Features", pet.features)
                            InfoRowPretty("Personality", pet.personality)
                            InfoRowPretty("Accessories", pet.accessories)
                            InfoRowPretty("Circumstances", pet.circumstances)
                            InfoRowPretty("Location", address)
                            InfoRowPretty("Contact", pet.contact)
                        }
                    }

                    // --- ACTIONS ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("all_pets_map") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Orange)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Open Map")
                        }
                        OutlinedButton(
                            onClick = {
                                val contact = pet.contact.ifBlank { "No contact provided" }
                                clipboard.setText(AnnotatedString(contact))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.List, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Copy Contact")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRowPretty(label: String, value: String) {
    if (value.isBlank()) return
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp)
    ) {
        Text(label, fontWeight = FontWeight.Bold, color = DarkBrown, style = MaterialTheme.typography.titleSmall)
        Text(value, style = MaterialTheme.typography.bodyLarge)
        Divider(modifier = Modifier.padding(top = 8.dp), color = Color.Gray.copy(alpha = 0.25f))
    }
}
