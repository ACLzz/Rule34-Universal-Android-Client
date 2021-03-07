package com.example.r34university

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.HashMap
import kotlin.coroutines.coroutineContext


class ImageAdapter(private val itemsList: List<ImageItem>, private val showFull: (Int) -> Unit) : RecyclerView.Adapter<ImageHolder>() {
    private val thumbCache = HashMap<Int, Bitmap>() // TODO cache for thumbnails

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.image_item,
            parent,
            false
        )
        return ImageHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        val item = itemsList[position]
        val thumbnail: Bitmap? = thumbCache[item.detail.hashCode()] // TODO
        holder.thumb = item.thumb.toString()
        holder.detail = item.detail.toString()
        holder.full = item.full.toString()

        holder.bind(thumbnail)
        if (item.detail != null && item.full != null)
            holder.itemView.setOnClickListener { showFull(position) }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}