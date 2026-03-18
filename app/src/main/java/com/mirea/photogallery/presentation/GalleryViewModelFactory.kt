package com.mirea.photogallery.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mirea.photogallery.data.GalleryRepository

class GalleryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GalleryViewModel(GalleryRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}