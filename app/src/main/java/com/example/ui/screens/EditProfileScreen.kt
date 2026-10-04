package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderLight
import com.example.ui.theme.FoodKingPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.CurrentScreen
import com.example.ui.viewmodel.KitchenViewModel

@Composable
fun EditProfileScreen(
    viewModel: KitchenViewModel,
    modifier: Modifier = Modifier
) {
    val currentProfile by viewModel.userProfile.collectAsState()

    var firstName by remember { mutableStateOf(currentProfile.firstName) }
    var lastName by remember { mutableStateOf(currentProfile.lastName) }
    var email by remember { mutableStateOf(currentProfile.email) }
    var phone by remember { mutableStateOf(currentProfile.phone) }
    var countryCode by remember { mutableStateOf(currentProfile.countryCode) }
    var codeMenuExpanded by remember { mutableStateOf(false) }

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
                modifier = Modifier.testTag("back_button_edit_profile")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = FoodKingPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Edit Profile",
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
            // First Name
            Text(
                text = "First Name",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("first_name_input"),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 14.sp),
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

            // Last Name
            Text(
                text = "Last Name",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("last_name_input"),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 14.sp),
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

            // Email
            Text(
                text = "Email",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_email_input"),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

            // Mobile Number
            Text(
                text = "Mobile Number",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Country Code Selector
                Box {
                    Row(
                        modifier = Modifier
                            .height(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                            .clickable { codeMenuExpanded = true }
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🇧🇩 $countryCode",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            tint = Color(0xFF64748B)
                        )
                    }

                    DropdownMenu(
                        expanded = codeMenuExpanded,
                        onDismissRequest = { codeMenuExpanded = false }
                    ) {
                        listOf("+880", "+92", "+1", "+44", "+971", "+966").forEach { code ->
                            DropdownMenuItem(
                                text = { Text(code, fontSize = 13.sp) },
                                onClick = {
                                    countryCode = code
                                    codeMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("profile_phone_input"),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Update Profile Button
            Button(
                onClick = {
                    viewModel.updateProfile(
                        firstName = firstName.trim(),
                        lastName = lastName.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        countryCode = countryCode
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("update_profile_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FoodKingPrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Update Profile",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
