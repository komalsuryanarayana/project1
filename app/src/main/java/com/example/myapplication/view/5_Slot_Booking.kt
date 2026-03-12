package com.example.myapplication.view


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.Model.Slot
import com.example.myapplication.repo.SlotRepository
import com.example.myapplication.ui.theme.KhelomoreGray
import com.example.myapplication.ui.theme.KhelomoreLightOrange
import com.example.myapplication.ui.theme.KhelomoreOrange
import kotlinx.coroutines.launch

// --- SCREEN 4: SLOT BOOKING ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotBookingScreen(navController: NavHostController, sportName: String) {
    var selectedDate by remember { mutableIntStateOf(0) }
    var selectedSlotId by remember { mutableStateOf<String?>(null) }
    var selectedSlotLabel by remember { mutableStateOf("") }
    var isBooking by remember { mutableStateOf(false) }

    val repo = remember { SlotRepository() }
    var slots by remember { mutableStateOf(listOf<Slot>()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(sportName) {
        repo.seedSlotsIfEmpty(
            sportName = sportName,
            labels = listOf("06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM", "09:40 AM", "10:00 AM", "11:00 AM", "11:30 AM", "12:00 PM")
        )
    }

    LaunchedEffect(sportName) {
        repo.streamSlots(sportName).collect { slots = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Slots - $sportName") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Selected Slot", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            if (selectedSlotId != null) selectedSlotLabel else "None",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = {
                            selectedSlotId?.let { id ->
                                scope.launch {
                                    isBooking = true
                                    // Pass selectedSlotLabel and selectedDate to repo to calculate startTime
                                    val success = repo.bookSlot(sportName, id, selectedSlotLabel, selectedDate)
                                    isBooking = false
                                    if (success) {
                                        Toast.makeText(context, "Booking Successful!", Toast.LENGTH_LONG).show()
                                        navController.navigate("booking_pass/$sportName")
                                    } else {
                                        Toast.makeText(context, "Booking failed.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        enabled = selectedSlotId != null && !isBooking,
                        colors = ButtonDefaults.buttonColors(containerColor = KhelomoreOrange),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isBooking) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("CONFIRM BOOKING")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("Select Date", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(7) { index ->
                    DateItem(index, index == selectedDate) { selectedDate = index }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Available Slots", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(slots) { slot ->
                    Box(
                        Modifier
                            .height(60.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    slot.isBooked -> Color.LightGray
                                    slot.id == selectedSlotId -> KhelomoreLightOrange
                                    else -> Color.White
                                }
                            )
                            .border(
                                1.dp,
                                if (slot.id == selectedSlotId) KhelomoreOrange else Color.LightGray,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable(enabled = !slot.isBooked) {
                                selectedSlotId = slot.id
                                selectedSlotLabel = slot.label
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            slot.label + if (slot.isBooked) "\nBooked" else "",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = if (slot.id == selectedSlotId) KhelomoreOrange else Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateItem(day: Int, isSelected: Boolean, onClick: () -> Unit) {
    val date = 15 + day
    val dayName = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")[day % 7]
    Column(
        modifier = Modifier
            .width(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) KhelomoreOrange else KhelomoreGray)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(dayName, color = if (isSelected) Color.White else Color.Gray, fontSize = 12.sp)
        Text(date.toString(), color = if (isSelected) Color.White else Color.Black, fontWeight = FontWeight.Bold)
    }
}
