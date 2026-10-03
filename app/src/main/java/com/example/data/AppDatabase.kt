package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.AdCampaignEntity
import com.example.model.BackgroundPreset
import com.example.model.CollaborationActivityEntity
import com.example.model.ScheduledPostEntity
import com.example.model.SyncStatus
import com.example.model.TeamMemberEntity
import com.example.model.TextileProjectEntity
import com.example.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TextileProjectEntity::class,
        ScheduledPostEntity::class,
        AdCampaignEntity::class,
        TeamMemberEntity::class,
        CollaborationActivityEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun textileDao(): TextileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "textile_studio_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database.textileDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: TextileDao) {
            // Seed Team Members
            val ownerId = dao.insertTeamMember(
                TeamMemberEntity(
                    name = "Sanjay Verma",
                    email = "sanjay@textilestudio.in",
                    role = UserRole.OWNER.name,
                    isCurrentUser = true
                )
            )
            dao.insertTeamMember(
                TeamMemberEntity(
                    name = "Priya Deshmukh",
                    email = "priya.design@textilestudio.in",
                    role = UserRole.EDITOR.name,
                    isCurrentUser = false
                )
            )
            dao.insertTeamMember(
                TeamMemberEntity(
                    name = "Arjun Mehta",
                    email = "arjun.growth@textilestudio.in",
                    role = UserRole.MARKETER.name,
                    isCurrentUser = false
                )
            )
            dao.insertTeamMember(
                TeamMemberEntity(
                    name = "Kavita Rao",
                    email = "kavita.review@clientbrand.com",
                    role = UserRole.VIEWER.name,
                    isCurrentUser = false
                )
            )

            // Seed Textile Projects with our generated high-res textile visuals
            val proj1 = dao.insertProject(
                TextileProjectEntity(
                    title = "Royal Crimson Banarasi Zari Silk",
                    category = "Saree",
                    fabricType = "Pure Katan Silk",
                    drawableResName = "textile_banarasi_saree_1790074526981",
                    backgroundPreset = BackgroundPreset.ROYAL_PALACE.name,
                    textureClarity = 0.92f,
                    fabricShine = 0.88f,
                    colorVibrancy = 0.95f,
                    aiDescription = "Handwoven Banarasi Silk Saree drenched in regal crimson with 24K electroplated gold Zari kadwa weave. Breathable handloom texture crafted over 45 days in Varanasi. Ideal for royal wedding receptions and festive pujas.",
                    hashtags = "#BanarasiSaree #PureSilk #HandloomHeritage #BridalWear #ZariCraft #VaranasiWeaves",
                    price = 8999.0,
                    reelHookText = "👑 The Royal Saree that took 45 Days to Handweave!",
                    reelAudioTrack = "Royal Shehnai x Lo-Fi Fusion (Viral IG Audio)",
                    viralProbability = 96,
                    syncStatus = SyncStatus.SYNCED.name,
                    authorName = "Sanjay Verma",
                    authorRole = UserRole.OWNER.name
                )
            )

            val proj2 = dao.insertProject(
                TextileProjectEntity(
                    title = "Emerald Bloom Embroidered Anarkali",
                    category = "Kurti & Suit",
                    fabricType = "Chanderi Silk & Chiffon",
                    drawableResName = "textile_designer_kurti_1790074540938",
                    backgroundPreset = BackgroundPreset.MINIMAL_MARBLE.name,
                    textureClarity = 0.85f,
                    fabricShine = 0.75f,
                    colorVibrancy = 0.90f,
                    aiDescription = "Lush Emerald Green flared Anarkali kurta accented with intricate white Lucknowi Chikankari jaal work and scalloped gold gota patti border. Paired with featherlight pure chiffon dupatta.",
                    hashtags = "#AnarkaliSuit #Chikankari #EmeraldFashion #DesignerEthnic #FestiveGlam",
                    price = 4599.0,
                    reelHookText = "✨ Steal the Spotlight in this Lucknowi Chikankari Anarkali!",
                    reelAudioTrack = "Trending Acoustic Sitar Chill (Reels Top 50)",
                    viralProbability = 91,
                    syncStatus = SyncStatus.SYNCED.name,
                    authorName = "Priya Deshmukh",
                    authorRole = UserRole.EDITOR.name
                )
            )

            // Seed Scheduled Social Media Posts
            dao.insertScheduledPost(
                ScheduledPostEntity(
                    projectId = proj1,
                    projectTitle = "Royal Crimson Banarasi Zari Silk",
                    caption = "Drape pure royalty this wedding season! 👑 Tap link in bio or WhatsApp to claim exclusive festive discount.\n\n#BanarasiSilk #BridalSaree #RoyalFashion",
                    postToInstagram = true,
                    postToYouTube = true,
                    postToTwitter = false,
                    scheduledTimeEpoch = System.currentTimeMillis() + (3600000 * 3), // in 3 hours
                    isPublished = false
                )
            )

            dao.insertScheduledPost(
                ScheduledPostEntity(
                    projectId = proj2,
                    projectTitle = "Emerald Bloom Embroidered Anarkali",
                    caption = "Elegance meets comfort with handwoven Chanderi silk. New arrival drops tonight! ✨\n\n#Anarkali #EthnicWear #OOTD",
                    postToInstagram = true,
                    postToYouTube = false,
                    postToTwitter = true,
                    scheduledTimeEpoch = System.currentTimeMillis() + (3600000 * 8), // in 8 hours
                    isPublished = false
                )
            )

            // Seed Automated Ads Campaigns
            dao.insertCampaign(
                AdCampaignEntity(
                    campaignName = "Festive Wedding Silk Catalog - Diwali Wave",
                    projectId = proj1,
                    platform = "Meta Ads (Instagram Reels + Stories)",
                    objective = "WhatsApp Orders & Catalog Sales",
                    dailyBudgetInr = 1200.0,
                    targetAudience = "Women 24-52 • Luxury Indian Ethnic • Delhi, Mumbai, Bengaluru, Hyderabad",
                    status = "ACTIVE",
                    impressions = 34500,
                    clicks = 1820,
                    roas = 4.8
                )
            )

            dao.insertCampaign(
                AdCampaignEntity(
                    campaignName = "Summer Chanderi Pret Kurti Launch",
                    projectId = proj2,
                    platform = "Google Shopping & YouTube Shorts",
                    objective = "Online Store Purchases",
                    dailyBudgetInr = 650.0,
                    targetAudience = "Women 18-40 • Designer Kurtis & Semi-Formal Ethnic",
                    status = "ACTIVE",
                    impressions = 14200,
                    clicks = 840,
                    roas = 3.6
                )
            )

            // Seed Collaboration Activity
            dao.insertActivity(
                CollaborationActivityEntity(
                    memberName = "Priya Deshmukh",
                    actionText = "Replaced backdrop with Italian Marble on Emerald Kurti",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
                    projectTitle = "Emerald Bloom Embroidered Anarkali"
                )
            )
            dao.insertActivity(
                CollaborationActivityEntity(
                    memberName = "Arjun Mehta",
                    actionText = "Scheduled cross-platform Reel release across IG & YT",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                    projectTitle = "Royal Crimson Banarasi Zari Silk"
                )
            )
            dao.insertActivity(
                CollaborationActivityEntity(
                    memberName = "Sanjay Verma",
                    actionText = "Generated AI description and enhanced silk texture clarity to 92%",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                    projectTitle = "Royal Crimson Banarasi Zari Silk"
                )
            )
        }
    }
}
