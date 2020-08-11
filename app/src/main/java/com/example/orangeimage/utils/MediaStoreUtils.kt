package com.example.orangeimage.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.OutputStream

object MediaStoreUtils {

    val FOLDER_IMAGE = "OrangeImage"

    fun saveImage(bitmap: Bitmap, context: Context, name: String): Uri {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.DISPLAY_NAME, name)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + FOLDER_IMAGE)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? =
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
            return Uri.parse(uri.toString())
        } else {
            // Save image to gallery
            val savedImageURL = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                bitmap,
                name,
                "Image of $name"
            )

            // Parse the gallery image url to uri
            return Uri.parse(savedImageURL)
        }
    }

    private fun contentValues(): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}