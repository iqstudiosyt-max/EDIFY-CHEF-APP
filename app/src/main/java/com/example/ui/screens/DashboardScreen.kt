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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
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
import com.example.ui.components.OrderStatsChart
import com.example.ui.components.StatsCard
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderLight
import com.example.ui.theme.FoodKingPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.KitchenViewModel

@Composable
fun DashboardScreen(
    viewModel: KitchenViewModel,
    modifier: Modifier = Modifier
) {
    val overview by viewModel.dashboardOverview.collectAsState()
    val hourlyStats by viewModel.hourlyStats.collectAsState()
    val popularItems by viewModel.popularItems.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedDateFilter by viewModel.selectedDateFilter.collectAsState()

    var filterMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        item {
            FoodKingTopBar(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { viewModel.setLanguage(it) },
                onRefresh = { viewModel.refreshKitchenData() },
                modifier = Modifier.background(Color.White)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Header with Date Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Box {
                        Row(
                            modifier = Modifier
                                .testTag("dashboard_date_filter")
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

                Spacer(modifier = Modifier.height(14.dp))

                // 2x2 Metric Cards Grid
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatsCard(
                        title = "Total Orders",
                        value = "${overview.totalOrders}",
                        icon = Icons.Default.ShoppingBag,
                        iconTint = Color(0xFF6366F1),
                        iconBgColor = Color(0xFFEEF2FF),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    StatsCard(
                        title = "Complete Orders",
                        value = "${overview.completeOrders}",
                        icon = Icons.Default.CheckCircle,
                        iconTint = Color(0xFFF59E0B),
                        iconBgColor = Color(0xFFFEF3C7),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    StatsCard(
                        title = "Preparing Orders",
                        value = "${overview.preparingOrders}",
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = FoodKingPrimary,
                        iconBgColor = Color(0xFFFFE4E6),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    StatsCard(
                        title = "Avg. Preparation Time",
                        value = "${overview.avgPrepTimeMin} Min",
                        icon = Icons.Default.Schedule,
                        iconTint = Color(0xFF0EA5E9),
                        iconBgColor = Color(0xFFE0F2FE),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Order Stats Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Order Stats",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White)
                            .border(1.dp, BorderLight, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "11 Aug - 10 Sep",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Date Range",
                            tint = FoodKingPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chart Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 14.dp)
                ) {
                    OrderStatsChart(
                        stats = hourlyStats,
                        onStatClick = { timeLabel ->
                            viewModel.selectHourlyStat(timeLabel)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Most Popular Items Header
                Text(
                    text = "Most Popular Items",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Popular Items Grid (2 columns)
                val chunkedItems = popularItems.chunked(2)
                chunkedItems.forEach { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Food icon / avatar thumbnail
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFFFF1F2)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fastfood,
                                            contentDescription = item.name,
                                            tint = FoodKingPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = item.name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = item.category,
                                            fontSize = 9.sp,
                                            color = Color(0xFF0284C7),
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = item.price,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
