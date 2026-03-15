package com.example.myapplication.view


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.myapplication.notification.BookingReminderReceiver
import com.example.myapplication.Model.Slot
import com.example.myapplication.ViewModel.OutScheduleViewModel
import com.example.myapplication.ui.theme.KhelomoreGray
import com.example.myapplication.ui.theme.KhelomoreLightOrange
import com.example.myapplication.ui.theme.KhelomoreOrange
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.myapplication.notification.NotificationWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

// --- SCREEN 4: SLOT BOOKING ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotBookingScreen(navController: NavHostController, sportName: String) {
    val vm: OutScheduleViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Today's date info
    val today = remember { Calendar.getInstance() }
    
    // State to keep track of current time to refresh "Passed" slots live


    // Update current time every minute
    LaunchedEffect(Unit) {
        while (true) {
            vm.currentTimeMillis.value = System.currentTimeMillis()
            delay(60000)
        }
    }

    LaunchedEffect(sportName) {
        vm.repo.seedSlotsIfEmpty(
            sportName = sportName,
            labels = listOf(
                "06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM", 
                "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM", 
                "06:00 PM"
            )
        )
    }

    LaunchedEffect(sportName) {
        vm.repo.streamSlots(sportName).collect { vm.slots.value = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Slots - $sportName", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 16.dp, color = Color.White) {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Selected Slot", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            if (vm.selectedSlotId.value != null) vm.selectedSlotLabel.value else "None",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = KhelomoreOrange
                        )
                    }
                    Button(
                        onClick = {
                            vm.selectedSlotId.value?.let { id ->
                                scope.launch {
                                    vm.isBookings.value = true
                                    val bookingId = vm.repo.bookSlot(sportName, id, vm.selectedSlotLabel.value, 0)
                                    vm.isBookings.value = false
                                    if (bookingId != null) {
                                        scheduleNotification(context, sportName, vm.selectedSlotLabel.value, 0)
                                        scheduleWorkNotification(context, sportName, vm.selectedSlotLabel.value, 0)
                                        Toast.makeText(context, "Booking Successful!", Toast.LENGTH_LONG).show()
                                        navController.navigate("booking_pass/$sportName?bookingId=$bookingId")
                                    } else {
                                        Toast.makeText(context, "Booking failed or already booked today.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        enabled = vm.selectedSlotId.value != null && !vm.isBookings.value,
                        colors = ButtonDefaults.buttonColors(containerColor = KhelomoreOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        if (vm.isBookings.value) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("CONFIRM", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Booking For Today", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Single Today Date Card
            Surface(
                modifier = Modifier
                    .width(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(KhelomoreOrange),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        today.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: "", 
                        color = Color.White.copy(alpha = 0.8f), 
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        today.get(Calendar.DAY_OF_MONTH).toString(), 
                        color = Color.White, 
                        fontWeight = FontWeight.ExtraBold, 
                        fontSize = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Available Time Slots", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(vm.slots.value) { slot ->
                    val slotTimeMillis = calculateTimestamp(slot.label, 0)
                    val isPast = slotTimeMillis < vm.currentTimeMillis.value
                    val isUnavailable = slot.isBooked || isPast

                    Box(
                        Modifier
                            .height(65.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    isUnavailable -> Color(0xFFF0F0F0)
                                    slot.id == vm.selectedSlotId.value -> KhelomoreLightOrange
                                    else -> Color.White
                                }
                            )
                            .border(
                                1.5.dp,
                                when {
                                    slot.id == vm.selectedSlotId.value -> KhelomoreOrange
                                    isUnavailable -> Color.Transparent
                                    else -> Color(0xFFE0E0E0)
                                },
                                RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = !isUnavailable) {
                                vm.selectedSlotId.value = slot.id
                                vm.selectedSlotLabel.value = slot.label
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = slot.label,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                fontWeight = if (slot.id == vm.selectedSlotId.value) FontWeight.Bold else FontWeight.Medium,
                                color = when {
                                    isUnavailable -> Color.LightGray
                                    slot.id == vm.selectedSlotId.value -> KhelomoreOrange
                                    else -> Color.Black
                                }
                            )
                            if (isUnavailable) {
                                Text(
                                    text = if (slot.isBooked) "Booked" else "Passed",
                                    fontSize = 10.sp,
                                    color = Color.LightGray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun scheduleWorkNotification(context: Context, sportName: String, timeLabel: String, dayOffset: Int) {
    val targetTimeMillis = calculateTimestamp(timeLabel, dayOffset)
    val delay = targetTimeMillis - (15 * 60 * 1000) - System.currentTimeMillis()

    if (delay > 0) {
        val inputData = Data.Builder()
            .putString("sportName", sportName)
            .putString("time", timeLabel)
            .build()

        val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}

private fun scheduleNotification(context: android.content.Context, sportName: String, timeLabel: String, dayOffset: Int) {
    val targetTimeMillis = calculateTimestamp(timeLabel, dayOffset)
    val triggerAtMillis = targetTimeMillis - (15 * 60 * 1000)

    if (triggerAtMillis > System.currentTimeMillis()) {
        val intent = Intent(context, BookingReminderReceiver::class.java).apply {
            putExtra("sportName", sportName)
            putExtra("timeLabel", timeLabel)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sportName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }
}

private fun calculateTimestamp(label: String, dayOffset: Int): Long {
    return try {
        val parts = label.trim().split(" ", ":")
        var hour = parts[0].toInt()
        val min = parts[1].toInt()
        val amPm = parts[2]
        if (amPm.equals("PM", true) && hour != 12) hour += 12
        if (amPm.equals("AM", true) && hour == 12) hour = 0

        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, dayOffset)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    } catch (e: Exception) { 0L }
}
