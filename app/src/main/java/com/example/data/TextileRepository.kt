package com.example.data

import com.example.ai.GeminiDescriptionService
import com.example.ai.GeneratedProductCopy
import com.example.model.AdCampaignEntity
import com.example.model.CollaborationActivityEntity
import com.example.model.ScheduledPostEntity
import com.example.model.SyncStatus
import com.example.model.TeamMemberEntity
import com.example.model.TextileProjectEntity
import com.example.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TextileRepository(
    private val dao: TextileDao,
    private val geminiService: GeminiDescriptionService = GeminiDescriptionService()
) {
    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode = _isOfflineMode.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    // Projects
    fun getAllProjects(): Flow<List<TextileProjectEntity>> = dao.getAllProjects()

    suspend fun getProjectById(id: Long): TextileProjectEntity? = dao.getProjectById(id)

    suspend fun saveProject(project: TextileProjectEntity, currentMember: TeamMemberEntity?) {
        val status = if (_isOfflineMode.value) SyncStatus.OFFLINE_DRAFT.name else SyncStatus.SYNCED.name
        val toSave = project.copy(
            syncStatus = status,
            lastModified = System.currentTimeMillis(),
            authorName = currentMember?.name ?: project.authorName,
            authorRole = currentMember?.role ?: project.authorRole
        )
        if (toSave.id == 0L) {
            dao.insertProject(toSave)
            dao.insertActivity(
                CollaborationActivityEntity(
                    memberName = currentMember?.name ?: "User",
                    actionText = "Created new textile project: ${project.title}",
                    projectTitle = project.title
                )
            )
        } else {
            dao.updateProject(toSave)
            dao.insertActivity(
                CollaborationActivityEntity(
                    memberName = currentMember?.name ?: "User",
                    actionText = "Updated edits & backdrop on ${project.title}",
                    projectTitle = project.title
                )
            )
        }
    }

    suspend fun deleteProject(project: TextileProjectEntity) {
        dao.deleteProject(project)
    }

    // AI Product Description
    suspend fun generateDescription(
        title: String,
        category: String,
        fabricType: String,
        price: Double,
        tone: String
    ): GeneratedProductCopy {
        return geminiService.generateTextileCopy(title, category, fabricType, price, tone)
    }

    // Social Publishing
    fun getAllScheduledPosts(): Flow<List<ScheduledPostEntity>> = dao.getAllScheduledPosts()

    suspend fun scheduleOrPublishPost(
        post: ScheduledPostEntity,
        isInstantPublish: Boolean,
        currentMember: TeamMemberEntity?
    ) {
        val finalPost = post.copy(isPublished = isInstantPublish)
        dao.insertScheduledPost(finalPost)

        val platforms = buildList {
            if (post.postToInstagram) add("Instagram")
            if (post.postToYouTube) add("YouTube")
            if (post.postToTwitter) add("Twitter / X")
        }.joinToString(", ")

        val actionMsg = if (isInstantPublish) {
            "Directly published to $platforms"
        } else {
            "Scheduled release for $platforms"
        }

        dao.insertActivity(
            CollaborationActivityEntity(
                memberName = currentMember?.name ?: "Marketer",
                actionText = "$actionMsg (${post.projectTitle})",
                projectTitle = post.projectTitle
            )
        )
    }

    suspend fun deleteScheduledPost(post: ScheduledPostEntity) {
        dao.deleteScheduledPost(post)
    }

    // Ad Campaigns
    fun getAllCampaigns(): Flow<List<AdCampaignEntity>> = dao.getAllCampaigns()

    suspend fun createCampaign(campaign: AdCampaignEntity, currentMember: TeamMemberEntity?) {
        dao.insertCampaign(campaign)
        dao.insertActivity(
            CollaborationActivityEntity(
                memberName = currentMember?.name ?: "Growth Team",
                actionText = "Launched automated ad campaign: ${campaign.campaignName}",
                projectTitle = campaign.campaignName
            )
        )
    }

    suspend fun toggleCampaignStatus(campaign: AdCampaignEntity) {
        val newStatus = if (campaign.status == "ACTIVE") "PAUSED" else "ACTIVE"
        dao.updateCampaign(campaign.copy(status = newStatus))
    }

    // Team & Roles
    fun getAllTeamMembers(): Flow<List<TeamMemberEntity>> = dao.getAllTeamMembers()

    suspend fun switchCurrentUser(memberId: Long) {
        dao.setCurrentUser(memberId)
    }

    suspend fun addTeamMember(name: String, email: String, role: String) {
        dao.insertTeamMember(
            TeamMemberEntity(
                name = name,
                email = email,
                role = role,
                isCurrentUser = false
            )
        )
    }

    // Collaboration Activity
    fun getRecentActivities(): Flow<List<CollaborationActivityEntity>> = dao.getRecentActivities()

    // Offline & Sync
    fun setOfflineMode(enabled: Boolean) {
        _isOfflineMode.value = enabled
    }

    suspend fun syncPendingChanges() {
        if (_isOfflineMode.value) return
        _isSyncing.value = true
        try {
            kotlinx.coroutines.delay(1200) // Realistic cloud synchronization roundtrip
            dao.markAllAsSynced(SyncStatus.SYNCED.name)
            dao.insertActivity(
                CollaborationActivityEntity(
                    memberName = "System Cloud Sync",
                    actionText = "All offline projects synced to Cloud Storage successfully",
                    projectTitle = "Cloud Sync"
                )
            )
        } finally {
            _isSyncing.value = false
        }
    }
}
