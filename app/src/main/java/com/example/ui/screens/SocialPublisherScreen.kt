package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScheduledPostEntity
import com.example.model.TextileProjectEntity
import com.example.model.UserRole
import com.example.ui.components.LuxurySectionHeader
import com.example.ui.components.ResourceUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SocialPublisherScreen(
    selectedProject: TextileProjectEntity?,
    scheduledPosts: List<ScheduledPostEntity>,
    currentRole: UserRole,
    onPublishOrSchedule: (String, Boolean, Boolean, Boolean, Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (selectedProject == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Select a textile project to publish.", color = MaterialTheme.colorScheme.onSurface)
        }
        return
    }

    var caption by remember(selectedProject.id) {
        mutableStateOf(
            if (selectedProject.aiDescription.isNotBlank()) {
                "${selectedProject.aiDescription}\n\nPrice: ₹${selectedProject.price.toInt()}\nOrder via DM / WhatsApp!\n\n${selectedProject.hashtags}"
            } else {
                "✨ Pure ${selectedProject.fabricType} ${selectedProject.title}!\nHandloom luxury at direct artisan price ₹${selectedProject.price.toInt()}.\nDM or tap link in bio to order.\n\n${selectedProject.hashtags}"
            }
        )
    }

    // Individual ON/OFF Toggles for each platform as requested:
    // "JISME SHARE KARNA HAI USE ON KARNE KA OPTION OR JIS PLATFORM PAR SHARE NAHI KARNA HI USE OFF KARNE KA POTION BHI CHAHIYE"
    var instagramOn by remember { mutableStateOf(true) }
    var youtubeOn by remember { mutableStateOf(true) }
    var twitterOn by remember { mutableStateOf(false) }

    var isDelayedScheduling by remember { mutableStateOf(false) }
    var selectedDelayHours by remember { mutableLongStateOf(2L) }

    val drawableId = ResourceUtils.getDrawableId(context, selectedProject.drawableResName)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Publisher Header
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
                            text = "MULTI-PLATFORM BROADCAST",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00B4D8)
                        )
                        Text(
                            text = "Direct Social Publisher & Scheduler",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF00B4D8).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Instant & Delayed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00B4D8),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Platform Selection ON/OFF Toggles
        item {
            LuxurySectionHeader(
                title = "Target Channels (Turn ON / OFF)",
                subtitle = "Select exactly where your textile post or reel is published"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Instagram Toggle
                    PlatformToggleRow(
                        platformName = "Instagram (Reels & Feed)",
                        platformDetail = "@textilestudio_official • 42.8K Followers",
                        accentColor = Color(0xFFE1306C),
                        isChecked = instagramOn,
                        onCheckedChange = { instagramOn = it },
                        testTag = "toggle_instagram"
                    )

                    // YouTube Toggle
                    PlatformToggleRow(
                        platformName = "YouTube (Shorts & Community)",
                        platformDetail = "Textile Studio Artisans • 18.2K Subscribers",
                        accentColor = Color(0xFFFF0000),
                        isChecked = youtubeOn,
                        onCheckedChange = { youtubeOn = it },
                        testTag = "toggle_youtube"
                    )

                    // Twitter / X Toggle
                    PlatformToggleRow(
                        platformName = "Twitter / X (Product Drop)",
                        platformDetail = "@TextileCrafts • 9.4K Followers",
                        accentColor = Color(0xFF1DA1F2),
                        isChecked = twitterOn,
                        onCheckedChange = { twitterOn = it },
                        testTag = "toggle_twitter"
                    )
                }
            }
        }

        // Caption & Media Preview
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Post Caption & Hashtags",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = drawableId),
                            contentDescription = "Post Thumbnail",
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )

                        OutlinedTextField(
                            value = caption,
                            onValueChange = { caption = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("publisher_caption_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // Scheduling Options
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF00B4D8),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Schedule for Delayed Posting",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Switch(
                            checked = isDelayedScheduling,
                            onCheckedChange = { isDelayedScheduling = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00B4D8)),
                            modifier = Modifier.testTag("schedule_delay_switch")
                        )
                    }

                    if (isDelayedScheduling) {
                        Text(
                            text = "Peak Fashion Engagement Hours: 7:30 PM - 9:30 PM",
                            fontSize = 11.sp,
                            color = Color(0xFF2EC4B6)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(
                                2L to "In 2 Hours",
                                4L to "In 4 Hours (Best)",
                                8L to "Tonight 8 PM",
                                24L to "Tomorrow"
                            ).forEach { (hrs, label) ->
                                val isSel = selectedDelayHours == hrs
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSel) Color(0xFF00B4D8) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedDelayHours = hrs }
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(vertical = 7.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons: Direct Post vs Schedule
        item {
            val hasSelectedPlatform = instagramOn || youtubeOn || twitterOn

            Button(
                onClick = {
                    if (!hasSelectedPlatform) {
                        Toast.makeText(context, "Please turn ON at least one platform!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val targetEpoch = if (isDelayedScheduling) {
                        System.currentTimeMillis() + (selectedDelayHours * 3600000)
                    } else {
                        System.currentTimeMillis()
                    }
                    onPublishOrSchedule(
                        caption,
                        instagramOn,
                        youtubeOn,
                        twitterOn,
                        targetEpoch,
                        !isDelayedScheduling
                    )
                },
                enabled = hasSelectedPlatform,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("execute_publish_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDelayedScheduling) Color(0xFF6C63FF) else Color(0xFF00B4D8)
                )
            ) {
                Icon(
                    imageVector = if (isDelayedScheduling) Icons.Default.AccessTime else Icons.Default.Send,
                    contentDescription = null,
                    tint = if (isDelayedScheduling) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isDelayedScheduling) "Confirm Scheduled Release" else "Direct Post Now to Selected Channels",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isDelayedScheduling) Color.White else Color.Black
                )
            }
        }

        // Scheduled Posts List
        item {
            LuxurySectionHeader(
                title = "Scheduled Queue & Post History",
                subtitle = "${scheduledPosts.size} posts queued across social channels"
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                scheduledPosts.forEach { post ->
                    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                    val dateStr = dateFormat.format(Date(post.scheduledTimeEpoch))

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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = post.projectTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Scheduled: $dateStr",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    if (post.postToInstagram) Text("• IG", fontSize = 10.sp, color = Color(0xFFE1306C), fontWeight = FontWeight.Bold)
                                    if (post.postToYouTube) Text("• YT", fontSize = 10.sp, color = Color(0xFFFF0000), fontWeight = FontWeight.Bold)
                                    if (post.postToTwitter) Text("• X", fontSize = 10.sp, color = Color(0xFF1DA1F2), fontWeight = FontWeight.Bold)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (post.isPublished) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF00B4D8).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (post.isPublished) "Published" else "Scheduled",
                                    color = if (post.isPublished) Color(0xFF10B981) else Color(0xFF00B4D8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
private fun PlatformToggleRow(
    platformName: String,
    platformDetail: String,
    accentColor: Color,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isChecked) accentColor.copy(alpha = 0.08f) else Color.Transparent)
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isChecked) accentColor else Color.Gray)
            )
            Column {
                Text(
                    text = platformName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = platformDetail,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentColor,
                checkedTrackColor = accentColor.copy(alpha = 0.35f)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
