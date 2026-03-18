package com.mirea.photogallery.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.mirea.photogallery.presentation.GalleryScreen
import com.mirea.photogallery.presentation.GalleryViewModel

sealed class Screen(val route: String) {
    object Gallery : Screen("gallery")
}

fun NavGraphBuilder.photoGalleryNavigationGraph(
    navController: NavHostController,
    viewModel: GalleryViewModel
) {
    composable(Screen.Gallery.route) {
        GalleryScreen(viewModel = viewModel)
    }
}