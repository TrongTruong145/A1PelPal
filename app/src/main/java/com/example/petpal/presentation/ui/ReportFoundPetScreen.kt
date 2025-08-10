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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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


private val Orange = Color(0xFFFF9B19)
private val DeepRed = Color(0xFFC1280F)
private val Cream = Color(0xFFFFF2D7)
private val DarkBrown = Color(0xFF561D03)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFoundPetScreen(
    navController: NavHostController,
    initialLocation: String?,
    viewModel: PetViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var petName by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var features by remember { mutableStateOf("") }
    var personality by remember { mutableStateOf("") }
    var circumstances by remember { mutableStateOf("") }
    var accessories by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }

    // coords from map
    var selectedLatitude by remember { mutableStateOf(0.0) }
    var selectedLongitude by remember { mutableStateOf(0.0) }

    // get "lat,lon" back from MapSelector
    LaunchedEffect(initialLocation) {
        if (!initialLocation.isNullOrBlank()) {
            locationText = initialLocation
            val parts = initialLocation.split(",")
            if (parts.size == 2) {
                selectedLatitude = parts[0].toDoubleOrNull() ?: 0.0
                selectedLongitude = parts[1].toDoubleOrNull() ?: 0.0
            }
        }
    }

    // image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> -> selectedImages = uris.take(5) }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Cream,
        topBar = {
            // Gradient header + transparent TopAppBar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Orange, DeepRed.copy(alpha = 0.88f))
                        )
                    )
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Report Found Pet", color = Color.White, style = MaterialTheme.typography.titleLarge)
                            Text(
                                "Help reunite pets with their families",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("main") }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card container
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 1.dp,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // ——— Section: Basic Info ———
                    SectionTitle("📌 Basic Info", accent = DeepRed)
                    StyledTextField(value = petName, onValueChange = { petName = it }, label = "Name")
                    StyledTextField(value = breed, onValueChange = { breed = it }, label = "Breed and Size (Large Labrador Dog)")
                    StyledTextField(value = color, onValueChange = { color = it }, label = "Color(s) and Markings (All white with a black spot on head)")

                    Spacer(Modifier.height(12.dp))

                    // ——— Section: Identifying Features ———
                    SectionTitle("🧬 Identifying Features", accent = DeepRed)
                    StyledTextField(value = features, onValueChange = { features = it }, label = "Physical Features (Tail has been clipped)")
                    StyledTextField(value = personality, onValueChange = { personality = it }, label = "Personality (May bark at strangers)")

                    Spacer(Modifier.height(12.dp))

                    // ——— Section: Where Found ———
                    SectionTitle("📍 Where Found", accent = DeepRed)
                    StyledTextField(
                        value = circumstances,
                        onValueChange = { circumstances = it },
                        label = "Where / Situation (Found near the park playground)"
                    )
                    StyledTextField(
                        value = accessories,
                        onValueChange = { accessories = it },
                        label = "Identifying Accessories (Red collar)"
                    )

                    OutlinedTextField(
                        value = locationText,
                        onValueChange = {},
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
                                    tint = Orange
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    // ——— Section: Photos ———
                    SectionTitle("📷 Photos", accent = DeepRed)
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
                                    .background(Color(0xFFEDEDED), RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Orange)
                    ) {
                        Text("Select Photos (Max 5)", color = Color.White)
                    }

                    Spacer(Modifier.height(16.dp))

                    // ——— Section: Contact ———
                    SectionTitle("📞 Contact Info", accent = DeepRed)
                    StyledTextField(value = contact, onValueChange = { contact = it }, label = "Your Contact")

                    Spacer(Modifier.height(16.dp))

                    // Submit
                    Button(
                        onClick = {
                            val newPet = PetRemote(
                                petName = petName,
                                breed = breed,
                                color = color,
                                features = features,
                                personality = personality,
                                circumstances = circumstances,
                                accessories = accessories,
                                contact = contact,
                                location = locationText,
                                latitude = selectedLatitude,
                                longitude = selectedLongitude,
                                status = "FOUND"
                            )
                            viewModel.reportFoundPet(
                                context = context,
                                pet = newPet,
                                imageUris = selectedImages,
                                onDone = { showDialog = true },
                                onError = { Log.e("ReportFoundPet", "Error submitting pet", it) }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepRed)
                    ) {
                        Text("📤 Submit", color = Color.White)
                    }
                }
            }
        }
    }

    // Confirmation dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }) { Text("OK") }
            },
            title = { Text("Report Submitted!") },
            text = { Text("Thank you for your help") }
        )
    }
}
@Composable
private fun SectionTitle(title: String, accent: Color = DeepRed) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleMedium,
            color = accent,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Divider(color = Color.Gray.copy(alpha = 0.25f), thickness = 1.dp)
    }
}

@Composable
private fun StyledTextField(
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
            focusedBorderColor = Orange,
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
            focusedLabelColor = DeepRed
        ),
        leadingIcon = leadingIcon
    )
}
