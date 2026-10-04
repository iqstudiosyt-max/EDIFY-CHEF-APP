package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HourlyStat
import com.example.ui.theme.ChartBarActive
import com.example.ui.theme.TextMuted

@Composable
fun OrderStatsChart(
    stats: List<HourlyStat>,
    onStatClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxOrders = (stats.maxOfOrNull { it.orders } ?: 25).coerceAtLeast(25)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        stats.forEach { stat ->
            val isHighlighted = stat.isHighlighted
            val interactionSource = remember { MutableInteractionSource() }

            val barHeightFraction = (stat.orders.toFloat() / maxOrders).coerceIn(0.12f, 1f)
            val animatedHeightFraction by animateFloatAsState(
                targetValue = barHeightFraction,
                animationSpec = tween(durationMillis = 600),
                label = "barHeight"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onStatClick(stat.timeLabel) }
            ) {
                // Tooltip badge if highlighted
                if (isHighlighted) {
                    Box(
                        modifier = Modifier
                            .shadow(4.dp, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Orders",
                                fontSize = 9.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${stat.orders}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    // Spacer to align unhighlighted bars with identical total vertical space
                    Spacer(modifier = Modifier.height(34.dp))
                }

                // Vertical Bar
                Box(
                    modifier = Modifier
                        .width(7.dp)
                        .weight(1f, fill = false)
                        .height((110 * animatedHeightFraction).dp)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(if (isHighlighted) ChartBarActive else Color(0xFF4A85F6))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Time label
                Text(
                    text = stat.timeLabel,
                    fontSize = 9.sp,
                    color = if (isHighlighted) Color(0xFF1E293B) else TextMuted,
                    fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
