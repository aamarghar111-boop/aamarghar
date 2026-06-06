package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "campaigns")
data class Campaign(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val originalPhotoUrl: String,
    val originalPhotoName: String,
    val generatedAdCopy: String,
    val generatedAdIdea: String,
    val colorPalette: String, // Comma separated list of hex colors
    val instagramCaption: String,
    val facebookCaption: String,
    val targetAudience: String,
    val designatedFolderId: String? = null,
    val designatedFolderName: String? = null,
    val isExportedBackToDrive: Boolean = false,
    val exportedFileId: String? = null,
    val isPostedSocialInstance: Boolean = false,
    val postedSocialChannels: String? = null, // e.g., "Instagram, Facebook"
    val createdAt: Long = System.currentTimeMillis()
) : Serializable

data class DriveFile(
    val id: String,
    val name: String,
    val mimeType: String,
    val thumbnailLink: String? = null,
    val size: String? = null,
    val modifiedTime: String? = null
)

enum class SocialPostChannel {
    INSTAGRAM,
    FACEBOOK,
    LINKEDIN,
    TWITTER
}

data class SocialPost(
    val id: String,
    val campaignId: Int,
    val campaignTitle: String,
    val channel: SocialPostChannel,
    val status: String, // "QUEUED", "POSTED", "FAILED"
    val caption: String,
    val postUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
