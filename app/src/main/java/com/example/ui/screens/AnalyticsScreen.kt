package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TextileProjectEntity
import com.example.ui.AppTab
import com.example.ui.components.LuxurySectionHeader

@Composable
fun AnalyticsScreen(
    projects: List<TextileProjectEntity>,
    isPremiumUnlocked: Boolean,
    onNavigateTab: (AppTab) -> Unit,
    onUnlockPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Analytics Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PERFORMANCE ANALYTICS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6C63FF)
                        )
                        Text(
                            text = "Textile Growth & Engagement",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF6C63FF).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Last 30 Days",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6C63FF),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 4 Key Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Total Reel Views",
                    value = "248.6K",
                    change = "+38.4% vs last mo",
                    color = Color(0xFF6C63FF),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Avg ROAS (Ads)",
                    value = "4.8x",
                    change = "₹4.8 return / ₹1 spend",
                    color = Color(0xFF2EC4B6),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "WhatsApp Orders",
                    value = "342 DMs",
                    change = "7.8% conversion",
                    color = Color(0xFFE0A93B),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Catalog Reach",
                    value = "92.1K",
                    change = "Organic + Sponsored",
                    color = Color(0xFFE27396),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Platform Traffic Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Audience Reach by Social Channel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    ChannelProgressRow(channel = "Instagram Reels & Feed", percent = 62, color = Color(0xFFE1306C))
                    ChannelProgressRow(channel = "YouTube Shorts", percent = 26, color = Color(0xFFFF0000))
                    ChannelProgressRow(channel = "Twitter / X Catalog Drops", percent = 12, color = Color(0xFF1DA1F2))
                }
            }
        }

        // Top Performing Textile Products
        item {
            LuxurySectionHeader(
                title = "Top Performing Textile Products",
                subtitle = "Ranked by algorithm engagement and inquiry conversions"
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                projects.forEachIndexed { index, proj ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE0A93B).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "#${index + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE0A93B)
                                    )
                                }
                                Column {
                                    Text(
                                        text = proj.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${proj.category} • ₹${proj.price.toInt()}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = "Viral Score: ${proj.viralProbability}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }
        }

        // Advanced Marketing Insights (Subscription Gated Feature!)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pro_insights_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPremiumUnlocked) Color(0xFF18182E) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isPremiumUnlocked) Color(0xFFE0A93B) else Color(0xFF6C63FF).copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isPremiumUnlocked) Icons.Default.AutoAwesome else Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFE0A93B),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Advanced AI Marketing Insights",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isPremiumUnlocked) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFE0A93B).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isPremiumUnlocked) "UNLOCKED" else "PRO ONLY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPremiumUnlocked) Color(0xFF10B981) else Color(0xFFE0A93B),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    if (isPremiumUnlocked) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            InsightBullet(
                                title = "High Bridal Silk Intent Detected",
                                detail = "Banarasi silk searches spike 3.8x between Thursday & Sunday evening. Shift 65% of Meta Ad budget to 7 PM - 11 PM slots."
                            )
                            InsightBullet(
                                title = "Photo Backdrop Affinity",
                                detail = "Reels with 'Royal Palace' and 'Italian Marble' backdrops generated 42% higher inquiry retention than solid white backgrounds."
                            )
                            InsightBullet(
                                title = "Price Tag Optimization",
                                detail = "Adding explicit price badges on the Reel cover decreased unqualified comments and raised checkout readiness by +55%."
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Unlock algorithm prediction metrics, competitor price benchmarks, and automated ad budget re-allocation.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = onUnlockPremium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("unlock_pro_insights_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0A93B))
                            ) {
                                Icon(Icons.Default.Diamond, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Unlock Pro Marketing Insights", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    change: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = change, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun ChannelProgressRow(
    channel: String,
    percent: Int,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = channel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "$percent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun InsightBullet(
    title: String,
    detail: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0A93B))
        )
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE0A93B))
            Text(text = detail, fontSize = 11.sp, color = Color.LightGray, lineHeight = 16.sp)
        }
    }
}
