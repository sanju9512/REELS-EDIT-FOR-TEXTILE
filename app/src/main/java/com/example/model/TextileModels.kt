package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val displayName: String, val badgeColorHex: Long) {
    OWNER("Owner / Admin", 0xFFE0A93B),
    EDITOR("Creative Editor", 0xFF6C63FF),
    MARKETER("Growth Marketer", 0xFF00B4D8),
    VIEWER("Viewer / Client", 0xFF8D99AE);

    fun canEditPhotos(): Boolean = this in listOf(OWNER, EDITOR)
    fun canPublishSocial(): Boolean = this in listOf(OWNER, MARKETER)
    fun canManageAds(): Boolean = this in listOf(OWNER, MARKETER)
    fun canManageTeam(): Boolean = this == OWNER
}

enum class SyncStatus(val label: String, val colorHex: Long) {
    SYNCED("Cloud Synced", 0xFF2EC4B6),
    PENDING_SYNC("Pending Sync", 0xFFFF9F1C),
    OFFLINE_DRAFT("Offline Draft", 0xFFE71D36)
}

enum class BackgroundPreset(
    val title: String,
    val subtitle: String,
    val previewGradientStart: Long,
    val previewGradientEnd: Long
) {
    ORIGINAL("Original Backdrop", "Default captured background", 0xFF3A3A4A, 0xFF2A2A38),
    STUDIO_WHITE("Pure Studio White", "Clean e-commerce standard", 0xFFFFFFFF, 0xFFE8ECEF),
    ROYAL_PALACE("Heritage Palace", "Royal Indian sandstone & arches", 0xFFDDA15E, 0xFFBC6C25),
    MINIMAL_MARBLE("Italian Marble", "Modern luxury boutique display", 0xFFE0E1DD, 0xFF778DA9),
    LUXURY_BOUTIQUE("Teak Wood Showcase", "Warm artisanal wooden studio", 0xFF6F4E37, 0xFF3D2B1F),
    SILK_DRAPE("Champagne Silk", "Soft luxurious textile ripples", 0xFFE9D8A6, 0xFFEE9B00),
    NEON_RUNWAY("High Fashion Runway", "Vibrant runway lighting", 0xFF5A189A, 0xFF10002B),
    TRANSPARENT("Transparent PNG", "Cutout for marketplace stickers", 0xFF4A4E69, 0xFF22223B)
}

enum class VideoAnimationType(val title: String, val durationMs: Int) {
    KEN_BURNS_ZOOM("Dramatic Fabric Zoom", 4000),
    SMOOTH_PAN("Vertical Drape Pan", 5000),
    SPIN_360("Artisanal 360 Spin", 4500),
    SHIMMER_PULSE("Zari Gold Shimmer", 3500)
}

@Entity(tableName = "textile_projects")
data class TextileProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val fabricType: String,
    val drawableResName: String,
    val backgroundPreset: String = BackgroundPreset.STUDIO_WHITE.name,
    val textureClarity: Float = 0.85f,
    val fabricShine: Float = 0.70f,
    val colorVibrancy: Float = 0.90f,
    val aiDescription: String = "",
    val hashtags: String = "#TextileFashion #PureSilk #EthnicWear #IndianFashion #FestiveLook",
    val price: Double = 3499.0,
    val reelHookText: String = "✨ Stop Scrolling: 100% Authentic Handloom Silk!",
    val reelAudioTrack: String = "Trending Sitar Lo-Fi Beats (Viral on IG)",
    val reelAnimation: String = VideoAnimationType.KEN_BURNS_ZOOM.name,
    val viralProbability: Int = 94,
    val syncStatus: String = SyncStatus.SYNCED.name,
    val lastModified: Long = System.currentTimeMillis(),
    val authorName: String = "Rahul Sharma",
    val authorRole: String = UserRole.OWNER.name
)

@Entity(tableName = "scheduled_posts")
data class ScheduledPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val projectTitle: String,
    val caption: String,
    val postToInstagram: Boolean = true,
    val postToYouTube: Boolean = true,
    val postToTwitter: Boolean = false,
    val scheduledTimeEpoch: Long = System.currentTimeMillis() + 3600000 * 4,
    val isPublished: Boolean = false,
    val postType: String = "Reel & Catalog"
)

@Entity(tableName = "ad_campaigns")
data class AdCampaignEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val campaignName: String,
    val projectId: Long,
    val platform: String = "Meta Ads (IG & FB)",
    val objective: String = "WhatsApp Direct Orders",
    val dailyBudgetInr: Double = 800.0,
    val targetAudience: String = "Women 22-55 • Silk & Festive Wear Buyers • Top Metro Cities",
    val status: String = "ACTIVE",
    val impressions: Int = 18450,
    val clicks: Int = 980,
    val roas: Double = 4.3
)

@Entity(tableName = "team_members")
data class TeamMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val role: String,
    val isCurrentUser: Boolean = false
)

@Entity(tableName = "collaboration_activities")
data class CollaborationActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberName: String,
    val actionText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val projectTitle: String
)
