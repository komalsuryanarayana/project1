package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.view.BookingHistoryScreen
import com.example.myapplication.view.BookingPassScreen
import com.example.myapplication.view.Categorypage
import com.example.myapplication.view.SlotBookingScreen
import com.example.myapplication.view.SplashScreen
import com.example.myapplication.view.SportDetailScreen
import com.example.myapplication.view.SportsListScreen
import com.example.myapplication.view.UserProfileScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable ("splash"){ SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { Signuppage(navController) }
        composable("category/{username}") { k->
            val username = k.arguments?.getString("username")?:""
            Categorypage(navController, username) }
        composable("sports_list") { SportsListScreen(navController) }
        composable("booking_history") { BookingHistoryScreen(navController) }
        composable("profile") { UserProfileScreen(navController) }
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