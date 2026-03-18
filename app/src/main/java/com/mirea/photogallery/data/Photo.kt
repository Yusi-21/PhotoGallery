package com.mirea.photogallery.data

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

data class Photo(
    val fileName: String,
    val path: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val displayName: String
        get() = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            .format(Date(timestamp))

    val file: File
        get() = File(path)

    companion object {
        fun fromFile(file: File): Photo? {
            return try {
                val fileName = file.name
                val timestamp = parseTimestampFromFileName(fileName) ?: file.lastModified()

                Photo(
                    fileName = fileName,
                    path = file.absolutePath,
                    timestamp = timestamp
                )
            } catch (e: Exception) {
                null
            }
        }

        private fun parseTimestampFromFileName(fileName: String): Long? {
            return try {
                val pattern = "IMG_(\\d{4})(\\d{2})(\\d{2})_(\\d{2})(\\d{2})(\\d{2})\\.jpg".toRegex()
                val matchResult = pattern.find(fileName)

                matchResult?.let {
                    val (year, month, day, hour, minute, second) = it.destructured
                    val dateStr = "$year-$month-$day $hour:$minute:$second"
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateStr)?.time
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}