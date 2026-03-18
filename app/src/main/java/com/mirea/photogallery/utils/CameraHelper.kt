package com.mirea.photogallery.utils

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.*
import com.mirea.photogallery.data.Photo
import com.mirea.photogallery.data.GalleryRepository

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberCameraLauncher(
    repository: GalleryRepository,
    onPhotoTaken: (Photo) -> Unit,
    onError: (String) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Получаем последнее сохраненное фото
            val photoFile = repository.getCameraOutputFile()
            Photo.fromFile(photoFile)?.let { photo ->
                onPhotoTaken(photo)
            } ?: onError("Не удалось загрузить фото")
        }
    }

    return {
        when (cameraPermissionState.status) {
            is PermissionStatus.Granted -> {
                // Разрешение есть - запускаем камеру
                val photoFile = repository.getCameraOutputFile()
                // ИСПОЛЬЗУЕМ FILEPROVIDER ВМЕСТО Uri.fromFile()
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    photoFile
                )
                launcher.launch(uri)
            }
            is PermissionStatus.Denied -> {
                if ((cameraPermissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                    onError("Разрешение на камеру необходимо для съемки фото")
                } else {
                    cameraPermissionState.launchPermissionRequest()
                }
            }
        }
    }
}