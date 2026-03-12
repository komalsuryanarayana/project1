package com.example.myapplication.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.Model.Booking
import com.example.myapplication.ViewModel.OutScheduleViewModel
import com.example.myapplication.repo.SlotRepository
import com.example.myapplication.ui.theme.KhelomoreLightOrange
import com.example.myapplication.ui.theme.KhelomoreOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(navController: NavHostController) {
    val vm: OutScheduleViewModel = viewModel()
    val repo = remember { SlotRepository() }
    val currentUserEmail = vm.getCurrentUserEmail()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val userBookings by repo.streamUserBookings(currentUserEmail).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("My Activity", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFBFBFB)
    ) { innerPadding ->
        if (userBookings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SportsScore, contentDescription = null, modifier = Modifier.size(50.dp), tint = Color.LightGray)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("No bookings yet", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 18.sp)
                    Text("Go book a slot to see it here", color = Color.LightGray, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(userBookings.reversed()) { booking ->
                    ModernHistoryCard(
                        booking = booking,
                        onCardClick = {
                            navController.navigate("booking_pass/${booking.sportName}")
                        },
                        onCancelClick = {
                            scope.launch {
                                val success = repo.cancelBooking(booking.id, booking.sportName, booking.time)
                                if (success) {
                                    Toast.makeText(context, "Booking removed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ModernHistoryCard(booking: Booking, onCardClick: () -> Unit, onCancelClick: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cancel Booking") },
            text = { Text("Are you sure you want to cancel your ${booking.sportName} slot?") },
            confirmButton = {
                TextButton(onClick = {
                    onCancelClick()
                    showDialog = false
                }) {
                    Text("Yes, Cancel", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(KhelomoreLightOrange),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = KhelomoreOrange, modifier = Modifier.size(28.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(booking.sportName, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Color(0xFF1A1A1A))
                Text("${booking.date} • ${booking.time}", fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        booking.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier.clip(CircleShape).background(Color(0xFFFFEBEE))
            ) {
                Icon(Icons.Default.Cancel, contentDescription = "Cancel", tint = Color.Red, modifier = Modifier.size(20.dp))
            }
        }
    }
}
