package com.example.myapplication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
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
import androidx.navigation.NavController
import com.example.myapplication.ViewModel.OutScheduleViewModel
import com.example.myapplication.ui.theme.KhelomoreOrange
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun Signuppage(navController: NavController) {
    val vm: OutScheduleViewModel = viewModel()
    val auth = Firebase.auth
    val context = LocalContext.current


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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Create Account",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "Join as an employee",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = vm.signupusername.value,
                    onValueChange = { vm.signupusername.value = it },
                    label = { Text("Work Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = KhelomoreOrange) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    placeholder = { Text("name@LtmEmp.com") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KhelomoreOrange,
                        focusedLabelColor = KhelomoreOrange
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = vm.signuppassword.value,
                    onValueChange = { vm.signuppassword.value = it },
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = vm.confirmPassword.value,
                    onValueChange = { vm.confirmPassword.value = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = KhelomoreOrange) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = if (vm.confirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { vm.confirmPasswordVisible.value = !vm.confirmPasswordVisible.value }) {
                            Icon(
                                imageVector = if (vm.confirmPasswordVisible.value) Icons.Default.Visibility else Icons.Default.VisibilityOff,
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
                        val email = vm.signupusername.value.trim()
                        val password = vm.signuppassword.value
                        val confirmPassword = vm.confirmPassword.value
                        
                        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val requiredDomain = "@LtmEmp.com"
                        if (!email.endsWith(requiredDomain, ignoreCase = true)) {
                            Toast.makeText(context, "Email must end with $requiredDomain", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (password != confirmPassword) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (password.length < 6) {
                            Toast.makeText(context, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        vm.isLoadingsignup.value = true
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                            vm.isLoadingsignup.value = false
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                                navController.navigate("login") {
                                    popUpTo("signup") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Error: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KhelomoreOrange),
                    enabled = !vm.isLoadingsignup.value
                ) {
                    if (vm.isLoadingsignup.value) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Sign Up",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Already have an account? ", color = Color.Gray, fontSize = 14.sp)
                    TextButton(
                        onClick = { 
                            navController.navigate("login") 
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Log In", color = KhelomoreOrange, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
