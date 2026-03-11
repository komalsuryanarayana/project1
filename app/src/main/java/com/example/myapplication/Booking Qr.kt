package com.example.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.ui.theme.KhelomoreGray


// --- SCREEN 5: BOOKING PASS ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingPassScreen(navController: NavHostController, sportName: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Confirmation") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("sports_list") { popUpTo("sports_list") { inclusive = true } } }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(64.dp))
            Text("Booking Confirmed!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(32.dp))

            // Ticket View
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Sport", color = Color.Gray, fontSize = 12.sp)
                            Text(sportName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Booking ID", color = Color.Gray, fontSize = 12.sp)
                            Text("#KM-29482", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Date", color = Color.Gray, fontSize = 12.sp)
                            Text("Wed, 15 Oct", fontWeight = FontWeight.Medium)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Time", color = Color.Gray, fontSize = 12.sp)
                            Text("07:00 PM - 08:00 PM", fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Dotted line
                    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // QR Placeholder
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(KhelomoreGray, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = "QR Code", modifier = Modifier.size(100.dp), tint = Color.DarkGray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Scan this QR at the venue entrance",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = { navController.navigate("sports_list") { popUpTo("sports_list") { inclusive = true } } },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("BACK TO HOME")
            }
        }
    }
}
