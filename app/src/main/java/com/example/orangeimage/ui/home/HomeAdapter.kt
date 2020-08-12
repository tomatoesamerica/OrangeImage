package com.example.orangeimage.ui.home

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.orangeimage.R
import com.example.orangeimage.db.ImageDatabase
import com.example.orangeimage.db.entity.Image
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeAdapter(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var imageCallback: ImageCallback
    lateinit var imageDB: ImageDatabase

    companion object {
        val VIEW_TYPE_ITEM = 0
        val VIEW_TYPE_LOADING = 1
    }

    init {
        imageDB = ImageDatabase.getInstance(context)
    }


    var imageOranges: ArrayList<UnsplashPhoto?>? = null


    fun setImageCallBack(imageCallback: ImageCallback) {
        this.imageCallback = imageCallback
    }

    fun setData(list: ArrayList<UnsplashPhoto?>) {
        imageOranges = list
//        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
            return ItemViewHolder(view, imageCallback)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading, parent, false)
            return LoadingViewHolder(
                view
            )
        }
    }

    override fun getItemCount(): Int {
        if (imageOranges == null)
            return 0
        else
            return imageOranges!!.size
    }

    override fun getItemViewType(position: Int): Int {
        if (imageOranges?.get(position) == null)
            return VIEW_TYPE_LOADING
        return VIEW_TYPE_ITEM
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            populateItemRows(holder, position)
        } else if (holder is LoadingViewHolder) {
            showLoadingView(holder, position)
        }
    }

    private class ItemViewHolder(itemView: View, var imageCallback: ImageCallback) :
        RecyclerView.ViewHolder(itemView) {
        var iv: ImageView
        var tv_load: TextView
        var bar_item: ProgressBar

        init {
            iv = itemView.findViewById(R.id.iv_server)
            tv_load = itemView.findViewById(R.id.tv_download)
            bar_item = itemView.findViewById(R.id.bar_image)
        }

        fun bind(item: UnsplashPhoto, position: Int, imageDB: ImageDatabase) {
            Glide.with(itemView.context).load(item.urls.thumb).into(iv)

            Log.d("001", "bind: " + position)

//            CoroutineScope(Dispatchers.Default).launch {
//                var list: List<Image> = imageDB.imageDAO().getAll()
//                for (ite in list) {
//                    if (TextUtils.equals(ite.url, item.id)) {
//                        Log.d("001", "bind save: " + position + "${ite.url}")
//                        bar_item.visibility = View.GONE
//                        tv_load.visibility = View.GONE
//
//                        break
//                    }
//                }
//            }

                imageDB.imageDAO().getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        Consumer { arrImage: List<Image> ->
                            for (ite in arrImage) {
                                if (TextUtils.equals(ite.url, item.id)) {
                                    Log.d("001", "bind save: " + position + "${ite.url}")
//                                Log.d("001", "bind: " + ite.uri + "     " + ite.url)
                                    tv_load.visibility = View.GONE
                                    bar_item.visibility = View.GONE
                                    break
                                }
                            }
                        }
                    )

            iv.setOnClickListener(View.OnClickListener {
                imageCallback.onSaveImage(item, position)
                bar_item.visibility = View.VISIBLE
            })
        }

    }

    private class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById(R.id.progressBar)
        }
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //ProgressBar would be displayed
    }

    private fun populateItemRows(viewHolder: ItemViewHolder, position: Int) {

        imageDB = ImageDatabase.getInstance(context)
        val item: UnsplashPhoto = imageOranges!!.get(position)!!
        viewHolder.bind(item, position, imageDB)

    }
}
