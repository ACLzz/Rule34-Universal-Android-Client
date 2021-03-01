package com.example.r34university

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.HashMap


class ImageAdapter(private val itemsList: List<ImageItem>) : RecyclerView.Adapter<ImageHolder>() {
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
        val thumbnail: Bitmap? = thumbCache[item.id]
        holder.thumb = item.thumb
        holder.detail = item.detail
        holder.full = item.full

        holder.bind(thumbnail)
        holder.itemView.setOnClickListener { showFull(item.detail, item.full) }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
    
    private fun showFull(detailLink: String, fullLink: String) {
        return
    }
}