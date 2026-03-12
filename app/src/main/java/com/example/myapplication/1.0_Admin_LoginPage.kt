package com.example.myapplication


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.ViewModel.OutScheduleViewModel
import com.example.myapplication.ui.theme.KhelomoreOrange
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AdminLoginPage(navController: NavHostController) {
    val vm: OutScheduleViewModel = viewModel()
    val auth = Firebase.auth
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(KhelomoreOrange.copy(alpha = 0.1f), Color.White)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Modern Title
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xFF1A1A1A))) {
                            append("Out")
                        }
                        withStyle(style = SpanStyle(color = KhelomoreOrange)) {
                            append("Schedule")
                        }
                    },
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
                
                Surface(
                    modifier = Modifier.padding(vertical = 8.dp).width(40.dp).height(4.dp),
                    color = KhelomoreOrange,
                    shape = RoundedCornerShape(2.dp)
                ) {}

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Admin Portal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "Sign in to manage bookings",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = vm.adminloginusername.value,
                    onValueChange = { vm.adminloginusername.value = it },
                    label = { Text("Admin Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = KhelomoreOrange) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KhelomoreOrange,
                        focusedLabelColor = KhelomoreOrange
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = vm.adminloginpassword.value,
                    onValueChange = { vm.adminloginpassword.value = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = KhelomoreOrange) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = if (vm.passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { vm.passwordVisible.value = !vm.passwordVisible.value }) {
                            Icon(
                                imageVector = if (vm.passwordVisible.value) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KhelomoreOrange,
                        focusedLabelColor = KhelomoreOrange
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val email = vm.adminloginusername.value.trim()
                        val password = vm.adminloginpassword.value

                        if (email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "All fields required", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        if (!email.endsWith("@ltmadmin.com")) {
                            Toast.makeText(context, "Invalid admin credentials", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                navController.navigate("adminpage") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KhelomoreOrange),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Login as Admin", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = { navController.navigate("login") }) {
                    Text(
                        text = "Are you an Employee? Login here",
                        color = KhelomoreOrange,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
