package com.example.orangeimage.ui.list

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orangeimage.R
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_list_image.*
import java.util.*
import kotlin.collections.ArrayList

class ListImageActivity : AppCompatActivity() {

    lateinit var adapter: ListImageAdapter
    val compositeDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_image)
        loadData()
    }

    fun loadData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setUpAdapter(null, getListImageAndroidQ())

//            for (item in getListImageAndroidQ())
//                Log.d("001", "uri:  "+ item)
        } else {
            setUpAdapter(getListImage(), null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun setUpAdapter(photos: ArrayList<String>?, uris: ArrayList<Uri>?) {

        if (photos == null) {
            adapter = ListImageAdapter(null, uris, this)
        } else {
            adapter = ListImageAdapter(photos, null, this)
        }
        rv_garally.adapter = adapter
        rv_garally.layoutManager = GridLayoutManager(this, 2)
        rv_garally.setHasFixedSize(true)
    }

    fun getListImage(): ArrayList<String> {
        val images: ArrayList<String> = ArrayList<String>()
        images.clear()
        val uri: Uri
        val cursor: Cursor?
        var absolutePathOfImage: String? = null
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        cursor = applicationContext.contentResolver
            .query(uri, projection, null, null, "$orderBy DESC")
        while (cursor!!.moveToNext()) {
            absolutePathOfImage =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            Log.e("Column", absolutePathOfImage)
            images.add(absolutePathOfImage)
        }

        return images
    }

    fun getListImageAndroidQ(): ArrayList<Uri> {

//        GlobalScope.launch {
        val listUris = ArrayList<Uri>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateTakenColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val dateTaken = Date(cursor.getLong(dateTakenColumn))
                val displayName = cursor.getString(displayNameColumn)
//                val contentUri = Uri.withAppendedPath(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    id.toString()
//                )
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                listUris.add(contentUri)

                Log.d(
                    "001", "id: $id, display_name: $displayName, date_taken: " +
                            "$dateTaken, content_uri: $contentUri"
                )
            }
        }
        return listUris
//        }
    }
}