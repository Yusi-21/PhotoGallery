package com.mirea.photogallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mirea.photogallery.presentation.GalleryScreen
import com.mirea.photogallery.presentation.GalleryViewModel
import com.mirea.photogallery.presentation.GalleryViewModelFactory
import com.mirea.photogallery.ui.theme.PhotoGalleryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoGalleryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: GalleryViewModel = viewModel(
                        factory = GalleryViewModelFactory(this)
                    )
                    GalleryScreen(viewModel = viewModel)
                }
            }
        }
    }
}