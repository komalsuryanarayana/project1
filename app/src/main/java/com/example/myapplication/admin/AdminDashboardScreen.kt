package com.example.myapplication.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.Model.Booking
import com.example.myapplication.ViewModel.OutScheduleViewModel
import com.example.myapplication.ui.theme.KhelomoreOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavHostController) {
    val vm: OutScheduleViewModel = viewModel()


    // Fetch all bookings from Firebase
    LaunchedEffect(Unit) {
        vm.repo.streamAllBookings().collect {
            vm.allBookings.value = it
        }
    }

    // Filtering Logic
    val filteredBookings = vm.allBookings.value.filter {
        it.userId.contains(vm.searchQuerys.value, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Dashboard", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* Handle Logout */
                        navController.navigate("login") { popUpTo(0) }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {

            // Search Bar
            OutlinedTextField(
                value = vm.searchQuerys.value,
                onValueChange = { vm.searchQuerys.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search by user email...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                text = "Total Bookings: ${filteredBookings.size}",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredBookings) { booking ->
                    AdminBookingCard(booking)
                }
            }
        }
    }
}

@Composable
fun AdminBookingCard(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(KhelomoreOrange.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = KhelomoreOrange)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = booking.userId, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "ID: ${booking.id.takeLast(8)}", fontSize = 12.sp, color = Color.Gray)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Sport", color = Color.Gray, fontSize = 11.sp)
                    Text(booking.sportName, fontWeight = FontWeight.Medium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Date & Time", color = Color.Gray, fontSize = 11.sp)
                    Text("${booking.date} | ${booking.time}", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}