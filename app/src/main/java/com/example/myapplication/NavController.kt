package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.view.BookingHistoryScreen
import com.example.myapplication.view.BookingPassScreen
import com.example.myapplication.view.Categorypage
import com.example.myapplication.view.SlotBookingScreen
import com.example.myapplication.view.SportDetailScreen
import com.example.myapplication.view.SportsListScreen
import com.example.myapplication.view.UserProfileScreen
import com.google.firebase.auth.FirebaseAuth
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private fun isAdmin(email: String?): Boolean {
    // Defensive checks + case-insensitive compare
    val normalized = email?.trim()?.lowercase() ?: return false
    // Match emails that end with '@ltmadmin.com'
    return normalized.endsWith("@ltmadmin.com")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val email = currentUser?.email

    // Encode username for safe nav route usage
    val encodedEmail = email?.let {
        URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
    } ?: ""

    // If user is admin -> Admin dashboard; if normal user -> category; else -> login
    val startDestination = when {
        currentUser != null && isAdmin(email) -> "AdminDashboardScreen"   // or "adminpage" if you prefer
        currentUser != null -> "category/$encodedEmail"
        else -> "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }
        composable("adminlogin") { AdminLoginPage(navController) }
        composable("AdminDashboardScreen") { AdminDashboardScreen(navController) }
        composable("signup") { Signuppage(navController) }

        composable("category/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            Categorypage(navController, username)
        }

        composable("sports_list") { SportsListScreen(navController) }
        composable("booking_history") { BookingHistoryScreen(navController) }

        composable("profile/{currentuser}") { backStackEntry ->
            val currentuser = backStackEntry.arguments?.getString("currentuser") ?: ""
            UserProfileScreen(navController, currentuser)
        }

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

        composable("adminpage") {
            // If you also use "adminpage" elsewhere, keep this too
            AdminDashboardScreen(navController)
        }
    }
}