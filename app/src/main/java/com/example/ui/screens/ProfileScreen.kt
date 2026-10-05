package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FoodKingTopBar
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderLight
import com.example.ui.theme.FoodKingPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CurrentScreen
import com.example.ui.viewmodel.KitchenViewModel

@Composable
fun ProfileScreen(
    viewModel: KitchenViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val profileMessage by viewModel.profileMessage.collectAsState()

    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        FoodKingTopBar(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { viewModel.setLanguage(it) },
            onRefresh = { viewModel.refreshKitchenData() },
            modifier = Modifier.background(Color.White)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
            text = "My Profile",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar with Camera icon
        Box(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
                    .border(2.dp, FoodKingPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(54.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(FoodKingPrimary)
                    .clickable { /* change photo */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change photo",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // User Name & details
        Text(
            text = "${profile.firstName} ${profile.lastName}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = profile.email,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = profile.phone,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Branch Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = "Branch",
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Branch",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = profile.branch,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Menu List
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
        ) {
            Column {
                ProfileMenuItem(
                    title = "Edit Profile",
                    icon = Icons.Default.Edit,
                    testTag = "menu_edit_profile",
                    onClick = { viewModel.navigateTo(CurrentScreen.EDIT_PROFILE) }
                )
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF8FAFC))

                ProfileMenuItem(
                    title = "Change Password",
                    icon = Icons.Default.Lock,
                    testTag = "menu_change_password",
                    onClick = { viewModel.navigateTo(CurrentScreen.CHANGE_PASSWORD) }
                )
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF8FAFC))

                ProfileMenuItem(
                    title = "Terms & Conditions",
                    icon = Icons.Default.Description,
                    testTag = "menu_terms",
                    onClick = { showTermsDialog = true }
                )
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF8FAFC))

                ProfileMenuItem(
                    title = "Privacy Policy",
                    icon = Icons.Default.Security,
                    testTag = "menu_privacy",
                    onClick = { showPrivacyDialog = true }
                )
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF8FAFC))

                ProfileMenuItem(
                    title = "Log Out",
                    icon = Icons.Default.Logout,
                    textColor = Color(0xFFEF4444),
                    iconTint = Color(0xFFEF4444),
                    testTag = "menu_logout",
                    onClick = { viewModel.logout() }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms & Conditions", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Welcome to Edify Chef & FoodKing Kitchen Operating System. Kitchen staff must adhere to food preparation hygiene, allergen notification, and prompt queue dispatch standards. All customer orders are logged and monitored for service quality.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("OK", color = FoodKingPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Edify Chef & FoodKing respects your privacy. Staff credentials, branch assignments, and order processing logs are encrypted and used solely for kitchen workflow operations.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("OK", color = FoodKingPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun ProfileMenuItem(
    title: String,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit,
    textColor: Color = TextPrimary,
    iconTint: Color = Color(0xFF64748B)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Open",
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(18.dp)
        )
    }
}
