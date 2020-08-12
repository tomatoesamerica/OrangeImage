package com.example.orangeimage.ui.home

data class PhotoItem(val thumbnail:String, val raw :String){

    fun isDownloaded() : Boolean{

        return false;
    }
}