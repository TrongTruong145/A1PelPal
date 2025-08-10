package com.example.petpal.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.petpal.domain.model.PetWithAddress
import com.example.petpal.presentation.viewmodel.PetViewModel


private val Orange = Color(0xFFFF9B19)
private val DeepRed = Color(0xFFC1280F)
private val Cream = Color(0xFFFFF2D7)
private val DarkBrown = Color(0xFF561D03)


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: PetViewModel = hiltViewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("ALL") } // ALL | LOST | FOUND
    var showFilter by remember { mutableStateOf(false) }

    val lostPets by viewModel.lostPets.collectAsState()
    val foundPets by viewModel.foundPets.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadAllPets() }

    fun filterPets(pets: List<PetWithAddress>, query: String): List<PetWithAddress> {
        val textFiltered = if (query.isBlank()) pets else {
            val q = query.lowercase()
            pets.filter {
                it.pet.petName.lowercase().contains(q) ||
                        it.pet.breed.lowercase().contains(q) ||
                        it.pet.color.lowercase().contains(q)
            }
        }
        return when (filterType) {
            "LOST" -> textFiltered.filter { it.pet.status == "LOST" }
            "FOUND" -> textFiltered.filter { it.pet.status == "FOUND" }
            else -> textFiltered
        }
    }

    val filteredLost = filterPets(lostPets, searchQuery)
    val filteredFound = filterPets(foundPets, searchQuery)

    Scaffold(
        containerColor = Cream,
        topBar = {
            HomeHeader(
                onBack = { navController.navigate("main") },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sticky search + filter
            stickyHeader {
                Surface(
                    color = Cream,
                    shadowElevation = 4.dp
                ) {
                    SearchFilterBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onFilterClick = { showFilter = true }
                    )
                }
            }

            // Lost pets
            item {
                SectionHeader(
                    title = "Lost Pets",
                    count = filteredLost.size
                )
            }
            item {
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    filteredLost.forEach { p ->
                        PetCardModern(item = p) {
                            if (p.pet.id.isNotBlank()) {
                                navController.navigate("pet_detail/${p.pet.id}")
                            }
                        }
                    }
                }
            }

            // Found pets
            item { Spacer(Modifier.height(8.dp)) }
            item {
                SectionHeader(
                    title = "Found Pets",
                    count = filteredFound.size
                )
            }
            item {
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    filteredFound.forEach { p ->
                        PetCardModern(item = p) {
                            if (p.pet.id.isNotBlank()) {
                                navController.navigate("pet_detail/${p.pet.id}")
                            }
                        }
                    }
                }
            }

            // CTA banners
            item { Spacer(Modifier.height(12.dp)) }
            item {
                CTABanners(
                    onReportLost = { navController.navigate("report_lost") },
                    onReportFound = { navController.navigate("report_found") },
                    onOpenMap = { navController.navigate("all_pets_map")}
                )
            }
            item { Spacer(Modifier.height(32.dp)) }
        }

        // Filter bottom sheet
        FilterBottomSheet(
            visible = showFilter,
            currentType = filterType,
            onTypeChange = { filterType = it },
            onDismiss = { showFilter = false }
        )
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

        // 3) OPEN MAP (new full-width CTA)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("all_pets_map") },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🗺️ Explore Nearby on Map", fontWeight = FontWeight.Bold)
                Text(
                    "See all reported pets around the city.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun CTABanners(
    onReportLost: () -> Unit,
    onReportFound: () -> Unit,
    onOpenMap: () -> Unit // ⬅️ NEW
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        CTABanner(
            title = "Can't find your pet?",
            subtitle = "Report now so the community can help.",
            buttonText = "🐶 Report Lost Pet",
            background = Orange,
            onClick = onReportLost
        )
        CTABanner(
            title = "Just saw a lost pet?",
            subtitle = "Report it to help reunite a family.",
            buttonText = "🐶 Report Found Pet",
            background = Color(0xFF1C98D6),
            onClick = onReportFound
        )
        // ⬇️ NEW: full-width MAP banner
        CTABanner(
            title = "Explore Nearby on Map",
            subtitle = "See all reported pets around the city.",
            buttonText = "Open Map",
            background = DeepRed,
            onClick = onOpenMap,
            leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) } // optional icon
        )
    }
}


