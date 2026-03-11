package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.ui.theme.KhelomoreLightOrange
import com.example.myapplication.ui.theme.KhelomoreOrange

// Data class to hold sport details
data class SportItem(
    val name: String,
    val icon: Int,
    val description: String,
    val availableSlots: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsListScreen(navController: NavHostController) {
    val sports = listOf(
        SportItem("Foosball", R.drawable.foosball, "A fast-paced tabletop football game where players control rods to score goals.", 12),
        SportItem("Table Tennis", R.drawable.tabletenis, "A quick indoor paddle sport where players rally a lightweight ball across a net.", 8),
        SportItem("Carrom", R.drawable.carrom, "A precision board game where players flick a striker to pocket wooden discs.", 4),
        SportItem("8 Ball Pool", R.drawable.pool, "A cue-sport played on a felt table where players aim to pocket balls using strategy and control.", 6),
        SportItem("Chess", R.drawable.chess, "A strategic board game where two players battle to checkmate each other’s king.", 10),

    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Choose a Sport", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            )
        },
        containerColor = Color(0xFFE3E5E7) // Slight grey background for better card contrast
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sports) { sport ->
                SportCard(sport) {
                    navController.navigate("sport_detail/${sport.name}")
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
            // "Photo" Placeholder / Icon Box
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(KhelomoreLightOrange),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(id = sport.icon),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // In a real app, replace Icon with Image(painter = painterResource(...))
//                Icon(
//                    imageVector = sport.icon,
//                    contentDescription = sport.name,
//                    tint = KhelomoreOrange,
//                    modifier = Modifier.size(48.dp)
//                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = sport.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Text(
                    text = sport.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Available Slots Tag
                Surface(
                    color = KhelomoreLightOrange.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${sport.availableSlots} slots available",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
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