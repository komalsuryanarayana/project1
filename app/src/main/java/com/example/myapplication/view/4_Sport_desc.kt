package com.example.myapplication.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.Model.SportDescription
import com.example.myapplication.R
import com.example.myapplication.ViewModel.OutScheduleViewModel
import com.example.myapplication.repo.SlotRepository
import com.example.myapplication.ui.theme.KhelomoreLightOrange
import com.example.myapplication.ui.theme.KhelomoreOrange
import kotlinx.coroutines.launch


// --- SCREEN 3: SPORT DESCRIPTION ---
val sportDetailsMap = mapOf(
    "Foosball" to SportDescription(
        name = "Foosball",
        imageRes = R.drawable.foosball,
        squadSize = "2 or 4 Players",
        description = "Foosball is a fast-paced tabletop game that simulates real football using miniature figures mounted on rotating rods. It is typically played by two or four players, with each side controlling defenders, midfielders, and strikers to score goals.",
        facilities = listOf("Professional Table", "Ergonomic Grips", "Score Counter", "Well Lit Area"),
        rules = listOf("No spinning the rods", "First to 10 goals wins", "Serve after a goal", "Be respectful to opponents")
    ),
    "Table Tennis" to SportDescription(
        name = "Table Tennis",
        imageRes = R.drawable.tabletenis,
        squadSize = "2 or 4 Players",
        description = "Table tennis is a fast and dynamic indoor sport played with lightweight paddles and a small plastic ball on a divided table. It is usually contested by two or four players, known as singles or doubles.",
        facilities = listOf("Standard Size Table", "Quality Paddles", "High-bounce Balls", "Sufficient Playing Space"),
        rules = listOf("Games are to 11 points", "Alternate serves every 2 points", "Ball must rest on open palm for serve", "No touching the table with free hand")
    ),
    "Carrom" to SportDescription(
        name = "Carrom",
        imageRes = R.drawable.carrom,
        squadSize = "2 or 4 Players",
        description = "Carrom is a popular indoor board game played on a smooth wooden board using small disks called carrom men. It is typically played by two or four players, competing individually or in teams.",
        facilities = listOf("Smooth Wooden Board", "Standard Striker", "Carrom Powder", "Comfortable Seating"),
        rules = listOf("Sinking the Queen requires a cover", "No touching the diagonal lines", "Foul results in a piece return", "Double/Triple shots allowed")
    ),
    "8 Ball Pool" to SportDescription(
        name = "8 Ball Pool",
        imageRes = R.drawable.pool,
        squadSize = "2 Players",
        description = "Pool is a cue-sport played on a felt-covered table with six pockets, using a cue stick to strike a white cue ball and pocket colored object balls.",
        facilities = listOf("7ft Pool Table", "Graphite Cues", "Complete Ball Set", "Chalk Provided"),
        rules = listOf("Call your shots", "Legal break requires 4 balls to hit cushions", "Potting the 8-ball early is a loss", "Scratch on 8-ball is a loss")
    ),
    "Chess" to SportDescription(
        name = "Chess",
        imageRes = R.drawable.chess,
        squadSize = "2 Players",
        description = "Chess is a classic strategic board game played on an 8×8 checkered board with 16 pieces per side. It revolves around planning, tactics, and foresight.",
        facilities = listOf("Weighted Pieces", "Tournament Board", "Chess Clock", "Quiet Environment"),
        rules = listOf("Touch-move rule applies", "Castle only if King/Rook haven't moved", "En passant is a valid capture", "Respect the silence")
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportDetailScreen(navController: NavHostController, sportName: String) {

    var vm : OutScheduleViewModel = viewModel()
    val sport = sportDetailsMap[sportName] ?: sportDetailsMap["8 Ball Pool"]!!
    val repo = remember { SlotRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val ratingInfo by repo.streamSportRating(sportName).collectAsState(initial = 0.0 to 0)


    if (vm.showRatingDialog.value) {
        RatingDialog(
            onDismiss = { vm.showRatingDialog.value = false },
            onSubmit = { rating ->
                scope.launch {
                    val success = repo.submitRating(sportName, rating)
                    if (success) {
                        Toast.makeText(context, "Thank you for rating!", Toast.LENGTH_SHORT).show()
                    }
                    vm.showRatingDialog.value = false
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { vm.showRatingDialog.value = true }) {
                        Icon(Icons.Default.StarBorder, contentDescription = "Rate", tint = KhelomoreOrange)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Button(
                    onClick = { navController.navigate("slot_booking/$sportName") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KhelomoreOrange),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("BOOK NOW", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
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
                .background(Color(0xFFFBFBFB))
        ) {
            // Image Section with Gradient Overlay
            Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                Image(
                    painter = painterResource(id = sport.imageRes),
                    contentDescription = sport.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                startY = 400f
                            )
                        )
                )
                
                Column(
                    modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)
                ) {
                    Surface(color = KhelomoreOrange, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            sport.name.uppercase(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Info Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(sport.name, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                            Text(sport.squadSize, color = Color.Gray, fontSize = 14.sp)
                        }
                        
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    String.format("%.1f", ratingInfo.first),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(" (${ratingInfo.second})", color = Color.Gray.copy(alpha = 0.7f), fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF5F5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Mensa Campus, EL 223, Mahape",
                            color = Color.DarkGray,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text("About Sport", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        sport.description,
                        color = Color.Gray,
                        lineHeight = 22.sp,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text("Amenities", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    sport.facilities.forEach { facility ->
                        Row(modifier = Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF008542), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(facility, fontSize = 15.sp, color = Color.DarkGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text("Rules", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    sport.rules.forEach { rule ->
                        Text("• $rule", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun RatingDialog(onDismiss: () -> Unit, onSubmit: (Int) -> Unit) {

    var vm : OutScheduleViewModel = viewModel()

    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rate your experience", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("How was your game session?")
                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.Center) {
                    (1..5).forEach { index ->
                        IconButton(onClick = { vm.selectedRating.value = index }) {
                            Icon(
                                imageVector = if (index <= vm.selectedRating.value) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (index <= vm.selectedRating.value) KhelomoreOrange else Color.LightGray,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (vm.selectedRating.value > 0) onSubmit(vm.selectedRating.value) },
                enabled = vm.selectedRating.value > 0,
                colors = ButtonDefaults.buttonColors(containerColor = KhelomoreOrange)
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
