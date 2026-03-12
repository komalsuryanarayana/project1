package com.example.myapplication.view

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.ViewModel.OutScheduleViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun Signuppage(controller: NavHostController) {

    val vm: OutScheduleViewModel = viewModel()
    val auth = Firebase.auth
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = vm.signupusername.value,
            onValueChange = { vm.signupusername.value = it },
            label = { Text(text = "Enter your Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = vm.signuppassword.value,
            onValueChange = { vm.signuppassword.value = it },
            label = { Text(text = "Enter your Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val email = vm.signupusername.value.trim()
                val password = vm.signuppassword.value

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Email and password required", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                else if (password.length < 6) {
                    Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                else{
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Sign up successful. Please login.", Toast.LENGTH_SHORT).show()
                            controller.popBackStack()
                        } else {
                            Toast.makeText(
                                context,
                                task.exception?.localizedMessage ?: "Sign up failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }


            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sign Up")
        }


        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Login",
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .semantics { role =Role.Button }
                .clickable(onClickLabel = "Login") {
                    controller.navigate("login")
                }
        )
    }
}
