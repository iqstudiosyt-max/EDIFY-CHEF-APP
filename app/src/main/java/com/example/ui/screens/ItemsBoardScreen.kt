package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FoodKingTopBar
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.KitchenViewModel

@Composable
fun ItemsBoardScreen(
    viewModel: KitchenViewModel,
    modifier: Modifier = Modifier
) {
    val itemsBoard by viewModel.itemsBoard.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Top Bar
        FoodKingTopBar(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { viewModel.setLanguage(it) },
            onRefresh = { viewModel.refreshKitchenData() },
            modifier = Modifier.background(Color.White)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Title Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Items Board",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Aggregated dishes queued for preparation",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // List of Items
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(itemsBoard, key = { it.id }) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (item.size != null) {
                                Text(
                                    text = "Size: ${item.size}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            if (!item.extras.isNullOrEmpty()) {
                                Text(
                                    text = "Extras: ${item.extras.joinToString(", ")}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Black Circular Counter Badge
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${item.count}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
