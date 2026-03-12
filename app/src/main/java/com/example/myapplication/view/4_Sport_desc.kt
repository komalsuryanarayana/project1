package com.example.myapplication.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.Model.SportDescription
import com.example.myapplication.R
import com.example.myapplication.ui.theme.KhelomoreGray
import com.example.myapplication.ui.theme.KhelomoreOrange


// --- SCREEN 3: SPORT DESCRIPTION ---
val sportDetailsMap = mapOf(
    "Foosball" to SportDescription(
        name = "Foosball",
        imageRes = R.drawable.foosball,
        squadSize = "2 or 4 Players",
        description = "Foosball is a fast‑paced tabletop game that simulates real football using miniature figures mounted on rotating rods. It is typically played by two or four players, with each side controlling defenders, midfielders, and strikers to score goals. The objective is to maneuver the rods skillfully to pass, block, and shoot the ball into the opponent’s goal.",
        facilities = listOf("Professional Table", "Ergonomic Grips", "Score Counter", "Well Lit Area"),
        rules = listOf("No spinning the rods", "First to 10 goals wins", "Serve after a goal", "Be respectful to opponents")
    ),
    "Table Tennis" to SportDescription(
        name = "Table Tennis",
        imageRes = R.drawable.tabletenis,
        squadSize = "2 or 4 Players",
        description = "Table tennis is a fast and dynamic indoor sport played with lightweight paddles and a small plastic ball on a divided table. It is usually contested by two or four players, known as singles or doubles, who rally the ball back and forth over the net. The aim is to outmaneuver the opponent using spins, smashes, and precise placements to win points.",
        facilities = listOf("Standard Size Table", "Quality Paddles", "High-bounce Balls", "Sufficient Playing Space"),
        rules = listOf("Games are to 11 points", "Alternate serves every 2 points", "Ball must rest on open palm for serve", "No touching the table with free hand")
    ),
    "Carrom" to SportDescription(
        name = "Carrom",
        imageRes = R.drawable.carrom,
        squadSize = "2 or 4 Players",
        description = "Carrom is a popular indoor board game played on a smooth wooden board using small disks called carrom men. It is typically played by two or four players, competing individually or in teams to pocket their assigned pieces. Players use a striker disk, flicked with the fingers, to skillfully hit and pocket their carrom men into the board’s corner pockets.",
        facilities = listOf("Smooth Wooden Board", "Standard Striker", "Carrom Powder", "Comfortable Seating"),
        rules = listOf("Sinking the Queen requires a cover", "No touching the diagonal lines", "Foul results in a piece return", "Double/Triple shots allowed")
    ),
    "8 Ball Pool" to SportDescription(
        name = "8 Ball Pool",
        imageRes = R.drawable.pool,
        squadSize = "2 Players",
        description = "Pool is a cue‑sport played on a felt‑covered table with six pockets, using a cue stick to strike a white cue ball and pocket colored object balls. It is commonly played by two players or in teams of two vs. two, depending on the game format. Popular variations include 8‑ball, 9‑ball, and 10‑ball, each with its own rules for ball order and strategy.",
        facilities = listOf("7ft Pool Table", "Graphite Cues", "Complete Ball Set", "Chalk Provided"),
        rules = listOf("Call your shots", "Legal break requires 4 balls to hit cushions", "Potting the 8-ball early is a loss", "Scratch on 8-ball is a loss")
    ),
    "Chess" to SportDescription(
        name = "Chess",
        imageRes = R.drawable.chess,
        squadSize = "2 Players",
        description = "Chess is a classic strategic board game played on an 8×8 checkered board with 16 pieces per side. It is played by two players, each controlling either the white or black pieces and taking alternate turns. The game revolves around planning, tactics, and foresight as players move their pieces to control space, attack, defend, and gain positional advantages. Every piece—king, queen, rooks, bishops, knights, and pawns—has unique movement patterns that shape the flow of play. The objective is to checkmate the opponent’s king by placing it in an inescapable threat.",
        facilities = listOf("Weighted Pieces", "Tournament Board", "Chess Clock", "Quiet Environment"),
        rules = listOf("Touch-move rule applies", "Castle only if King/Rook haven't moved", "En passant is a valid capture", "Respect the silence")
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportDetailScreen(navController: NavHostController, sportName: String) {
    val sport = sportDetailsMap[sportName] ?: sportDetailsMap["8 Ball Pool"]!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Venue Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Button(
                    onClick = { navController.navigate("slot_booking/$sportName") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008542)), // Dark Green
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "SELECT A SLOT TO PROCEED",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp).align(Alignment.CenterEnd)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            // Image Section
            Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                Image(
                    painter = painterResource(id = sport.imageRes),
                    contentDescription = sport.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Text(
                        "1 of 1",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Title and Info Card
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = sport.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Squad Size: ${sport.squadSize}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Mensa Campus, EL 223, TTC Industrial Area, Mahape, 400710",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF008542),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("5", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2 ratings",
                            color = Color(0xFFFF6B00),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // About Section
            SectionHeader("About this sport")
            Text(
                text = sport.description,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.DarkGray,
                lineHeight = 18.sp,
                fontSize = 13.sp
            )

            // Amenities Section
            SectionHeader("Amenities")
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                sport.facilities.forEach { facility ->
                    AmenityRow(facility)
                }
            }

            // Rules Section
            SectionHeader("Venue rules")
            Column(modifier = Modifier.padding(16.dp)) {
                sport.rules.forEach { rule ->
                    Text(
                        text = "• $rule",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 14.sp,
        color = Color.Black
    )
}

@Composable
fun AmenityRow(facility: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF008542),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = facility, fontSize = 13.sp, color = Color.DarkGray)
    }
}
