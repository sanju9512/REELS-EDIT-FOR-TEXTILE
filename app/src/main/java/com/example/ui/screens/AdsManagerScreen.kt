package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdCampaignEntity
import com.example.model.TextileProjectEntity
import com.example.model.UserRole
import com.example.ui.components.LuxurySectionHeader

@Composable
fun AdsManagerScreen(
    campaigns: List<AdCampaignEntity>,
    selectedProject: TextileProjectEntity?,
    currentRole: UserRole,
    onCreateCampaign: (String, String, String, Double, String) -> Unit,
    onToggleCampaign: (AdCampaignEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var campaignName by remember(selectedProject?.id) {
        mutableStateOf(
            if (selectedProject != null) "Festive Sales - ${selectedProject.title}" else "Diwali Silk & Kurti Festive Blast"
        )
    }
    var selectedPlatform by remember { mutableStateOf("Meta Ads (IG Reels & FB)") }
    var selectedObjective by remember { mutableStateOf("WhatsApp Direct Orders") }
    var dailyBudget by remember { mutableDoubleStateOf(850.0) }
    var selectedAudience by remember {
        mutableStateOf("Women 22-55 • Silk & Festive Wear Buyers • Top Tier 1 & 2 Metro Cities")
    }

    // Predictive ROI calculations
    val estimatedImpressions = (dailyBudget * 28).toInt()
    val estimatedClicks = (dailyBudget * 1.5).toInt()
    val estimatedRoas = 4.4

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Ads Header
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
                            text = "AUTOMATED ADS ENGINE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2EC4B6)
                        )
                        Text(
                            text = "Meta & Google Ads Automation",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF2EC4B6).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "ROAS AI Engine",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2EC4B6),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Create Automated Campaign Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_ad_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2EC4B6).copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Launch Automated Campaign",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = campaignName,
                        onValueChange = { campaignName = it },
                        label = { Text("Campaign Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    // Platform selector
                    Text("Ad Network", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "Meta Ads (IG Reels & FB)",
                            "Google & YouTube Shorts"
                        ).forEach { plat ->
                            val isSel = selectedPlatform == plat
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) Color(0xFF2EC4B6) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedPlatform = plat }
                            ) {
                                Text(
                                    text = plat,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    // Objective selector
                    Text("Campaign Objective", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "WhatsApp Direct Orders",
                            "Website Catalog Sales"
                        ).forEach { obj ->
                            val isSel = selectedObjective == obj
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) Color(0xFFE0A93B) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedObjective = obj }
                            ) {
                                Text(
                                    text = obj,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    // Budget Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Daily Ad Budget", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("₹${dailyBudget.toInt()} / day", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2EC4B6))
                        }
                        Slider(
                            value = dailyBudget.toFloat(),
                            onValueChange = { dailyBudget = it.toDouble() },
                            valueRange = 300f..4000f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF2EC4B6),
                                activeTrackColor = Color(0xFF2EC4B6)
                            ),
                            modifier = Modifier.testTag("ad_budget_slider")
                        )
                    }

                    // Forecast Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF2EC4B6).copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2EC4B6).copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "$estimatedImpressions+", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2EC4B6))
                                Text(text = "Daily Reach", fontSize = 10.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "$estimatedClicks", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE0A93B))
                                Text(text = "Inquiries / DMs", fontSize = 10.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${estimatedRoas}x", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                Text(text = "Projected ROAS", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }

                    // Launch Campaign Button
                    Button(
                        onClick = {
                            onCreateCampaign(
                                campaignName,
                                selectedPlatform,
                                selectedObjective,
                                dailyBudget,
                                selectedAudience
                            )
                            Toast.makeText(context, "Campaign '$campaignName' launched!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("launch_ad_campaign_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EC4B6))
                    ) {
                        Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Auto-Launch Campaign Now",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // Live Campaigns List
        item {
            LuxurySectionHeader(
                title = "Live & Automated Campaigns",
                subtitle = "Manage active ad budgets and monitor real-time conversions"
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                campaigns.forEach { campaign ->
                    val isActive = campaign.status == "ACTIVE"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = campaign.campaignName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${campaign.platform} • ${campaign.objective}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isActive) Color(0xFF10B981).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f),
                                    modifier = Modifier.clickable { onToggleCampaign(campaign) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = if (isActive) Color(0xFF10B981) else Color.Gray,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = campaign.status,
                                            color = if (isActive) Color(0xFF10B981) else Color.Gray,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Budget: ₹${campaign.dailyBudgetInr.toInt()}/day", fontSize = 11.sp, color = Color.LightGray)
                                Text("Clicks: ${campaign.clicks}", fontSize = 11.sp, color = Color.LightGray)
                                Text("ROAS: ${campaign.roas}x", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                        }
                    }
                }
            }
        }
    }
}
