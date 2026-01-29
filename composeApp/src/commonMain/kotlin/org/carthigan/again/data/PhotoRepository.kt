package org.carthigan.again.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import org.carthigan.again.util.currentTimeMillis

/**
 * Repository for fetching and managing photos.
 * Uses Picsum Photos API for random photos.
 */
class PhotoRepository {
    
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }
    
    // In-memory storage for "uploaded" photos (simulated uploads)
    private val _uploadedPhotos = MutableStateFlow<List<Photo>>(emptyList())
    val uploadedPhotos: StateFlow<List<Photo>> = _uploadedPhotos.asStateFlow()
    
    /**
     * Fetch a list of random photos from Picsum API
     * @param page Page number (for pagination)
     * @param limit Number of photos per page
     */
    suspend fun getRandomPhotos(page: Int = 1, limit: Int = 30): Result<List<Photo>> {
        return try {
            val photos: List<Photo> = httpClient.get(
                "https://picsum.photos/v2/list?page=$page&limit=$limit"
            ).body()
            Result.success(photos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get a specific photo by ID
     */
    suspend fun getPhotoById(id: String): Result<Photo> {
        return try {
            val photo: Photo = httpClient.get(
                "https://picsum.photos/id/$id/info"
            ).body()
            Result.success(photo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Simulate uploading a photo by adding it to local storage.
     * In a real app, this would upload to a server.
     * @param imageUrl The URL of the image to "upload"
     */
    fun uploadPhoto(imageUrl: String) {
        val newPhoto = Photo(
            id = "user_${currentTimeMillis()}",
            author = "You",
            url = imageUrl,
            downloadUrl = imageUrl
        )
        _uploadedPhotos.value = _uploadedPhotos.value + newPhoto
    }
    
    /**
     * Add a randomly generated photo (simulating an upload)
     */
    fun addRandomUpload() {
        val randomId = (1000..2000).random()
        val newPhoto = Photo(
            id = "random_$randomId",
            author = "You",
            downloadUrl = "https://picsum.photos/id/$randomId/600/600"
        )
        _uploadedPhotos.value = listOf(newPhoto) + _uploadedPhotos.value
    }
    
    /**
     * Get all photos (API photos + uploaded photos)
     */
    suspend fun getAllPhotos(page: Int = 1): Result<List<Photo>> {
        return getRandomPhotos(page).map { apiPhotos ->
            _uploadedPhotos.value + apiPhotos
        }
    }
    
    fun close() {
        httpClient.close()
    }
}
