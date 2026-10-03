package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.TextileRepository
import com.example.model.AdCampaignEntity
import com.example.model.BackgroundPreset
import com.example.model.CollaborationActivityEntity
import com.example.model.ScheduledPostEntity
import com.example.model.SyncStatus
import com.example.model.TeamMemberEntity
import com.example.model.TextileProjectEntity
import com.example.model.UserRole
import com.example.model.VideoAnimationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val iconName: String) {
    HOME("Home", "home"),
    STUDIO("Photo Studio", "photo"),
    REELS("Reel / Video", "video"),
    PUBLISH("Social Post", "share"),
    ADS("Ads Manager", "ads"),
    ANALYTICS("Analytics", "analytics"),
    TEAM("Team & Roles", "team"),
    SUBSCRIPTION("Pro Plans", "diamond")
}

data class TextileUiState(
    val currentTab: AppTab = AppTab.HOME,
    val selectedProject: TextileProjectEntity? = null,
    val isAiGenerating: Boolean = false,
    val isPlayingReel: Boolean = true,
    val reelPlayheadProgress: Float = 0.0f,
    val currentRole: UserRole = UserRole.OWNER,
    val statusMessage: String? = null,
    val isPremiumUnlocked: Boolean = false
)

class TextileViewModel(
    private val repository: TextileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TextileUiState())
    val uiState: StateFlow<TextileUiState> = _uiState.asStateFlow()

    val projects: StateFlow<List<TextileProjectEntity>> = repository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scheduledPosts: StateFlow<List<ScheduledPostEntity>> = repository.getAllScheduledPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adCampaigns: StateFlow<List<AdCampaignEntity>> = repository.getAllCampaigns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val teamMembers: StateFlow<List<TeamMemberEntity>> = repository.getAllTeamMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentActivities: StateFlow<List<CollaborationActivityEntity>> = repository.getRecentActivities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isOfflineMode: StateFlow<Boolean> = repository.isOfflineMode
    val isSyncing: StateFlow<Boolean> = repository.isSyncing

    init {
        // Auto-select first project once loaded if none selected
        viewModelScope.launch {
            projects.collect { list ->
                if (_uiState.value.selectedProject == null && list.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(selectedProject = list.first())
                }
            }
        }
        viewModelScope.launch {
            teamMembers.collect { members ->
                val current = members.firstOrNull { it.isCurrentUser }
                if (current != null) {
                    val role = try {
                        UserRole.valueOf(current.role)
                    } catch (e: Exception) {
                        UserRole.OWNER
                    }
                    _uiState.value = _uiState.value.copy(currentRole = role)
                }
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun selectProject(project: TextileProjectEntity) {
        _uiState.value = _uiState.value.copy(selectedProject = project)
    }

    // Photo Studio Editing
    fun updateBackgroundPreset(preset: BackgroundPreset) {
        val current = _uiState.value.selectedProject ?: return
        val updated = current.copy(backgroundPreset = preset.name)
        _uiState.value = _uiState.value.copy(selectedProject = updated)
        saveCurrentProject(updated)
    }

    fun updateFabricEnhancements(textureClarity: Float, fabricShine: Float, colorVibrancy: Float) {
        val current = _uiState.value.selectedProject ?: return
        val updated = current.copy(
            textureClarity = textureClarity,
            fabricShine = fabricShine,
            colorVibrancy = colorVibrancy
        )
        _uiState.value = _uiState.value.copy(selectedProject = updated)
        saveCurrentProject(updated)
    }

    // AI Description Generation
    fun generateAiDescription(tone: String = "Festive Luxury") {
        val project = _uiState.value.selectedProject ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAiGenerating = true)
            val result = repository.generateDescription(
                title = project.title,
                category = project.category,
                fabricType = project.fabricType,
                price = project.price,
                tone = tone
            )
            val updated = project.copy(
                aiDescription = result.description,
                hashtags = result.hashtags,
                reelHookText = result.viralHook
            )
            _uiState.value = _uiState.value.copy(
                selectedProject = updated,
                isAiGenerating = false,
                statusMessage = "AI Product Story & Viral Tags generated!"
            )
            saveCurrentProject(updated)
        }
    }

    // Reel / Photo-To-Video Editor
    fun updateReelSettings(
        animationType: VideoAnimationType,
        audioTrack: String,
        hookText: String
    ) {
        val current = _uiState.value.selectedProject ?: return
        val updated = current.copy(
            reelAnimation = animationType.name,
            reelAudioTrack = audioTrack,
            reelHookText = hookText
        )
        _uiState.value = _uiState.value.copy(selectedProject = updated)
        saveCurrentProject(updated)
    }

    fun toggleReelPlayback() {
        _uiState.value = _uiState.value.copy(isPlayingReel = !_uiState.value.isPlayingReel)
    }

    fun setReelProgress(progress: Float) {
        _uiState.value = _uiState.value.copy(reelPlayheadProgress = progress.coerceIn(0f, 1f))
    }

    // Social Media Multi-Platform Publishing
    fun publishOrSchedulePost(
        caption: String,
        postToInstagram: Boolean,
        postToYouTube: Boolean,
        postToTwitter: Boolean,
        scheduledEpoch: Long,
        isInstant: Boolean
    ) {
        val project = _uiState.value.selectedProject ?: return
        val currentMember = teamMembers.value.firstOrNull { it.isCurrentUser }

        viewModelScope.launch {
            val post = ScheduledPostEntity(
                projectId = project.id,
                projectTitle = project.title,
                caption = caption,
                postToInstagram = postToInstagram,
                postToYouTube = postToYouTube,
                postToTwitter = postToTwitter,
                scheduledTimeEpoch = scheduledEpoch,
                isPublished = isInstant
            )
            repository.scheduleOrPublishPost(post, isInstant, currentMember)
            _uiState.value = _uiState.value.copy(
                statusMessage = if (isInstant) "Successfully posted to selected channels!" else "Post scheduled successfully!"
            )
        }
    }

    // Automated Ads Manager
    fun createAdCampaign(
        campaignName: String,
        platform: String,
        objective: String,
        budget: Double,
        targetAudience: String
    ) {
        val project = _uiState.value.selectedProject ?: return
        val currentMember = teamMembers.value.firstOrNull { it.isCurrentUser }

        viewModelScope.launch {
            val campaign = AdCampaignEntity(
                campaignName = campaignName,
                projectId = project.id,
                platform = platform,
                objective = objective,
                dailyBudgetInr = budget,
                targetAudience = targetAudience,
                status = "ACTIVE",
                impressions = (budget * 28).toInt(),
                clicks = (budget * 1.5).toInt(),
                roas = 4.2
            )
            repository.createCampaign(campaign, currentMember)
            _uiState.value = _uiState.value.copy(statusMessage = "Automated Ad Campaign launched!")
        }
    }

    fun toggleCampaign(campaign: AdCampaignEntity) {
        viewModelScope.launch {
            repository.toggleCampaignStatus(campaign)
        }
    }

    // Team & Roles
    fun switchUserRole(memberId: Long) {
        viewModelScope.launch {
            repository.switchCurrentUser(memberId)
            val member = teamMembers.value.firstOrNull { it.id == memberId }
            if (member != null) {
                val role = try {
                    UserRole.valueOf(member.role)
                } catch (e: Exception) {
                    UserRole.OWNER
                }
                _uiState.value = _uiState.value.copy(
                    currentRole = role,
                    statusMessage = "Switched active role to: ${member.name} (${role.displayName})"
                )
            }
        }
    }

    fun inviteTeamMember(name: String, email: String, role: String) {
        viewModelScope.launch {
            repository.addTeamMember(name, email, role)
            _uiState.value = _uiState.value.copy(statusMessage = "Added team member $name with $role role")
        }
    }

    // Offline & Sync
    fun toggleOfflineMode() {
        val next = !repository.isOfflineMode.value
        repository.setOfflineMode(next)
        _uiState.value = _uiState.value.copy(
            statusMessage = if (next) "Offline Mode Activated. Edits saved locally." else "Online Mode Restored. Ready to sync."
        )
        if (!next) {
            triggerCloudSync()
        }
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            repository.syncPendingChanges()
            _uiState.value = _uiState.value.copy(statusMessage = "All projects synced to Cloud Storage.")
        }
    }

    fun unlockPremium() {
        _uiState.value = _uiState.value.copy(
            isPremiumUnlocked = true,
            statusMessage = "Pro Artisan Plan Activated! Unlocked 4K render & automated ad scaling."
        )
    }

    fun dismissStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    private fun saveCurrentProject(project: TextileProjectEntity) {
        val currentMember = teamMembers.value.firstOrNull { it.isCurrentUser }
        viewModelScope.launch {
            repository.saveProject(project, currentMember)
        }
    }
}

class TextileViewModelFactory(
    private val repository: TextileRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TextileViewModel(repository) as T
    }
}
