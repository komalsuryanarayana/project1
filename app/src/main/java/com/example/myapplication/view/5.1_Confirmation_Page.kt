package com.example.myapplication.view


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ConfirmationScreen(navController: NavHostController, sportName: String) {
    Scaffold(
        bottomBar = {
            Column {
                HorizontalDivider(thickness = 1.dp, color = Color(0xFF0D99FF))
                NavigationBar(
                    containerColor = Color(0xFFEAF6FF),
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(70.dp)
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { navController.navigate("sports_list") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(32.dp)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0D99FF),
                            unselectedIconColor = Color(0xFF0D99FF),
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("history") },
                        icon = { Icon(Icons.Default.History, contentDescription = "History", modifier = Modifier.size(32.dp)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0D99FF),
                            unselectedIconColor = Color(0xFF0D99FF),
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { /* Navigate Profile */ },
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(32.dp)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0D99FF),
                            unselectedIconColor = Color(0xFF0D99FF),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Back Button
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { navController.popBackStack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Back", fontSize = 16.sp)
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "OutSchedule",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Confirmation Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .border(1.dp, Color(0xFFB3E5FC), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Close Icon
                        Box(modifier = Modifier.fillMaxWidth()) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.LightGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Are you sure?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { navController.navigate("booking_pass/$sportName") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D99FF)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.width(100.dp)
                            ) {
                                Text("Yes", color = Color.White)
                            }

                            Button(
                                onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D99FF)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.width(100.dp)
                            ) {
                                Text("No", color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
