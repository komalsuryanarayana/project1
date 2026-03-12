package com.example.myapplication.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    
    // Stream bookings from Firebase in real-time
    val userBookings by repo.streamUserBookings(currentUserEmail).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Bookings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        if (userBookings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No bookings found", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(userBookings.reversed()) { booking ->
                    HistoryCard(
                        booking = booking,
                        onCardClick = {
                            navController.navigate("booking_pass/${booking.sportName}")
                        },
                        onCancelClick = {
                            scope.launch {
                                val success = repo.cancelBooking(booking.id, booking.sportName, booking.time)
                                if (success) {
                                    Toast.makeText(context, "Booking Cancelled", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to cancel", Toast.LENGTH_SHORT).show()
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
fun HistoryCard(booking: Booking, onCardClick: () -> Unit, onCancelClick: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cancel Booking") },
            text = { Text("Are you sure you want to cancel this booking for ${booking.sportName}?") },
            confirmButton = {
                TextButton(onClick = {
                    onCancelClick()
                    showDialog = false
                }) {
                    Text("Yes, Cancel", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(50.dp).background(KhelomoreLightOrange, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = KhelomoreOrange)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(booking.sportName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("${booking.date} | ${booking.time}", fontSize = 14.sp, color = Color.Gray)
                }
                
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Cancel, contentDescription = "Cancel", tint = Color.Red.copy(alpha = 0.7f))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ID: ${booking.id}", fontSize = 12.sp, color = KhelomoreOrange, fontWeight = FontWeight.Medium)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (booking.status == "Confirmed") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ) {
                    Text(
                        booking.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (booking.status == "Confirmed") Color(0xFF2E7D32) else Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
