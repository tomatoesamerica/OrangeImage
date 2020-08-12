package com.example.orangeimage.db.dao

import android.provider.ContactsContract
import androidx.room.*
import com.example.orangeimage.db.entity.Image
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface ImageDAO {
    @Query("SELECT * FROM images WHERE id = :id")
    fun getUserById(id: Int): Flowable<Image>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insert(image: Image)
    fun insert(image: Image): Completable

    @Query("SELECT * FROM images")
//    fun getAll(): List<Image>
    fun getAll(): Flowable<List<Image>>

    @Delete
    fun delete(image: Image)

    @Query("DELETE FROM images")
    fun deleteAllImage()

    @Update
    fun update(image: Image): Completable
}