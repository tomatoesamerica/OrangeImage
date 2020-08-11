package com.unsplash.pickerandroid.photopicker.data

import android.annotation.SuppressLint
import android.os.Parcelable


data class UnsplashLinks(
    val self: String,
    val html: String,
    val photos: String?,
    val likes: String?,
    val portfolio: String?,
    val download: String?,
    val download_location: String?
)
