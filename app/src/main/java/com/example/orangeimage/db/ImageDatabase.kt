package com.example.orangeimage.db

import android.content.Context
import android.provider.ContactsContract
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.orangeimage.db.dao.ImageDAO
import com.example.orangeimage.db.entity.Image


@Database(entities = [Image::class], version = 1)
abstract class ImageDatabase : RoomDatabase() {

    abstract fun imageDAO(): ImageDAO

    companion object {

        @Volatile
        private var INSTANCE: ImageDatabase? = null

        fun getInstance(context: Context): ImageDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ImageDatabase::class.java, "Image.db"
            ).build()
    }

}