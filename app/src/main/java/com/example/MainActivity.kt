package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ai.GeminiDescriptionService
import com.example.data.AppDatabase
import com.example.data.TextileRepository
import com.example.ui.AppTab
import com.example.ui.TextileViewModel
import com.example.ui.TextileViewModelFactory
import com.example.ui.components.OfflineBanner
import com.example.ui.components.UserRoleChip
import com.example.ui.screens.AdsManagerScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PhotoEditorScreen
import com.example.ui.screens.ReelEditorScreen
import com.example.ui.screens.SocialPublisherScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.TeamAndRoleScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: TextileViewModel by viewModels {
        val database = AppDatabase.getInstance(applicationContext)
        val repository = TextileRepository(database.textileDao(), GeminiDescriptionService())
        TextileViewModelFactory(repository)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val uiState by viewModel.uiState.collectAsState()
                val projects by viewModel.projects.collectAsState()
                val scheduledPosts by viewModel.scheduledPosts.collectAsState()
                val campaigns by viewModel.adCampaigns.collectAsState()
                val teamMembers by viewModel.teamMembers.collectAsState()
                val activities by viewModel.recentActivities.collectAsState()
                val isOffline by viewModel.isOfflineMode.collectAsState()
                val isSyncing by viewModel.isSyncing.collectAsState()

                var showMoreMenu by remember { mutableStateOf(false) }

                LaunchedEffect(uiState.statusMessage) {
                    uiState.statusMessage?.let { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        viewModel.dismissStatusMessage()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Column {
                            TopAppBar(
                                title = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE0A93B)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "Textile Studio",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = uiState.currentTab.title,
                                                fontSize = 11.sp,
                                                color = Color(0xFFE0A93B)
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    UserRoleChip(
                                        role = uiState.currentRole,
                                        onClick = { viewModel.selectTab(AppTab.TEAM) }
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Quick Sync / Status button
                                    IconButton(
                                        onClick = {
                                            if (isOffline) viewModel.toggleOfflineMode()
                                            else viewModel.triggerCloudSync()
                                        },
                                        modifier = Modifier.testTag("appbar_sync_btn")
                                    ) {
                                        if (isSyncing) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                color = Color(0xFF2EC4B6),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Icon(
                                                imageVector = if (isOffline) Icons.Default.WifiOff else Icons.Default.Sync,
                                                contentDescription = "Sync",
                                                tint = if (isOffline) Color(0xFFE71D36) else Color(0xFF2EC4B6),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    // Overflow menu for extra tabs (Analytics, Subscription, Team)
                                    Box {
                                        IconButton(
                                            onClick = { showMoreMenu = true },
                                            modifier = Modifier.testTag("overflow_menu_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MoreHoriz,
                                                contentDescription = "More Menu",
                                                tint = Color.White
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showMoreMenu,
                                            onDismissRequest = { showMoreMenu = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Performance Analytics") },
                                                leadingIcon = {
                                                    Icon(Icons.Default.Insights, contentDescription = null, tint = Color(0xFF6C63FF))
                                                },
                                                onClick = {
                                                    viewModel.selectTab(AppTab.ANALYTICS)
                                                    showMoreMenu = false
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Team & Roles") },
                                                leadingIcon = {
                                                    Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFF2EC4B6))
                                                },
                                                onClick = {
                                                    viewModel.selectTab(AppTab.TEAM)
                                                    showMoreMenu = false
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Subscription & Pro") },
                                                leadingIcon = {
                                                    Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFFE0A93B))
                                                },
                                                onClick = {
                                                    viewModel.selectTab(AppTab.SUBSCRIPTION)
                                                    showMoreMenu = false
                                                }
                                            )
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                )
                            )

                            // Offline Banner notification
                            OfflineBanner(
                                isOffline = isOffline,
                                isSyncing = isSyncing,
                                onSyncClick = { viewModel.triggerCloudSync() },
                                onToggleOffline = { viewModel.toggleOfflineMode() }
                            )
                        }
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp
                        ) {
                            val mainTabs = listOf(
                                AppTab.HOME,
                                AppTab.STUDIO,
                                AppTab.REELS,
                                AppTab.PUBLISH,
                                AppTab.ADS
                            )

                            mainTabs.forEach { tab ->
                                val isSelected = uiState.currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { viewModel.selectTab(tab) },
                                    icon = {
                                        Icon(
                                            imageVector = when (tab) {
                                                AppTab.HOME -> Icons.Default.Home
                                                AppTab.STUDIO -> Icons.Default.Image
                                                AppTab.REELS -> Icons.Default.Movie
                                                AppTab.PUBLISH -> Icons.Default.Share
                                                AppTab.ADS -> Icons.Default.Campaign
                                                else -> Icons.Default.Home
                                            },
                                            contentDescription = tab.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = when (tab) {
                                                AppTab.HOME -> "Home"
                                                AppTab.STUDIO -> "Photo"
                                                AppTab.REELS -> "Reels"
                                                AppTab.PUBLISH -> "Post"
                                                AppTab.ADS -> "Ads"
                                                else -> tab.title
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = Color(0xFFE0A93B),
                                        indicatorColor = Color(0xFFE0A93B),
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${tab.name}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = uiState.currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tab_animation"
                        ) { targetTab ->
                            when (targetTab) {
                                AppTab.HOME -> HomeScreen(
                                    projects = projects,
                                    selectedProject = uiState.selectedProject,
                                    currentRole = uiState.currentRole,
                                    isOffline = isOffline,
                                    activities = activities,
                                    onSelectProject = { viewModel.selectProject(it) },
                                    onNavigateTab = { viewModel.selectTab(it) },
                                    onToggleOffline = { viewModel.toggleOfflineMode() }
                                )

                                AppTab.STUDIO -> PhotoEditorScreen(
                                    selectedProject = uiState.selectedProject,
                                    allProjects = projects,
                                    isAiGenerating = uiState.isAiGenerating,
                                    currentRole = uiState.currentRole,
                                    onSelectProject = { viewModel.selectProject(it) },
                                    onUpdateBackground = { viewModel.updateBackgroundPreset(it) },
                                    onUpdateEnhancements = { c, s, v -> viewModel.updateFabricEnhancements(c, s, v) },
                                    onGenerateAiDescription = { tone -> viewModel.generateAiDescription(tone) },
                                    onNavigateTab = { viewModel.selectTab(it) }
                                )

                                AppTab.REELS -> ReelEditorScreen(
                                    selectedProject = uiState.selectedProject,
                                    isPlaying = uiState.isPlayingReel,
                                    onTogglePlayback = { viewModel.toggleReelPlayback() },
                                    onUpdateReelSettings = { anim, audio, hook ->
                                        viewModel.updateReelSettings(anim, audio, hook)
                                    },
                                    onNavigateTab = { viewModel.selectTab(it) }
                                )

                                AppTab.PUBLISH -> SocialPublisherScreen(
                                    selectedProject = uiState.selectedProject,
                                    scheduledPosts = scheduledPosts,
                                    currentRole = uiState.currentRole,
                                    onPublishOrSchedule = { caption, ig, yt, tw, epoch, instant ->
                                        viewModel.publishOrSchedulePost(caption, ig, yt, tw, epoch, instant)
                                    }
                                )

                                AppTab.ADS -> AdsManagerScreen(
                                    campaigns = campaigns,
                                    selectedProject = uiState.selectedProject,
                                    currentRole = uiState.currentRole,
                                    onCreateCampaign = { name, plat, obj, bud, aud ->
                                        viewModel.createAdCampaign(name, plat, obj, bud, aud)
                                    },
                                    onToggleCampaign = { viewModel.toggleCampaign(it) }
                                )

                                AppTab.ANALYTICS -> AnalyticsScreen(
                                    projects = projects,
                                    isPremiumUnlocked = uiState.isPremiumUnlocked,
                                    onNavigateTab = { viewModel.selectTab(it) },
                                    onUnlockPremium = { viewModel.unlockPremium() }
                                )

                                AppTab.TEAM -> TeamAndRoleScreen(
                                    teamMembers = teamMembers,
                                    currentRole = uiState.currentRole,
                                    activities = activities,
                                    isSyncing = isSyncing,
                                    isOffline = isOffline,
                                    onSwitchUserRole = { viewModel.switchUserRole(it) },
                                    onInviteMember = { name, email, role ->
                                        viewModel.inviteTeamMember(name, email, role)
                                    },
                                    onTriggerCloudSync = { viewModel.triggerCloudSync() }
                                )

                                AppTab.SUBSCRIPTION -> SubscriptionScreen(
                                    isPremiumUnlocked = uiState.isPremiumUnlocked,
                                    onUnlockPremium = { viewModel.unlockPremium() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
