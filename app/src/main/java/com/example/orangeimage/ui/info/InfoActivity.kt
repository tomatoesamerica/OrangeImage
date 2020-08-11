package com.example.orangeimage.ui.info

import android.annotation.SuppressLint
import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.orangeimage.R
import com.example.orangeimage.ui.list.ListImageAdapter
import com.example.orangeimage.utils.Constant
import kotlinx.android.synthetic.main.activity_info.*
import java.io.File

class InfoActivity : AppCompatActivity() {
    lateinit var value: String;
    val DELETE_PERMISSION_REQUEST = 9901

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Glide.with(this).load(Constant.URI)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(iv_info)
        } else {
            val intent = getIntent()
            value = intent.getStringExtra(Constant.KEY_URL)
            Glide.with(this).load(value)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(iv_info)
        }

        btn_delete.setOnClickListener(View.OnClickListener {
//            deleteImage(value)
            delete()
        })
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun delete() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkDeletedialog(this, Constant.URI, null)
        } else {
            checkDeletedialog(this, null, value)
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkDeletedialog(context: Context, uri: Uri?, url: String?) {
        val alertDialogBuilder =
            AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure,You wanted to make decision")
        alertDialogBuilder.setPositiveButton("yes", object : DialogInterface.OnClickListener {
            override fun onClick(arg0: DialogInterface?, arg1: Int) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    uri?.let { deleteAndroidQ(context, it) }
                    onBackPressed()
                } else {
                    url?.let { deleteImage(it) }
                    onBackPressed()
                }


            }
        })

        alertDialogBuilder.setNegativeButton("No", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
//                finish()
            }
        })

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private var removeUri = Uri.EMPTY

    @RequiresApi(Build.VERSION_CODES.Q)
    fun deleteAndroidQ(context: Context, uri: Uri) {

        try {
            uri.let {
                context.contentResolver.delete(uri, null, null)
                Log.d("test", "Removed MediaStore: $it")
            }

//                (rv_file_list.adapter as MediaFileAdepter).setFileList(getFileList(this, curType))

        } catch (@SuppressLint("NewApi") e: RecoverableSecurityException) {
            val intentSender = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                e.userAction.actionIntent.intentSender
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            intentSender?.let {
                ActivityCompat.startIntentSenderForResult(
                    context as Activity,
                    intentSender,
                    DELETE_PERMISSION_REQUEST,
                    null,
                    0,
                    0,
                    0,
                    null
                )
            }
        }
        removeUri = uri

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == DELETE_PERMISSION_REQUEST) {
            deleteAndroidQ(this, removeUri)
        }
    }

    fun deleteImage(url: String) {
        val fdelete = File(url)
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.d("001", "deleteImage: ")
                galleryAddPic(url)
            } else {
                Log.d("001", "file not Deleted: ")
            }
        }
    }

    private fun galleryAddPic(imagePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        sendBroadcast(mediaScanIntent)
    }
}