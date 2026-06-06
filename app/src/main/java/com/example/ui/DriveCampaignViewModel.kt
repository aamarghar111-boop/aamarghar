package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CampaignRepository
import com.example.model.Campaign
import com.example.model.DriveFile
import com.example.model.SocialPost
import com.example.model.SocialPostChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class DriveCampaignViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = CampaignRepository(database.campaignDao(), application)

    // Flow from Room of all created campaigns
    val campaignsList: StateFlow<List<Campaign>> = repository.allCampaigns
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // State Managers
    var driveFiles by mutableStateOf<List<DriveFile>>(emptyList())
        private set

    var selectedFile by mutableStateOf<DriveFile?>(null)

    var activeCampaign by mutableStateOf<Campaign?>(null)

    var isLoadingFiles by mutableStateOf(false)
        private set

    var isAnalyzing by mutableStateOf(false)
        private set

    var isExporting by mutableStateOf(false)
        private set

    var oauthToken by mutableStateOf("simulated") // "simulated" toggles high-intent sandboxing

    var customPromptInstruction by mutableStateOf("")

    var designatedFolderId by mutableStateOf<String?>(null)
    var designatedFolderName by mutableStateOf<String?>(null)

    // Social post logs (Marketing automation tracker)
    private val _socialPosts = MutableStateFlow<List<SocialPost>>(emptyList())
    val socialPosts: StateFlow<List<SocialPost>> = _socialPosts.asStateFlow()

    init {
        loadDriveFiles()
        seedSimulatedPosts()
    }

    fun updateOauthToken(newToken: String) {
        oauthToken = newToken
        loadDriveFiles()
    }

    /**
     * Loads photos from Google Drive (real or simulated)
     */
    fun loadDriveFiles() {
        viewModelScope.launch {
            isLoadingFiles = true
            try {
                driveFiles = repository.getDrivePhotos(oauthToken)
                // Select first block automatically to improve immediate onboarding
                if (driveFiles.isNotEmpty() && selectedFile == null) {
                    selectedFile = driveFiles.first()
                }
            } catch (e: Exception) {
                LogState("Load files error: ${e.localizedMessage}")
            } finally {
                isLoadingFiles = false
            }
        }
    }

    /**
     * Triggers Gemini Multimodal analysis
     */
    fun generateCampaign() {
        val file = selectedFile ?: return
        viewModelScope.launch {
            isAnalyzing = true
            try {
                val campaignResult = repository.analyzePhoto(file, customPromptInstruction.ifEmpty { null })
                // Save immediately into Room DB to meet Offline-First persistence
                val insertedId = repository.insertCampaign(campaignResult)
                activeCampaign = campaignResult.copy(id = insertedId.toInt())
            } catch (e: Exception) {
                LogState("Analysis failure: ${e.localizedMessage}")
            } finally {
                isAnalyzing = false
            }
        }
    }

    /**
     * Selects an old campaign from the local DB archive
     */
    fun selectActiveCampaign(campaign: Campaign) {
        activeCampaign = campaign
        // Attempt to match the linked file
        selectedFile = driveFiles.find { it.name == campaign.originalPhotoName }
    }

    /**
     * Create designated project folder & Export design documentation directly back to Drive
     */
    fun exportToDrive() {
        val campaign = activeCampaign ?: return
        viewModelScope.launch {
            isExporting = true
            try {
                // 1. Create designated folder if it does not exist
                val folderName = "Campaign Project: ${campaign.title.take(20)}"
                val (folderId, createdName) = repository.createDriveFolder(oauthToken, folderName)
                designatedFolderId = folderId
                designatedFolderName = createdName

                // 2. Synthesize text campaign brief content
                val adBriefDocument = """
                    ========================================
                    DRIVE CAMPAIGN STUDIO BRIEF
                    ========================================
                    CAMPAIGN: ${campaign.title}
                    DESIGN SOURCE: ${campaign.originalPhotoName}
                    DATE: ${java.text.DateFormat.getDateTimeInstance().format(java.util.Date())}
                    
                    1. VISUAL PALETTE ANALYZED:
                       ${campaign.colorPalette}
                       
                    2. CORE TARGET AUDIENCE SECTOR:
                       ${campaign.targetAudience}
                       
                    3. CREATIVE MARKETING STRATEGY / ANGLE:
                       ${campaign.generatedAdIdea}
                       
                    4. ADVERTISING COPY SPECIFICATIONS:
                       Primary Display Outline: ${campaign.generatedAdCopy}
                       
                    5. PLATFORM-OPTIMIZED COPY:
                       [INSTAGRAM WRITTEN BRIEF]:
                       ${campaign.instagramCaption}
                       
                       [FACEBOOK WRITTEN BRIEF]:
                       ${campaign.facebookCaption}
                       
                    ========================================
                    AUTOMATION LIFECYCLE: ACTIVE SCHEDULER
                    ========================================
                """.trimIndent()

                // 3. Upload written content back onto Google Drive
                val fileId = repository.uploadDesignToFolder(
                    oauthToken = oauthToken,
                    folderId = folderId,
                    fileName = "advertisement_brief_${campaign.title.lowercase().replace(" ", "_")}.txt",
                    fileContentString = adBriefDocument
                )

                // 4. Update Campaign in local Room Database
                val updatedCampaign = campaign.copy(
                    isExportedBackToDrive = true,
                    exportedFileId = fileId,
                    designatedFolderId = folderId,
                    designatedFolderName = createdName
                )
                repository.updateCampaign(updatedCampaign)
                activeCampaign = updatedCampaign

            } catch (e: Exception) {
                LogState("Export failed: ${e.localizedMessage}")
            } finally {
                isExporting = false
            }
        }
    }

    /**
     * Delete active or archived campaign from database list
     */
    fun deleteCampaign(campaign: Campaign) {
        viewModelScope.launch {
            repository.deleteCampaign(campaign)
            if (activeCampaign?.id == campaign.id) {
                activeCampaign = null
            }
        }
    }

    /**
     * Handles simulated high-intent social publication schedule for fb/instagram
     */
    fun pushSocialAutomation() {
        val campaign = activeCampaign ?: return
        viewModelScope.launch {
            // Update Campaign Local States
            val currentChannels = campaign.postedSocialChannels?.split(", ")?.toMutableList() ?: mutableListOf()
            
            val newPosts = mutableListOf<SocialPost>()
            if (!currentChannels.contains("Instagram")) {
                currentChannels.add("Instagram")
                newPosts.add(
                    SocialPost(
                        id = "ig_${UUID.randomUUID().toString().take(6)}",
                        campaignId = campaign.id,
                        campaignTitle = campaign.title,
                        channel = SocialPostChannel.INSTAGRAM,
                        status = "POSTED",
                        caption = campaign.instagramCaption,
                        postUrl = "https://instagram.com/p/simulated_${campaign.id}"
                    )
                )
            }
            if (!currentChannels.contains("Facebook")) {
                currentChannels.add("Facebook")
                newPosts.add(
                    SocialPost(
                        id = "fb_${UUID.randomUUID().toString().take(6)}",
                        campaignId = campaign.id,
                        campaignTitle = campaign.title,
                        channel = SocialPostChannel.FACEBOOK,
                        status = "POSTED",
                        caption = campaign.facebookCaption,
                        postUrl = "https://facebook.com/posts/simulated_${campaign.id}"
                    )
                )
            }

            if (newPosts.isNotEmpty()) {
                _socialPosts.value = newPosts + _socialPosts.value
                val updatedCampaign = campaign.copy(
                    isPostedSocialInstance = true,
                    postedSocialChannels = currentChannels.joinToString(", ")
                )
                repository.updateCampaign(updatedCampaign)
                activeCampaign = updatedCampaign
            }
        }
    }

    private fun seedSimulatedPosts() {
        _socialPosts.value = listOf(
            SocialPost(
                id = "sp_001",
                campaignId = -1,
                campaignTitle = "Cozy Organic Blend Pre-Launch",
                channel = SocialPostChannel.INSTAGRAM,
                status = "POSTED",
                caption = "Nothing says authenticity like hand-crafted details. Brewed with love. ☕️🌿 #CoffeeVibe #Simulated",
                postUrl = "https://instagram.com/p/mock_pre_launch",
                timestamp = System.currentTimeMillis() - 86400000
            ),
            SocialPost(
                id = "sp_002",
                campaignId = -1,
                campaignTitle = "Quantum Audio Beta Board",
                channel = SocialPostChannel.FACEBOOK,
                status = "POSTED",
                caption = "The engineering community has spoken—the future of separation technology has arrived. Check out user logs below. 📡🎧",
                postUrl = "https://facebook.com/posts/mock_beta_board",
                timestamp = System.currentTimeMillis() - 172800000
            )
        )
    }

    private fun LogState(msg: String) {
        android.util.Log.w("DriveCampaignViewModel", msg)
    }
}
