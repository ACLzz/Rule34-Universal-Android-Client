package com.example.r34university

import android.graphics.*
import android.os.Build
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.image_item.view.*

class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
    var thumbnail: ImageView = view.findViewById(R.id.image_thumbnail)
    var thumb = ""
    var detail = ""
    var full = ""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun bind(cachedThumb: Bitmap?) {
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_default_image_item)

        cachedThumb?: run {
            Glide
                .with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(thumb)
                .into(thumbnail)
        }
        cachedThumb?.let {
            thumbnail.setImageBitmap(cachedThumb)
        }

        this.itemView.image_thumbnail.clipToOutline = true
    }
}