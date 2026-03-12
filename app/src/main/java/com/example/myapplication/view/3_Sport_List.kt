package com.example.myapplication.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.Model.SportItem
import com.example.myapplication.repo.SlotRepository
import com.example.myapplication.ui.theme.KhelomoreLightOrange
import com.example.myapplication.ui.theme.KhelomoreOrange

// Enum to manage sorting logic
enum class SlotSortOrder {
    MANY_TO_FEW,
    FEW_TO_MANY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsListScreen(navController: NavHostController) {
    // ---- UI State ----
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var sortOrder by rememberSaveable { mutableStateOf(SlotSortOrder.MANY_TO_FEW) }
    val focusManager = LocalFocusManager.current

    val repo = remember { SlotRepository() }
    val availableCounts by repo.streamAllAvailableCounts().collectAsState(initial = emptyMap())

    val sports = remember(availableCounts) {
        listOf(
            SportItem("Foosball", R.drawable.foosball, "A fast-paced tabletop football game where players control rods.", availableCounts["foosball"] ?: 9),
            SportItem("Table Tennis", R.drawable.tabletenis, "A quick indoor paddle sport with a lightweight ball.", availableCounts["table_tennis"] ?: 9),
            SportItem("Carrom", R.drawable.carrom, "Precision board game where players flick a striker.", availableCounts["carrom"] ?: 9),
            SportItem("8 Ball Pool", R.drawable.pool, "Cue-sport played on a felt table with strategy.", availableCounts["8_ball_pool"] ?: 9),
            SportItem("Chess", R.drawable.chess, "Strategic board game to checkmate the king.", availableCounts["chess"] ?: 9),
        )
    }

    // ---- Filtering and Sorting Logic ----
    val displayList = remember(searchQuery, sortOrder, sports) {
        sports
            .filter { it.name.contains(searchQuery, ignoreCase = true) }
            .let { filtered ->
                if (sortOrder == SlotSortOrder.MANY_TO_FEW) {
                    filtered.sortedByDescending { it.availableSlots }
                } else {
                    filtered.sortedBy { it.availableSlots }
                }
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Choose a Sport", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFE3E5E7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // ---- 1. Search Bar ----
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search for a sport...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = KhelomoreOrange
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
            )

            // ---- 2. Filter/Sort Chips ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Sort by slots:", fontSize = 14.sp, fontWeight = FontWeight.Bold)

                FilterChip(
                    selected = sortOrder == SlotSortOrder.MANY_TO_FEW,
                    onClick = { sortOrder = SlotSortOrder.MANY_TO_FEW },
                    label = { Text("Many → Few") },
                    leadingIcon = if (sortOrder == SlotSortOrder.MANY_TO_FEW) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )

                FilterChip(
                    selected = sortOrder == SlotSortOrder.FEW_TO_MANY,
                    onClick = { sortOrder = SlotSortOrder.FEW_TO_MANY },
                    label = { Text("Few → Many") },
                    leadingIcon = if (sortOrder == SlotSortOrder.FEW_TO_MANY) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }

            // ---- 3. List of Sports ----
            if (displayList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No sports found matching '$searchQuery'", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(displayList) { sport ->
                        SportCard(sport) {
                            navController.navigate("sport_detail/${sport.name}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SportCard(sport: SportItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image Box
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(KhelomoreLightOrange),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = sport.icon),
                    contentDescription = sport.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sport.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Text(
                    text = sport.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Slots Tag
                Surface(
                    color = KhelomoreLightOrange.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${sport.availableSlots} slots available",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = KhelomoreOrange
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}
