package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("sports_list") { SportsListScreen(navController) }
        composable("sport_detail/{sportName}") { backStackEntry ->
            val sportName = backStackEntry.arguments?.getString("sportName") ?: "Sport"
            SportDetailScreen(navController, sportName)
        }
        composable("slot_booking/{sportName}") { backStackEntry ->
            val sportName = backStackEntry.arguments?.getString("sportName") ?: "Sport"
            SlotBookingScreen(navController, sportName)
        }
        composable("booking_pass/{sportName}") { backStackEntry ->
            val sportName = backStackEntry.arguments?.getString("sportName") ?: "Sport"
            BookingPassScreen(navController, sportName)
        }
    }
}