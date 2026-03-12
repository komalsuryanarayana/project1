package com.example.myapplication.view



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    // Navigate to login after 2 seconds
    LaunchedEffect(key1 = true) {
        delay(2000)
        navController.navigate("login") {
            // Pop the splash screen off the back stack so the user can't go back to it
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.SportsTennis,
                contentDescription = "Logo",
                tint = Color(0xFF0D99FF),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Out",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 36.sp
                )
                Text(
                    text = "Schedule",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 36.sp
                )
            }
        }
    }
}
