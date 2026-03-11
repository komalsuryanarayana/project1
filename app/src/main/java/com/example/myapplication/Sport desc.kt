package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.ui.theme.KhelomoreGray
import com.example.myapplication.ui.theme.KhelomoreOrange


// --- SCREEN 3: SPORT DESCRIPTION ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportDetailScreen(navController: NavHostController, sportName: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sportName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { navController.navigate("slot_booking/$sportName") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KhelomoreOrange),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("BOOK NOW", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "About $sportName", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Experience the best $sportName facilities in your company. High-quality turf, standard dimensions, and all necessary equipment provided. Ideal for both beginners and experienced players.",
                color = Color.DarkGray,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Facilities", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                FacilityChip("Floodlights")
                Spacer(modifier = Modifier.width(8.dp))
                FacilityChip("Changing Rooms")
                Spacer(modifier = Modifier.width(8.dp))
                FacilityChip("Water")
            }
        }
    }
}

@Composable
fun FacilityChip(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = KhelomoreGray,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp)
    }
}
