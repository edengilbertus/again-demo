package org.carthigan.again.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.carthigan.again.data.Photo
import org.carthigan.again.data.PhotoRepository
import org.carthigan.again.data.PhotosUiState

class PhotoViewModel : ViewModel() {
    
    private val repository = PhotoRepository()
    
    private val _uiState = MutableStateFlow(PhotosUiState())
    val uiState: StateFlow<PhotosUiState> = _uiState.asStateFlow()
    
    private var currentPage = 1
    
    init {
        loadPhotos()
    }
    
    fun loadPhotos(refresh: Boolean = false) {
        if (refresh) {
            currentPage = 1
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            repository.getAllPhotos(currentPage)
                .onSuccess { photos ->
                    _uiState.update { state ->
                        val newPhotos = if (refresh || currentPage == 1) {
                            photos
                        } else {
                            state.photos + photos
                        }
                        state.copy(
                            photos = newPhotos,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load photos"
                    )}
                }
        }
    }
    
    fun loadMorePhotos() {
        if (_uiState.value.isLoading) return
        currentPage++
        loadPhotos()
    }
    
    fun refreshPhotos() {
        loadPhotos(refresh = true)
    }
    
    fun selectPhoto(photo: Photo?) {
        _uiState.update { it.copy(selectedPhoto = photo) }
    }
    
    /**
     * Simulate uploading a new photo.
     * Adds a random photo from Picsum to the "uploaded" list.
     */
    fun uploadNewPhoto() {
        repository.addRandomUpload()
        // Refresh to show the new photo
        loadPhotos(refresh = true)
    }
    
    /**
     * Upload a photo with a specific URL
     */
    fun uploadPhotoFromUrl(url: String) {
        if (url.isNotBlank()) {
            repository.uploadPhoto(url)
            loadPhotos(refresh = true)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        repository.close()
    }
}
