package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FoodKingPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.CurrentScreen
import com.example.ui.viewmodel.KitchenViewModel

@Composable
fun ChangePasswordScreen(
    viewModel: KitchenViewModel,
    modifier: Modifier = Modifier
) {
    var oldPassword by remember { mutableStateOf("James123") }
    var newPassword by remember { mutableStateOf("") }
    var retypePassword by remember { mutableStateOf("") }

    var oldVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var retypeVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    BackHandler {
        viewModel.navigateTo(CurrentScreen.MAIN)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(CurrentScreen.MAIN) },
                modifier = Modifier.testTag("back_button_change_password")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = FoodKingPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Change Password",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Old Password
            Text(
                text = "Old Password",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = oldPassword,
                onValueChange = { oldPassword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("old_password_input"),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 14.sp),
                visualTransformation = if (oldVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { oldVisible = !oldVisible }) {
                        Icon(
                            imageVector = if (oldVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle old password",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedBorderColor = FoodKingPrimary,
                    cursorColor = FoodKingPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New Password
            Text(
                text = "New Password",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_password_input"),
                placeholder = { Text("Enter new password", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 14.sp),
                visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { newVisible = !newVisible }) {
                        Icon(
                            imageVector = if (newVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle new password",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedBorderColor = FoodKingPrimary,
                    cursorColor = FoodKingPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Retype New Password
            Text(
                text = "Retype New Password",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = retypePassword,
                onValueChange = { retypePassword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("retype_password_input"),
                placeholder = { Text("Confirm new password", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 14.sp),
                visualTransformation = if (retypeVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { retypeVisible = !retypeVisible }) {
                        Icon(
                            imageVector = if (retypeVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle retype password",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedBorderColor = FoodKingPrimary,
                    cursorColor = FoodKingPrimary
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Change Password Button
            Button(
                onClick = {
                    if (newPassword.isBlank()) {
                        errorMessage = "Please enter a new password."
                    } else if (newPassword != retypePassword) {
                        errorMessage = "New passwords do not match."
                    } else {
                        errorMessage = null
                        viewModel.changePassword(oldPassword, newPassword)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_change_password_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FoodKingPrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Change Password",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
