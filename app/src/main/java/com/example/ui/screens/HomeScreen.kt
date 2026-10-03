package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CollaborationActivityEntity
import com.example.model.TextileProjectEntity
import com.example.model.UserRole
import com.example.ui.AppTab
import com.example.ui.components.LuxurySectionHeader
import com.example.ui.components.ResourceUtils
import com.example.ui.components.SyncStatusChip
import com.example.ui.components.UserRoleChip
import com.example.ui.components.ViralMeter

@Composable
fun HomeScreen(
    projects: List<TextileProjectEntity>,
    selectedProject: TextileProjectEntity?,
    currentRole: UserRole,
    isOffline: Boolean,
    activities: List<CollaborationActivityEntity>,
    onSelectProject: (TextileProjectEntity) -> Unit,
    onNavigateTab: (AppTab) -> Unit,
    onToggleOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Studio Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF261D45),
                                    Color(0xFF141226)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TEXTILE STUDIO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.8.sp,
                                    color = Color(0xFFE0A93B)
                                )
                                Text(
                                    text = "AI Photo, Reel & Ads Engine",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                UserRoleChip(
                                    role = currentRole,
                                    onClick = { onNavigateTab(AppTab.TEAM) }
                                )
                                IconButton(
                                    onClick = onToggleOffline,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isOffline) Color(0xFFE71D36).copy(alpha = 0.2f)
                                            else Color(0xFF2EC4B6).copy(alpha = 0.2f)
                                        )
                                        .testTag("offline_toggle_btn")
                                ) {
                                    Icon(
                                        imageVector = if (isOffline) Icons.Default.WifiOff else Icons.Default.CloudDone,
                                        contentDescription = "Toggle Offline",
                                        tint = if (isOffline) Color(0xFFE71D36) else Color(0xFF2EC4B6),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Growth summary metrics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricPill(
                                label = "Total Views",
                                value = "248.6K",
                                color = Color(0xFF6C63FF),
                                modifier = Modifier.weight(1f)
                            )
                            MetricPill(
                                label = "Avg ROAS",
                                value = "4.8x",
                                color = Color(0xFFE0A93B),
                                modifier = Modifier.weight(1f)
                            )
                            MetricPill(
                                label = "Active Ads",
                                value = "2 Live",
                                color = Color(0xFF2EC4B6),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Quick Studio Tools
        item {
            LuxurySectionHeader(
                title = "Creator Tools",
                subtitle = "Photo backdrop removal, reels, publishing & ads"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ToolActionCard(
                    title = "Photo Studio",
                    subtitle = "BG Remove & Enhancer",
                    icon = Icons.Default.Image,
                    accentColor = Color(0xFFE0A93B),
                    modifier = Modifier.weight(1f),
                    testTag = "quick_tool_photo_studio",
                    onClick = { onNavigateTab(AppTab.STUDIO) }
                )
                ToolActionCard(
                    title = "Reel Creator",
                    subtitle = "Photo-to-Video & Viral",
                    icon = Icons.Default.Movie,
                    accentColor = Color(0xFF9E2A67),
                    modifier = Modifier.weight(1f),
                    testTag = "quick_tool_reels",
                    onClick = { onNavigateTab(AppTab.REELS) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ToolActionCard(
                    title = "Multi-Publish",
                    subtitle = "IG, YT, X with Schedule",
                    icon = Icons.Default.Share,
                    accentColor = Color(0xFF00B4D8),
                    modifier = Modifier.weight(1f),
                    testTag = "quick_tool_publish",
                    onClick = { onNavigateTab(AppTab.PUBLISH) }
                )
                ToolActionCard(
                    title = "Ads Manager",
                    subtitle = "Automated ROAS Campaigns",
                    icon = Icons.Default.Campaign,
                    accentColor = Color(0xFF2EC4B6),
                    modifier = Modifier.weight(1f),
                    testTag = "quick_tool_ads",
                    onClick = { onNavigateTab(AppTab.ADS) }
                )
            }
        }

        // Active Textile Projects
        item {
            LuxurySectionHeader(
                title = "Textile Creations & Catalogs",
                subtitle = "Select an item to edit background, convert to video, or publish",
                actionLabel = "Pro Studio",
                onActionClick = { onNavigateTab(AppTab.SUBSCRIPTION) }
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(projects) { project ->
                    val isSelected = project.id == selectedProject?.id
                    val drawableId = ResourceUtils.getDrawableId(context, project.drawableResName)

                    Card(
                        modifier = Modifier
                            .width(230.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFFE0A93B) else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { onSelectProject(project) }
                            .testTag("project_card_${project.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = drawableId),
                                    contentDescription = project.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.7f)
                                                ),
                                                startY = 80f
                                            )
                                        )
                                )
                                // Sync Status Badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                ) {
                                    SyncStatusChip(status = project.syncStatus)
                                }

                                // Category & Price Badge
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = project.category.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE0A93B)
                                    )
                                    Text(
                                        text = "₹${project.price.toInt()}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }

                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = project.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${project.fabricType} • Backdrop: ${project.backgroundPreset}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ViralMeter(score = project.viralProbability)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFE0A93B).copy(alpha = 0.15f),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                onSelectProject(project)
                                                onNavigateTab(AppTab.STUDIO)
                                            }
                                    ) {
                                        Text(
                                            text = "Edit Photo",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE0A93B),
                                            modifier = Modifier.padding(vertical = 6.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF9E2A67).copy(alpha = 0.15f),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                onSelectProject(project)
                                                onNavigateTab(AppTab.REELS)
                                            }
                                    ) {
                                        Text(
                                            text = "Make Reel",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE27396),
                                            modifier = Modifier.padding(vertical = 6.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Real-Time Team Collaboration Feed
        item {
            LuxurySectionHeader(
                title = "Team Collaboration & Live Sync",
                subtitle = "Recent changes made by editors and marketers",
                actionLabel = "Manage Team",
                onActionClick = { onNavigateTab(AppTab.TEAM) }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    activities.take(3).forEach { activity ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6C63FF).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = activity.memberName.first().toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6C63FF)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${activity.memberName}: ${activity.actionText}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = activity.projectTitle,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
private fun ToolActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
