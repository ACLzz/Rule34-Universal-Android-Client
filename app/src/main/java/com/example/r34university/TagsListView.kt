package com.example.r34university

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.tag_item.view.*

class TagsListHolder(view: View) : RecyclerView.ViewHolder(view) {
    var tagText: TextView = view.findViewById(R.id.tag_text)
    var tagContainer: ConstraintLayout = view.findViewById(R.id.tag_container)
}

class TagsListAdapter (private val tagsList: List<String>, private val tagsColorList: List<Int>,
                       private val searchTag: (Int) -> Unit) : RecyclerView.Adapter<TagsListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsListHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.tag_item,
            parent,
            false
        )
        return TagsListHolder(itemView)
    }

    override fun onBindViewHolder(holder: TagsListHolder, position: Int) {
        val tag = tagsList[position]
        // TODO change background color and text white level
        holder.itemView.tag_text.text = tag
        val tagsBackground = holder.itemView.tag_text.background as GradientDrawable
        tagsBackground.setColor(pickColor(tag))

        holder.itemView.setOnClickListener { searchTag(position) }
    }

    override fun getItemCount(): Int {
        return tagsList.size
    }

    private fun pickColor(tagName: String) : Int {
        val hash = tagName.hashCode().let { if (it < 0) {-it} else {it} }
        return tagsColorList[hash % (tagsColorList.size - 1)]
    }
}