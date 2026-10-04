package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KitchenOrder
import com.example.data.model.OrderStatus
import com.example.data.model.OrderType
import com.example.ui.theme.BorderLight
import com.example.ui.theme.FoodKingPrimary
import com.example.ui.theme.StatusConfirmedBg
import com.example.ui.theme.StatusConfirmedText
import com.example.ui.theme.StatusDoneBg
import com.example.ui.theme.StatusDoneText
import com.example.ui.theme.StatusPreparingBg
import com.example.ui.theme.StatusPreparingText
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TypeDineInBg
import com.example.ui.theme.TypeDineInText
import com.example.ui.theme.TypeOnlineDeliveryBg
import com.example.ui.theme.TypeOnlineDeliveryText
import com.example.ui.theme.TypeTakeawayBg
import com.example.ui.theme.TypeTakeawayText

@Composable
fun OrderCard(
    order: KitchenOrder,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onAdvanceStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (typeTextColor, typeBgColor, typeIcon) = when (order.orderType) {
        OrderType.ONLINE_DELIVERY -> Triple(TypeOnlineDeliveryText, TypeOnlineDeliveryBg, Icons.Default.DeliveryDining)
        OrderType.DINE_IN -> Triple(TypeDineInText, TypeDineInBg, Icons.Default.DinnerDining)
        OrderType.TAKEAWAY -> Triple(TypeTakeawayText, TypeTakeawayBg, Icons.Default.ShoppingBag)
    }

    val (statusTextColor, statusBgColor) = when (order.status) {
        OrderStatus.CONFIRMED -> Pair(StatusConfirmedText, StatusConfirmedBg)
        OrderStatus.PREPARING -> Pair(StatusPreparingText, StatusPreparingBg)
        OrderStatus.DONE -> Pair(StatusDoneText, StatusDoneBg)
        OrderStatus.CANCELLED -> Pair(Color(0xFF64748B), Color(0xFFF1F5F9))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Order ID Tag
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(typeBgColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = typeIcon,
                        contentDescription = order.orderType.label,
                        tint = typeTextColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = order.orderNumber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = typeTextColor
                    )
                }

                // Status Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusBgColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.status.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Order Metadata & Expand toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    when (order.orderType) {
                        OrderType.ONLINE_DELIVERY -> {
                            order.scheduleTime?.let {
                                Row {
                                    Text(
                                        text = "Schedule: ",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = it,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                        OrderType.DINE_IN -> {
                            Row {
                                Text(
                                    text = "Table No: ",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = order.tableNo ?: "-",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row {
                                Text(
                                    text = "Token No: ",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = order.tokenNo ?: "-",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                        OrderType.TAKEAWAY -> {
                            Row {
                                Text(
                                    text = "Token No: ",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = order.tokenNo ?: "-",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Row {
                        Text(
                            text = "Type: ",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = order.orderType.label,
                            fontSize = 12.sp,
                            color = typeTextColor,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = order.orderDate,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF8FAFC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = FoodKingPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Expanded Item List and Action Button
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(8.dp))

                    order.items.forEach { item ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                text = "${item.quantity}x  ${item.name}",
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
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Button according to status
                    when (order.status) {
                        OrderStatus.CONFIRMED -> {
                            Button(
                                onClick = onAdvanceStatus,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("start_preparing_button_${order.id}"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FoodKingPrimary,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "Start Preparing",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        OrderStatus.PREPARING -> {
                            Button(
                                onClick = onAdvanceStatus,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("mark_done_button_${order.id}"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SuccessGreen,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "Mark Done",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        OrderStatus.DONE -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF0FDF4))
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Order Completed",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            }
                        }
                        OrderStatus.CANCELLED -> {}
                    }
                }
            }
        }
    }
}
