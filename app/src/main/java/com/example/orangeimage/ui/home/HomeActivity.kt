package com.example.orangeimage.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.orangeimage.R
import com.example.orangeimage.db.ImageDatabase
import com.example.orangeimage.db.entity.Image
import com.example.orangeimage.ui.list.ListImageActivity
import com.example.orangeimage.utils.MediaStoreUtils
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlincodes.com.retrofitwithkotlin.retrofit.ApiClient
import kotlinx.android.synthetic.main.activity_home.*
import java.io.OutputStream

class HomeActivity : AppCompatActivity(), ImageCallback {

    lateinit var imageDB: ImageDatabase
    private val READ_EXTERNAL_STORAGE_REQUEST = 0x1045
    val compositeDisposable = CompositeDisposable()
    lateinit var adapter: HomeAdapter
    var page = 1
    var images: ArrayList<UnsplashPhoto?> = ArrayList<UnsplashPhoto?>()

    var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        imageDB = ImageDatabase.getInstance(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission()
        } else {
            init()
        }

        fab.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ListImageActivity::class.java)
            startActivity(intent)

        })


        imageDB.imageDAO().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                Consumer { arrImage: List<Image> ->
                    for (item in arrImage)
                        Log.d(
                            "001",
                            "onCreate: image url" + item.id + "  " + item.url + "  " + item.uri
                        )
                    Log.d("001", "onCreate:22222222222222222 ")
                }

            )


    }

    fun init() {
        setUpAdapter()
        getData()
        initScrollListener()
    }

    fun getData() {
        compositeDisposable.add(
            ApiClient.getClient.getPhotos(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { response -> onResponse(response) },
                    { t -> onFailure(t) })
        )
    }

    private fun onFailure(t: Throwable) {
        Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
        Log.d("001", "onFailure: " + t.message)
    }

    private fun onResponse(list: ArrayList<UnsplashPhoto?>) {
        progress_bar.visibility = View.GONE
        images.addAll(list)
        adapter.setData(images)
    }

    private fun onResponseLoadmore(list: ArrayList<UnsplashPhoto?>) {
        images.addAll(list)
        adapter.setData(images)
        isLoading = false
    }


    fun initScrollListener() {
        rv_image.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var gridLayoutManager: GridLayoutManager =
                    rv_image.layoutManager as GridLayoutManager

                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (adapter.getItemViewType(position)) {
                            1 -> 2
                            0 -> 1
                            else -> -1
                        }

                    }
                }

                if (!isLoading) {
                    if (gridLayoutManager != null && gridLayoutManager.findLastCompletelyVisibleItemPosition() == images.size - 1) {
                        isLoading = true
                        loadMore()
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    fun setUpAdapter() {
        adapter = HomeAdapter(this)
        adapter.setImageCallBack(this)
        rv_image.adapter = adapter
        rv_image.layoutManager = GridLayoutManager(this, 2)
        rv_image.setHasFixedSize(true)
    }

    private fun loadMore() {
        images.add(null)
        adapter.notifyItemInserted(images.size - 1)
        val handler = Handler()
        handler.postDelayed({
            images.removeAt(images.size - 1)
            adapter.notifyItemRemoved(images.size)
            page++;
            compositeDisposable.add(
                ApiClient.getClient.getPhotos(page)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        { response -> onResponseLoadmore(response) },
                        { t -> onFailure(t) })
            )


        }, 2000)
    }


    private fun goToSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        ).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    READ_EXTERNAL_STORAGE_REQUEST
                )
            } else {
                init()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init()
                } else {

                    val showRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    if (!showRationale) {
                        goToSettings()
                    } else {
                        finishAffinity()
                    }
                }
                return
            }
        }
    }

    @SuppressLint("ResourceType")
    override fun onSaveImage(unsplashPhoto: UnsplashPhoto, position: Int) {
        val handler = Handler()
        handler.postDelayed(Runnable {
            Glide.with(applicationContext)
                .asBitmap()
                .load(unsplashPhoto.urls.thumb)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap, transition: Transition<in Bitmap?>?
                    ) {

                        // save inmage for mediastore
                        var uri: Uri = MediaStoreUtils.saveImage(
                            resource,
                            applicationContext,
                            unsplashPhoto.id
                        )

                        // save image for db
                        imageDB.imageDAO().insert(Image(0, unsplashPhoto.id, uri.toString()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                Log.d("001", "insert success")
                                adapter.notifyItemChanged(position)
                            }, { error -> Log.e("001", "insert false", error) })

//                        adapter.notifyItemChanged(position)

                        Toast.makeText(
                            applicationContext,
                            "Save image Succsufl !",
                            Toast.LENGTH_SHORT
                        )
                            .show()
//                        adapter.setData(synchronizeData(images))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }, 1000)
    }

}