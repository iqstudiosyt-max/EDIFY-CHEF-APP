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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FoodKingTopBar
import com.example.ui.components.OrderCard
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderLight
import com.example.ui.theme.FoodKingPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.KitchenViewModel

@Composable
fun OrderHistoryScreen(
    viewModel: KitchenViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.historyOrders.collectAsState()
    val expandedOrderIds by viewModel.expandedOrderIds.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedDateFilter by viewModel.selectedDateFilter.collectAsState()

    var filterMenuExpanded by remember { mutableStateOf(false) }

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

        // Title Header with Date Filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Order History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Box {
                Row(
                    modifier = Modifier
                        .testTag("history_date_filter")
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                        .clickable { filterMenuExpanded = true }
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDateFilter,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Select Date",
                        tint = FoodKingPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                DropdownMenu(
                    expanded = filterMenuExpanded,
                    onDismissRequest = { filterMenuExpanded = false }
                ) {
                    listOf("Today", "This Week", "Last 30 Days", "This Year").forEach { filter ->
                        DropdownMenuItem(
                            text = { Text(filter, fontSize = 13.sp) },
                            onClick = {
                                viewModel.setDateFilter(filter)
                                filterMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Orders List
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No past completed orders found.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    OrderCard(
                        order = order,
                        isExpanded = expandedOrderIds.contains(order.id),
                        onToggleExpand = { viewModel.toggleOrderExpanded(order.id) },
                        onAdvanceStatus = { /* Already completed */ }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
