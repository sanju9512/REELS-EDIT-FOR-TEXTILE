package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.AdCampaignEntity
import com.example.model.CollaborationActivityEntity
import com.example.model.ScheduledPostEntity
import com.example.model.TeamMemberEntity
import com.example.model.TextileProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TextileDao {
    // Projects
    @Query("SELECT * FROM textile_projects ORDER BY lastModified DESC")
    fun getAllProjects(): Flow<List<TextileProjectEntity>>

    @Query("SELECT * FROM textile_projects WHERE id = :id")
    suspend fun getProjectById(id: Long): TextileProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: TextileProjectEntity): Long

    @Update
    suspend fun updateProject(project: TextileProjectEntity)

    @Delete
    suspend fun deleteProject(project: TextileProjectEntity)

    @Query("UPDATE textile_projects SET syncStatus = :newStatus WHERE syncStatus != :newStatus")
    suspend fun markAllAsSynced(newStatus: String)

    // Scheduled Posts
    @Query("SELECT * FROM scheduled_posts ORDER BY scheduledTimeEpoch ASC")
    fun getAllScheduledPosts(): Flow<List<ScheduledPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledPost(post: ScheduledPostEntity): Long

    @Update
    suspend fun updateScheduledPost(post: ScheduledPostEntity)

    @Delete
    suspend fun deleteScheduledPost(post: ScheduledPostEntity)

    // Ad Campaigns
    @Query("SELECT * FROM ad_campaigns ORDER BY id DESC")
    fun getAllCampaigns(): Flow<List<AdCampaignEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: AdCampaignEntity): Long

    @Update
    suspend fun updateCampaign(campaign: AdCampaignEntity)

    // Team Members
    @Query("SELECT * FROM team_members ORDER BY id ASC")
    fun getAllTeamMembers(): Flow<List<TeamMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMember(member: TeamMemberEntity): Long

    @Query("UPDATE team_members SET isCurrentUser = (id = :memberId)")
    suspend fun setCurrentUser(memberId: Long)

    // Collaboration Activity
    @Query("SELECT * FROM collaboration_activities ORDER BY timestamp DESC LIMIT 20")
    fun getRecentActivities(): Flow<List<CollaborationActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: CollaborationActivityEntity): Long
}
