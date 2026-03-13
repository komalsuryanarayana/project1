package com.example.myapplication.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
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
        containerColor = Color(0xFFFDFDFD)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Welcome Header (Replaces TopAppBar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Hello,", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                    Text(displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                }

            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Modern Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(KhelomoreOrange, Color(0xFFFF8C42))
                            )
                        )
                ) {
                    // Decorative circles
                    Box(modifier = Modifier.size(150.dp).offset(x = 220.dp, y = (-50).dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)))
                    
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "LTM RECREATION",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Ready for a\nGame Break?",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 32.sp
                        )
                    }
                    
                    Icon(
                        Icons.Default.SportsTennis,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 10.dp, y = 10.dp),
                        tint = Color.White.copy(alpha = 0.15f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Explore Categories", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ModernCategoryCard(
                    title = "Sports Arena",
                    subtitle = "Table Tennis, Pool...",
                    icon = Icons.Default.SportsBasketball,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF6C63FF),
                    onClick = { navController.navigate("sports_list") }
                )
                ModernCategoryCard(
                    title = "Wellness Hub",
                    subtitle = "Massage & Relax",
                    icon = Icons.Default.SelfImprovement,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFFF6584),
                    onClick = { navController.navigate("slot_booking/Massage Chair") }
                )
            }
        }
    }
}

@Composable
fun ModernCategoryCard(title: String, subtitle: String, icon: ImageVector, modifier: Modifier, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF1A1A1A))
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
    }
}
