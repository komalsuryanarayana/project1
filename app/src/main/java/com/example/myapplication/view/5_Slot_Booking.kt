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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.Model.Slot
import com.example.myapplication.ViewModel.OutScheduleViewModel
import com.example.myapplication.repo.SlotRepository
import com.example.myapplication.ui.theme.KhelomoreGray
import com.example.myapplication.ui.theme.KhelomoreLightOrange
import com.example.myapplication.ui.theme.KhelomoreOrange
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

// --- SCREEN 4: SLOT BOOKING ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotBookingScreen(navController: NavHostController, sportName: String) {
    var vm: OutScheduleViewModel = viewModel()


    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Generate next 7 days starting from today
    val calendarDays = remember {
        (0 until 7).map {
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, it) }
        }
    }

    LaunchedEffect(sportName) {
        vm.repo.seedSlotsIfEmpty(
            sportName = sportName,
            labels = listOf("06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM", "09:40 AM", "10:00 AM", "11:00 AM", "11:30 AM", "12:00 PM")
        )
    }

    LaunchedEffect(sportName) {
        vm.repo.streamSlots(sportName).collect { vm.slots.value = it }
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
                            if (vm.selectedSlotId.value != null) vm.selectedSlotLabel.value else "None",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = {
                            vm.selectedSlotId.value?.let { id ->
                                scope.launch {
                                    vm.isBookings.value = true
                                    // Pass selectedSlotLabel and selectedDate to repo to calculate startTime
                                    val bookingId = vm.repo.bookSlot(sportName, id, vm.selectedSlotLabel.value, vm.selectedDate.intValue)
                                    vm.isBookings.value = false
                                    if (bookingId != null) {
                                        Toast.makeText(context, "Booking Successful!", Toast.LENGTH_LONG).show()
                                        // Pass the new bookingId to the pass screen
                                        navController.navigate("booking_pass/$sportName?bookingId=$bookingId")
                                    } else {
                                        Toast.makeText(context, "Booking failed.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        enabled = vm.selectedSlotId.value != null && !vm.isBookings.value,
                        colors = ButtonDefaults.buttonColors(containerColor = KhelomoreOrange),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (vm.isBookings.value) {
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
                    val isEnabled = index == 0 // Only allow today, disable all following days
                    DateItem(
                        calendar = calendarDays[index],
                        isSelected = index == vm.selectedDate.intValue,
                        isEnabled = isEnabled
                    ) {
                        if (isEnabled) vm.selectedDate.intValue = index
                    }
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
                items(vm.slots.value) { slot ->
                    Box(
                        Modifier
                            .height(60.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    slot.isBooked -> Color.LightGray
                                    slot.id == vm.selectedSlotId.value -> KhelomoreLightOrange
                                    else -> Color.White
                                }
                            )
                            .border(
                                1.dp,
                                if (slot.id == vm.selectedSlotId.value) KhelomoreOrange else Color.LightGray,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable(enabled = !slot.isBooked) {
                                vm.selectedSlotId.value = slot.id
                                vm.selectedSlotLabel.value = slot.label
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            slot.label + if (slot.isBooked) "\nBooked" else "",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = if (slot.id == vm.selectedSlotId.value) KhelomoreOrange else Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateItem(calendar: Calendar, isSelected: Boolean, isEnabled: Boolean, onClick: () -> Unit) {
    val date = calendar.get(Calendar.DAY_OF_MONTH).toString()
    val dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""
    Column(
        modifier = Modifier
            .width(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                when {
                    isSelected -> KhelomoreOrange
                    !isEnabled -> Color.LightGray.copy(alpha = 0.4f)
                    else -> KhelomoreGray
                }
            )
            .clickable(enabled = isEnabled) { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(dayName, color = if (isSelected) Color.White else Color.Gray, fontSize = 12.sp)
        Text(date, color = if (isSelected) Color.White else if (isEnabled) Color.Black else Color.Gray.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
    }
}
