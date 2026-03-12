package com.example.myapplication.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ViewModel.OutScheduleViewModel
import com.example.myapplication.ui.theme.KhelomoreLightOrange
import com.example.myapplication.ui.theme.KhelomoreOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Categorypage(navController: NavController, username: String) {
    val displayName = if (username.isNotBlank()) {
        username.substringBefore("@").replaceFirstChar { it.uppercase() }
    } else {
        "User"
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Welcome Back,", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                        Text(displayName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(KhelomoreOrange, Color(0xFFFF9F43))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text("Book Your\nRecreation Slot", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 28.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Refresh & Rejuvenate", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                    Icon(
                        Icons.Default.SportsTennis,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp).align(Alignment.CenterEnd).offset(x = 20.dp),
                        tint = Color.White.copy(alpha = 0.2f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Select Category", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CategoryCard(
                    title = "Sports",
                    icon = Icons.Default.SportsBasketball,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("sports_list") }
                )
                CategoryCard(
                    title = "Wellness",
                    icon = Icons.Default.Chair,
                    modifier = Modifier.weight(1f),
                    onClick = {navController.navigate("slot_booking/{Na}")}
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quick Info
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = KhelomoreLightOrange,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = KhelomoreOrange)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "You have 3 bookings available for this week.",
                        color = Color(0xFF633D06),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(title: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(KhelomoreLightOrange),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = KhelomoreOrange, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}
