package com.mhwan.wannabeimagecomponent.motionview.utils

import android.net.Uri
import android.provider.MediaStore

import android.os.Build

import android.content.ContentResolver
import android.content.Context

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import java.lang.Exception


object ImageUtils {
    fun getBitmapFromUri(context: Context, imageUri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        val contentResolver: ContentResolver = context.contentResolver
        try {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            } else {
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }
}