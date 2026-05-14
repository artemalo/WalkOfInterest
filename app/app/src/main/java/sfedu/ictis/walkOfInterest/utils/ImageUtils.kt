package sfedu.ictis.walkOfInterest.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun Context.getBitmapDataFromUri(uri: Uri): Pair<ByteArray, String>? = withContext(Dispatchers.IO) {
    try {
        val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: return@withContext null

        val mime = contentResolver.getType(uri) ?: "image/jpeg"
        val ext = when (mime) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "jpg"
        }

        bytes to ext
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}