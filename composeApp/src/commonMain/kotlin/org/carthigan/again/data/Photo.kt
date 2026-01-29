package org.carthigan.again.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a photo from the Picsum API or user-uploaded content
 */
@Serializable
data class Photo(
    val id: String,
    val author: String = "Anonymous",
    val width: Int = 0,
    val height: Int = 0,
    @SerialName("download_url")
    val downloadUrl: String = "",
    val url: String = ""
) {
    /**
     * Get the image URL with optional sizing
     * Picsum supports: https://picsum.photos/id/{id}/{width}/{height}
     */
    fun getImageUrl(requestedWidth: Int = 600, requestedHeight: Int = 600): String {
        return if (downloadUrl.isNotEmpty()) {
            // Use picsum resize endpoint for better performance
            "https://picsum.photos/id/$id/$requestedWidth/$requestedHeight"
        } else if (url.isNotEmpty()) {
            url
        } else {
            "https://picsum.photos/$requestedWidth/$requestedHeight?random=$id"
        }
    }

    fun getThumbnailUrl(): String = getImageUrl(300, 300)
    
    fun getFullSizeUrl(): String = getImageUrl(1200, 1200)
}

/**
 * UI state for the photo gallery
 */
data class PhotosUiState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPhoto: Photo? = null
)
