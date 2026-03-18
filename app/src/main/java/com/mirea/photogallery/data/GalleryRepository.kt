package com.mirea.photogallery.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GalleryRepository (private val context: Context) {
    private val picturesDir: File
        get() = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

    suspend fun loadAllPhotos(): List<Photo> = withContext(Dispatchers.IO) {
        picturesDir.listFiles { file ->
            file.extension == "jpg" || file.extension == "jpeg" || file.extension == "png"
        }?.mapNotNull { file -> Photo.fromFile(file) }
            ?.sortedByDescending { it.timestamp }
            ?: emptyList()
    }

    suspend fun savePhotoFromCamera(): Photo? = withContext(Dispatchers.IO) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_${timestamp}.jpg"
        val file = File(picturesDir, fileName)

        return@withContext try {
            file.createNewFile()
            Photo.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun exportToGallery(photo: Photo): Boolean = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // here is for new versions we are using MediaStore
            exportUsingMediaStore(photo)
        } else {
            exportLegacy(photo) // for old version we can copy the file
        }
    }

    private fun exportUsingMediaStore(photo: Photo): Boolean {
        return try {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, photo.fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PhotoGallery")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            ) ?: return false

            // coping file for see in the gallery
            context.contentResolver.openOutputStream(uri)?.use { output ->
                photo.file.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @Suppress("DEEPRECATION")
    private fun exportLegacy(photo: Photo): Boolean {
        return try {
            val picturesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            )
            val appDir = File(picturesDir, "PhotoGallery")
            if (!appDir.exists()) {
                appDir.mkdirs()
            }

            val destFile = File(appDir, photo.fileName)
            photo.file.inputStream().use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            context.sendBroadcast(android.content.Intent(
                android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(destFile)
            ))

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getCameraOutputFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_${timestamp}.jpg"
        return File(picturesDir, fileName)
    }
}