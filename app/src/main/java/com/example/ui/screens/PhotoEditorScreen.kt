package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackgroundPreset
import com.example.model.TextileProjectEntity
import com.example.model.UserRole
import com.example.ui.AppTab
import com.example.ui.components.LuxurySectionHeader
import com.example.ui.components.ResourceUtils
import com.example.ui.components.SyncStatusChip
import com.example.ui.components.ViralMeter

@Composable
fun PhotoEditorScreen(
    selectedProject: TextileProjectEntity?,
    allProjects: List<TextileProjectEntity>,
    isAiGenerating: Boolean,
    currentRole: UserRole,
    onSelectProject: (TextileProjectEntity) -> Unit,
    onUpdateBackground: (BackgroundPreset) -> Unit,
    onUpdateEnhancements: (Float, Float, Float) -> Unit,
    onGenerateAiDescription: (String) -> Unit,
    onNavigateTab: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (selectedProject == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No textile product selected. Choose one from Home.", color = MaterialTheme.colorScheme.onSurface)
        }
        return
    }

    val currentPreset = try {
        BackgroundPreset.valueOf(selectedProject.backgroundPreset)
    } catch (e: Exception) {
        BackgroundPreset.STUDIO_WHITE
    }

    var textureClarity by remember(selectedProject.id) { mutableFloatStateOf(selectedProject.textureClarity) }
    var fabricShine by remember(selectedProject.id) { mutableFloatStateOf(selectedProject.fabricShine) }
    var colorVibrancy by remember(selectedProject.id) { mutableFloatStateOf(selectedProject.colorVibrancy) }
    var showOriginalBg by remember { mutableStateOf(false) }
    var selectedTone by remember { mutableStateOf("Festive Luxury") }

    val drawableId = ResourceUtils.getDrawableId(context, selectedProject.drawableResName)

    // Dynamic ColorMatrix for live fabric texture & lustre enhancement preview
    val colorMatrix = remember(textureClarity, fabricShine, colorVibrancy) {
        val contrast = 0.8f + (textureClarity * 0.4f)
        val brightness = (fabricShine - 0.5f) * 40f
        val sat = 0.8f + (colorVibrancy * 0.5f)

        val invSat = 1 - sat
        val r = 0.213f * invSat
        val g = 0.715f * invSat
        val b = 0.072f * invSat

        ColorMatrix(
            floatArrayOf(
                (r + sat) * contrast, g * contrast, b * contrast, 0f, brightness,
                r * contrast, (g + sat) * contrast, b * contrast, 0f, brightness,
                r * contrast, g * contrast, (b + sat) * contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Switcher Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "EDITING PRODUCT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE0A93B)
                        )
                        Text(
                            text = selectedProject.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                    SyncStatusChip(status = selectedProject.syncStatus)
                }
            }
        }

        // Live Photo Canvas with Instant Background Remover & Fabric Texture
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .testTag("photo_editor_canvas"),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Studio Backdrop Layer
                    if (showOriginalBg) {
                        // Original backdrop simulation
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF2A2A38))
                        )
                    } else {
                        // AI Studio Backdrop
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(currentPreset.previewGradientStart),
                                            Color(currentPreset.previewGradientEnd)
                                        )
                                    )
                                )
                        )
                    }

                    // Textile Product Layer with live fabric enhancement filters
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = selectedProject.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (showOriginalBg) 0.dp else 16.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.colorMatrix(colorMatrix)
                    )

                    // Overlay Controls (Before/After toggle & preset badge)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.65f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showOriginalBg = !showOriginalBg }
                                .testTag("before_after_toggle_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "Toggle Before/After",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (showOriginalBg) "Original Shot" else "AI Studio Backdrop",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE0A93B),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "₹${selectedProject.price.toInt()}",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Bottom info strip
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                )
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${selectedProject.fabricType} • ${currentPreset.title}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            ViralMeter(score = selectedProject.viralProbability)
                        }
                    }
                }
            }
        }

        // Instant Studio Backdrops Selector
        item {
            LuxurySectionHeader(
                title = "Studio Backdrops (Instant BG Remover)",
                subtitle = "Replaces background with luxury studio environments"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(BackgroundPreset.values()) { preset ->
                    val isCurrent = preset == currentPreset
                    Card(
                        modifier = Modifier
                            .width(135.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) Color(0xFFE0A93B) else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onUpdateBackground(preset) }
                            .testTag("backdrop_preset_${preset.name}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(preset.previewGradientStart),
                                                Color(preset.previewGradientEnd)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE0A93B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = preset.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Text(
                                    text = preset.subtitle,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Fabric Enhancer Sliders
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
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
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Enhance Fabric",
                                tint = Color(0xFFE0A93B),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Fabric Texture & Lustre Enhancer",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = {
                                onUpdateEnhancements(textureClarity, fabricShine, colorVibrancy)
                                Toast.makeText(context, "Enhancements saved!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0A93B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("apply_enhancements_button"),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Text("Apply", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Texture Clarity Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Weave & Thread Clarity", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("${(textureClarity * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE0A93B))
                        }
                        Slider(
                            value = textureClarity,
                            onValueChange = { textureClarity = it },
                            valueRange = 0.4f..1.2f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFE0A93B),
                                activeTrackColor = Color(0xFFE0A93B)
                            ),
                            modifier = Modifier.testTag("slider_texture_clarity")
                        )
                    }

                    // Silk Shine & Zari Lustre
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Zari Sheen & Silk Lustre", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("${(fabricShine * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE0A93B))
                        }
                        Slider(
                            value = fabricShine,
                            onValueChange = { fabricShine = it },
                            valueRange = 0.3f..1.2f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFE0A93B),
                                activeTrackColor = Color(0xFFE0A93B)
                            ),
                            modifier = Modifier.testTag("slider_fabric_shine")
                        )
                    }

                    // Color Dye Vibrancy
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Dye Vibrancy & Contrast", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("${(colorVibrancy * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE0A93B))
                        }
                        Slider(
                            value = colorVibrancy,
                            onValueChange = { colorVibrancy = it },
                            valueRange = 0.5f..1.3f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFE0A93B),
                                activeTrackColor = Color(0xFFE0A93B)
                            ),
                            modifier = Modifier.testTag("slider_color_vibrancy")
                        )
                    }
                }
            }
        }

        // AI-Based Product Description Generator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_description_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6C63FF).copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
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
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini AI",
                                tint = Color(0xFF6C63FF),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "AI Product Description & Tags",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = { onGenerateAiDescription(selectedTone) },
                            enabled = !isAiGenerating,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("generate_ai_desc_btn")
                        ) {
                            if (isAiGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Writing...", fontSize = 11.sp, color = Color.White)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate AI", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }

                    // Tone selector chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Festive Luxury", "Bridal Exclusive", "Hinglish Viral", "Wholesale").forEach { tone ->
                            val isSel = selectedTone == tone
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) Color(0xFF6C63FF) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedTone = tone }
                            ) {
                                Text(
                                    text = tone,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Generated Copy Display
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = selectedProject.aiDescription.ifBlank {
                                    "Tap 'Generate AI' to create a compelling, sales-driven textile product story with authentic weave details and viral hashtags."
                                },
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )

                            Text(
                                text = selectedProject.hashtags,
                                fontSize = 11.sp,
                                color = Color(0xFF6C63FF),
                                fontWeight = FontWeight.Medium
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Textile Description", "${selectedProject.aiDescription}\n\n${selectedProject.hashtags}")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Copied description & tags!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Description",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons: Make Reel & Publish
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigateTab(AppTab.REELS) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("nav_to_reels_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF9E2A67))
                ) {
                    Icon(imageVector = Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Make Reel", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = { onNavigateTab(AppTab.PUBLISH) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("nav_to_publish_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8))
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share / Post", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                }
            }
        }
    }
}
