package com.example.orangeimage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class Image(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "url") var url: String,
    @ColumnInfo(name = "uri") var uri: String
) {
}