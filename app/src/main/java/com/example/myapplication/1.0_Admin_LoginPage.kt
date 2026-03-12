package com.example.myapplication


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.ViewModel.OutScheduleViewModel
import com.example.myapplication.ui.theme.KhelomoreOrange
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

// --- SCREEN 1: LOGIN ---

@Composable
fun AdminLoginPage(navController: NavHostController) {

    val vm: OutScheduleViewModel = viewModel()
    val auth = Firebase.auth
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)), contentAlignment = Alignment.Center){
        Card (
            modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Log In for Admin",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(32.dp))


                Text(
                    "Recreation Slot Booking",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = KhelomoreOrange
                )

                Spacer(modifier = Modifier.height(48.dp))


                OutlinedTextField(
                    value = vm.adminloginusername.value,
                    onValueChange = { vm.adminloginusername.value = it },
                    label = { Text("Email ") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF5722),
                        focusedLabelColor = Color(0xFFFF5722),
                        unfocusedBorderColor = Color.LightGray,
                        unfocusedLabelColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = vm.adminloginpassword.value,
                    onValueChange = { vm.adminloginpassword.value = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (vm.passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { vm.passwordVisible.value = !vm.passwordVisible.value }) {
                            Icon(
                                imageVector = if (vm.passwordVisible.value) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val email = vm.adminloginusername.value.trim()
                        val password = vm.adminloginpassword.value

                        if (!email.isNotBlank() || !password.isNotBlank()) {
                            Toast.makeText(context, "Email and password required", Toast.LENGTH_SHORT).show()
                        }

//                    else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                        Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show()
//                    }


                        else if(!email.endsWith("@ltmadmin.com")){
                            Toast.makeText(context,"Invalid email address",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navController.navigate("adminpage")
                                    {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "Incorrect Details", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    })
                {
                    Text(
                        text = "Log In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Are you an Employee? ",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    TextButton (
                        onClick = { navController.navigate("login") },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Login here",
                            color = Color(0xFFFF5722),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            }
        }
    }
}
