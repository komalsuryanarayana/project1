package com.example.myapplication.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.ViewModel.OutScheduleViewModel
import com.example.myapplication.ui.theme.KhelomoreGray
import com.example.myapplication.ui.theme.KhelomoreLightOrange
import com.example.myapplication.ui.theme.KhelomoreOrange


// --- SCREEN 4: SLOT BOOKING ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotBookingScreen(navController: NavHostController, sportName: String) {
    val vm: OutScheduleViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Slots") },
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
                        Text(if (vm.selectedSlot.intValue != -1) "${6+vm.selectedSlot.intValue}:00 PM" else "None", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { 
                            if(vm.selectedSlot.intValue != -1) {
                                val slots = listOf("06:00 PM", "07:00 PM", "08:00 PM", "09:00 PM", "10:00 PM", "11:00 PM")
                                val date = (15 + vm.selectedDate.intValue).toString() + " Oct"
                                vm.addBooking(sportName, date, slots[vm.selectedSlot.intValue])
                                navController.navigate("booking_pass/$sportName")
                            }
                        },
                        enabled = vm.selectedSlot.intValue != -1,
                        colors = ButtonDefaults.buttonColors(containerColor = KhelomoreOrange),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("CONFIRM BOOKING")
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
                    DateItem(index, index == vm.selectedDate.intValue) { vm.selectedDate.intValue = index }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Available Slots", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            val slots = listOf("06:00 PM", "07:00 PM", "08:00 PM", "09:00 PM", "10:00 PM", "11:00 PM")
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(slots.size) { index ->
                    SlotItem(slots[index], index == vm.selectedSlot.intValue) { vm.selectedSlot.intValue = index }
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

@Composable
fun SlotItem(time: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(
                1.dp,
                if (isSelected) KhelomoreOrange else Color.LightGray,
                RoundedCornerShape(8.dp)
            )
            .background(if (isSelected) KhelomoreLightOrange else Color.White, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(time, color = if (isSelected) KhelomoreOrange else Color.Black, fontSize = 14.sp)
    }
}
