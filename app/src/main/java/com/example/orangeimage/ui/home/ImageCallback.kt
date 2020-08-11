package com.example.orangeimage.ui.home

import android.widget.ImageView
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto

interface ImageCallback {

    fun onSaveImage(unsplashPhoto: UnsplashPhoto, position:Int)
}