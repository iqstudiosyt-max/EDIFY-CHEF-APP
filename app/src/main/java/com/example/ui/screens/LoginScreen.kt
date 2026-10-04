package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.FoodKingTopBar
import com.example.ui.theme.FoodKingPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.KitchenViewModel
import com.example.ui.viewmodel.LoginUiState

@Composable
fun LoginScreen(
    viewModel: KitchenViewModel,
    modifier: Modifier = Modifier
) {
    val loginState by viewModel.loginState.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val currentServerUrl by viewModel.serverUrl.collectAsState()
    val currentApiKey by viewModel.apiKey.collectAsState()

    var email by remember { mutableStateOf("chef@eventrra.pk") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }

    var showServerSettingsDialog by remember { mutableStateOf(false) }
    var tempUrl by remember(currentServerUrl) { mutableStateOf(currentServerUrl) }
    var tempApiKey by remember(currentApiKey) { mutableStateOf(currentApiKey) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        FoodKingTopBar(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { viewModel.setLanguage(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Center Chef Cooking Illustration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_chef_cooking),
                contentDescription = "Chef Cooking Illustration",
                modifier = Modifier
                    .size(170.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Welcome",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FoodKingPrimary
            )
            Text(
                text = "back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Login to access your dashboard, orders, history & others information and serve food to customers.",
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email Field - Explicit black typed text color
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
                    .testTag("email_input"),
                placeholder = { Text("Enter your email", fontSize = 14.sp, color = Color(0xFF94A3B8)) },
                singleLine = true,
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Normal),
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

            // Password Field - Explicit black typed text color
            Text(
                text = "Password",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password_input"),
                placeholder = { Text("Enter your password", fontSize = 14.sp, color = Color(0xFF94A3B8)) },
                singleLine = true,
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Normal),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
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

            Spacer(modifier = Modifier.height(12.dp))

            // Remember Me Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { rememberMe = !rememberMe },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = FoodKingPrimary,
                        checkmarkColor = Color.White
                    ),
                    modifier = Modifier.testTag("remember_me_checkbox")
                )
                Text(
                    text = "Remember me",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Button
            Button(
                onClick = {
                    viewModel.login(email.trim(), password.trim(), rememberMe)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FoodKingPrimary,
                    contentColor = Color.White
                ),
                enabled = loginState !is LoginUiState.Loading
            ) {
                if (loginState is LoginUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Real Server Error Feedback
            if (loginState is LoginUiState.Error) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF2F2))
                        .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = (loginState as LoginUiState.Error).message,
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Quick Demo / Offline Option
            Spacer(modifier = Modifier.height(10.dp))
            androidx.compose.material3.OutlinedButton(
                onClick = { viewModel.loginDemo() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("demo_mode_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = FoodKingPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, FoodKingPrimary)
            ) {
                Text(
                    text = "Explore in Demo Mode (Sample Kitchen)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Server & API Settings Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF8FAFC))
                    .clickable { showServerSettingsDialog = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "API Key & Server Settings",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Server & API Key Dialog
    if (showServerSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showServerSettingsDialog = false },
            title = {
                Text(
                    text = "API Key & Server Settings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Configure API base URL and FoodKing x-api-key header:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Base URL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = tempUrl,
                        onValueChange = { tempUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 13.sp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "x-api-key (API Key)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = tempApiKey,
                        onValueChange = { tempApiKey = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter API key from server", fontSize = 12.sp) },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 13.sp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateServerSettings(tempUrl.trim(), tempApiKey.trim())
                        showServerSettingsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodKingPrimary)
                ) {
                    Text("Save Settings", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showServerSettingsDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
