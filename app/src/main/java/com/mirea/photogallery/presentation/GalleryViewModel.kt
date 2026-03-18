package com.mirea.photogallery.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirea.photogallery.data.GalleryRepository
import com.mirea.photogallery.data.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GalleryUiState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null
)

class GalleryViewModel(
    val repository: GalleryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val photos = withContext(Dispatchers.IO) {
                repository.loadAllPhotos()
            }
            _uiState.update { it.copy(
                photos = photos,
                isLoading = false
            ) }
        }
    }

    fun onPhotoTaken(photo: Photo) {
        // Добавляем фото в список (без перезагрузки всех)
        _uiState.update { state ->
            state.copy(
                photos = listOf(photo) + state.photos
            )
        }
    }

    fun exportPhoto(photo: Photo) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.exportToGallery(photo)
            }

            val message = if (success) {
                "Фото добавлено в галерею"
            } else {
                "Ошибка при экспорте фото"
            }

            _uiState.update { it.copy(snackbarMessage = message) }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}