package com.example.orangeimage.ui.list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.orangeimage.R
import com.example.orangeimage.ui.info.InfoActivity
import com.example.orangeimage.utils.Constant
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto

class ListImageAdapter(images: ArrayList<String>?, imagesUri: ArrayList<Uri>?, context: Context) :
    RecyclerView.Adapter<ListImageAdapter.ViewHolder>() {

    var images: ArrayList<String> = ArrayList<String>()
    var imagesUri: ArrayList<Uri> = ArrayList<Uri>()
    var mContext: Context? = null

    init {
        if (images != null) {
            this.images = images
        }
        if (imagesUri != null) {
            this.imagesUri = imagesUri
        }
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val view: View = inflater.inflate(R.layout.item_list_image, parent, false)
        return ViewHolder(view, context)
    }


    override fun getItemCount(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return imagesUri.size
        } else {
            return images.size
        }
    }

    class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView

        init {
            ivImage = itemView.findViewById(R.id.iv_picture)
        }
        fun bind(item: String?, uri: Uri?, position: Int) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Glide.with(itemView.context).load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ivImage)
            } else {
                Glide.with(itemView.context).load(item)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ivImage)
            }
        }
    }

    override fun onBindViewHolder(holder: ListImageAdapter.ViewHolder, position: Int) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            holder.bind(null,imagesUri[position], position)
        } else {
            holder.bind(images[position],null, position)
        }
        holder.ivImage.setOnClickListener(View.OnClickListener {
            val intent = Intent(mContext, InfoActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Constant.URI = imagesUri[position]
            } else {
                intent.putExtra(Constant.KEY_URL, images[position])
            }
            mContext!!.startActivity(intent)
        })


    }
}