@Composable
private fun CTABanner(
    title: String,
    subtitle: String,
    buttonText: String,
    background: Color,
    onClick: () -> Unit,
    leadingIcon: (@Composable (() -> Unit))? = null // ⬅️ NEW (optional)
) {
    Surface(
        color = background,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Box(Modifier.fillMaxSize().padding(16.dp)) {
            Column(Modifier.align(Alignment.CenterStart)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    color = Color.White.copy(alpha = .9f),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(10.dp))
                FilledTonalButton(
                    onClick = onClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.White,
                        contentColor = background
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    if (leadingIcon != null) {
                        leadingIcon()
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(buttonText)
                }
            }
        }
    }
}



@Composable
fun PetCard(petWithAddress: PetWithAddress, navController: NavController) {
    val pet = petWithAddress.pet
    Column(
        modifier = Modifier
            .width(160.dp)
            .padding(end = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
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
            if (pet.imageUrls.isNotEmpty()) {
                AsyncImage(
                    model = pet.imageUrls.first(),
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
        Text("${pet.breed}, ${pet.color}", maxLines = 2, style = MaterialTheme.typography.bodySmall)
        Text("👤 ${pet.contact}", style = MaterialTheme.typography.labelSmall)
        Text("📍 ${petWithAddress.address}", style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeHeader(
    onBack: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Orange, DeepRed.copy(alpha = 0.85f))
                )
            )
    ) {
        LargeTopAppBar(
            navigationIcon = {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            },
            title = {
                Column {
                    Text("Home Screen", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                    Text("Find • Report • Reunite", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
                }
            },
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White
            )
        )
    }
}


@Composable
private fun SearchFilterBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Cream)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            singleLine = true,
            placeholder = { Text("Search") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(28.dp)
        )
        Spacer(Modifier.width(12.dp))
        FilledTonalButton(
            onClick = onFilterClick,
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = "Filter")
            Spacer(Modifier.width(6.dp))
            Text("Filter")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    visible: Boolean,
    currentType: String, // "ALL" | "LOST" | "FOUND"
    onTypeChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(Modifier.padding(20.dp)) {
            Text("Filter by type", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChipX("All", currentType == "ALL") { onTypeChange("ALL") }
                FilterChipX("Lost", currentType == "LOST") { onTypeChange("LOST") }
                FilterChipX("Found", currentType == "FOUND") { onTypeChange("FOUND") }
            }
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                Text("Done")
            }
        }
    }
}

@Composable
private fun FilterChipX(text: String, selected: Boolean, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) Orange.copy(alpha = .2f) else Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) Orange else Color.Gray.copy(alpha = 0.4f)
        )
    )
}


@Composable
private fun SectionHeader(
    title: String,
    count: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "$count",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun PetCardModern(
    item: PetWithAddress,
    onClick: () -> Unit
) {
    val statusColor = when (item.pet.status) {
        "LOST" -> DeepRed
        "FOUND" -> Color(0xFF2E7D32) // green-ish
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .width(180.dp)
            .padding(start = 16.dp, bottom = 12.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box {
            // Image
            if (item.pet.imageUrls.isNotEmpty()) {
                AsyncImage(
                    model = item.pet.imageUrls.first(),
                    contentDescription = item.pet.petName,
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) { Text("🐾", fontSize = MaterialTheme.typography.headlineMedium.fontSize) }
            }

            // Status chip
            Surface(
                color = statusColor,
                shape = RoundedCornerShape(bottomEnd = 12.dp),
                tonalElevation = 2.dp,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = item.pet.status.ifBlank { "UNKNOWN" },
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // Bottom gradient + text
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xAA000000))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(item.pet.petName, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text("${item.pet.breed}, ${item.pet.color}",
                    color = Color.White.copy(alpha = .9f),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
                Text("📍 ${item.address}",
                    color = Color.White.copy(alpha = .9f),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
    }
}

