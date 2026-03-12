package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.KhelomoreOrange

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute !in listOf("login", "signup", "splash")

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(navController, currentRoute)
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "category" || currentRoute == "sports_list",
            onClick = {
                navController.navigate("category") {
                    popUpTo("category") { inclusive = true }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = KhelomoreOrange,
                selectedTextColor = KhelomoreOrange,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = "History") },
            label = { Text("History") },
            selected = currentRoute == "booking_history",
            onClick = {
                navController.navigate("booking_history") {
                    launchSingleTop = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = KhelomoreOrange,
                selectedTextColor = KhelomoreOrange,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile") {
                    launchSingleTop = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = KhelomoreOrange,
                selectedTextColor = KhelomoreOrange,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
    }
}
