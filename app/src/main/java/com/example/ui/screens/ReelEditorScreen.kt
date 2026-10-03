package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackgroundPreset
import com.example.model.TextileProjectEntity
import com.example.model.VideoAnimationType
import com.example.ui.AppTab
import com.example.ui.components.LuxurySectionHeader
import com.example.ui.components.ResourceUtils
import com.example.ui.components.ViralMeter

@Composable
fun ReelEditorScreen(
    selectedProject: TextileProjectEntity?,
    isPlaying: Boolean,
    onTogglePlayback: () -> Unit,
    onUpdateReelSettings: (VideoAnimationType, String, String) -> Unit,
    onNavigateTab: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (selectedProject == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Select a textile project to create video reels.", color = MaterialTheme.colorScheme.onSurface)
        }
        return
    }

    var selectedAnimation by remember(selectedProject.id) {
        mutableStateOf(
            try {
                VideoAnimationType.valueOf(selectedProject.reelAnimation)
            } catch (e: Exception) {
                VideoAnimationType.KEN_BURNS_ZOOM
            }
        )
    }

    var selectedAudio by remember(selectedProject.id) { mutableStateOf(selectedProject.reelAudioTrack) }
    var hookText by remember(selectedProject.id) { mutableStateOf(selectedProject.reelHookText) }

    val trendingAudios = listOf(
        "Trending Sitar Lo-Fi Beats (Viral on IG)",
        "Royal Shehnai x Festive Bass Drop",
        "Bollywood Ethnic Acoustic (Top Reels)",
        "Handloom Loom Symphony Chill"
    )

    val drawableId = ResourceUtils.getDrawableId(context, selectedProject.drawableResName)

    // Infinite transitions for video animation preview
    val infiniteTransition = rememberInfiniteTransition(label = "reel_animation")
    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isPlaying && selectedAnimation == VideoAnimationType.KEN_BURNS_ZOOM) 1.18f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val animatedOffsetY by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = if (isPlaying && selectedAnimation == VideoAnimationType.SMOOTH_PAN) 15f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    val animatedRotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = if (isPlaying && selectedAnimation == VideoAnimationType.SPIN_360) 3f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Reel Header
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
                            text = "PHOTO-TO-VIDEO REEL CONVERTER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9E2A67)
                        )
                        Text(
                            text = "Viral Trend Studio (9:16 Format)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    ViralMeter(score = selectedProject.viralProbability)
                }
            }
        }

        // 9:16 Vertical Video Preview Player
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(390.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(220.dp)
                        .height(390.dp)
                        .testTag("reel_vertical_player"),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Video background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF261D45),
                                            Color(0xFF0D0B1C)
                                        )
                                    )
                                )
                        )

                        // Animated textile photo layer
                        Image(
                            painter = painterResource(id = drawableId),
                            contentDescription = "Reel Animated Frame",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                                .graphicsLayer(
                                    scaleX = animatedScale,
                                    scaleY = animatedScale,
                                    translationY = animatedOffsetY,
                                    rotationZ = animatedRotation
                                ),
                            contentScale = ContentScale.Fit
                        )

                        // Floating Hook Banner (Top of Reel)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 12.dp)
                                .align(Alignment.TopCenter)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.Black.copy(alpha = 0.8f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0A93B).copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = hookText,
                                    color = Color(0xFFE0A93B),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    maxLines = 2
                                )
                            }
                        }

                        // Floating Price Sticker & Audio Tag (Bottom of Reel)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                    )
                                )
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFE0A93B)
                                ) {
                                    Text(
                                        text = "₹${selectedProject.price.toInt()}",
                                        color = Color.Black,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = selectedProject.category,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = selectedAudio,
                                    color = Color.LightGray,
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        // Play/Pause center overlay button
                        IconButton(
                            onClick = onTogglePlayback,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .testTag("reel_play_pause_btn")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Progress indicator at very bottom
                        LinearProgressIndicator(
                            progress = { if (isPlaying) (animatedScale - 1.0f) / 0.18f else 0.5f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .align(Alignment.BottomCenter),
                            color = Color(0xFFE0A93B),
                            trackColor = Color.Transparent
                        )
                    }
                }
            }
        }

        // Photo-To-Video Animation Modes
        item {
            LuxurySectionHeader(
                title = "Motion Effects (Photo to Video)",
                subtitle = "Select camera motion to animate static fabric shots"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(VideoAnimationType.values()) { anim ->
                    val isSel = anim == selectedAnimation
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) Color(0xFF9E2A67) else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSel) Color(0xFFE27396) else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedAnimation = anim
                                onUpdateReelSettings(anim, selectedAudio, hookText)
                            }
                            .testTag("motion_type_${anim.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isSel) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                text = anim.title,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Viral Hook Text Input
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
                        Text(
                            text = "Viral Hook Banner (First 2 Seconds)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Boosts Watch Time",
                            fontSize = 11.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedTextField(
                        value = hookText,
                        onValueChange = {
                            hookText = it
                            onUpdateReelSettings(selectedAnimation, selectedAudio, it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hook_text_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        placeholder = { Text("Catchy hook: e.g. Pure Handloom Silk at Direct Weaver Price!") }
                    )
                }
            }
        }

        // Trending Audio Selector
        item {
            LuxurySectionHeader(
                title = "Trending Soundtracks",
                subtitle = "Reels with trending audio receive 3.4x more reach"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    trendingAudios.forEach { audio ->
                        val isAudioSel = audio == selectedAudio
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isAudioSel) Color(0xFF6C63FF).copy(alpha = 0.15f) else Color.Transparent)
                                .clickable {
                                    selectedAudio = audio
                                    onUpdateReelSettings(selectedAnimation, audio, hookText)
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = if (isAudioSel) Color(0xFF6C63FF) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = audio,
                                    fontSize = 12.sp,
                                    fontWeight = if (isAudioSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isAudioSel) Color(0xFF6C63FF) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (isAudioSel) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF6C63FF), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Direct Post Option ("OR SIDHE POST KARNE KA OPTION DE")
        item {
            Button(
                onClick = { onNavigateTab(AppTab.PUBLISH) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("direct_post_reel_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E2A67))
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Direct Post Reel to Social Channels",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}
