package com.example.r34university

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.r34university.databinding.DetailActivityBinding
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.detail_activity.view.*
import kotlin.math.abs

class DetailActivity: AppCompatActivity(), Communicator {
    private lateinit var binding: DetailActivityBinding
    private lateinit var items: ArrayList<ImageItem>
    private lateinit var customTagsAdapter: TagsListAdapter
    private lateinit var communicator: Communicator

    private var currentPos = 0
    private val currentImage: ImageItem get() = items[currentPos]
    private val tagsList get() = currentImage.tags
    private lateinit var tagsColorList: List<Int>

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        items = intent.getParcelableArrayListExtra<ImageItem>("results")!!
        currentPos = intent.getIntExtra("imageId", 0)
        getDetails()

        binding = DetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lm = FlexboxLayoutManager(FlexDirection.ROW)
        lm.flexWrap = FlexWrap.WRAP
        lm.alignItems = AlignItems.STRETCH

        tagsColorList = listOf(
            ContextCompat.getColor(applicationContext, R.color.redTagColor),
            ContextCompat.getColor(applicationContext, R.color.purpleTagColor),
            ContextCompat.getColor(applicationContext, R.color.navyTagColor),
            ContextCompat.getColor(applicationContext, R.color.blueTagColor),
            ContextCompat.getColor(applicationContext, R.color.greenTagColor),
            ContextCompat.getColor(applicationContext, R.color.yellowTagColor),
            ContextCompat.getColor(applicationContext, R.color.orangeTagColor),
        )

        customTagsAdapter = TagsListAdapter(tagsList, tagsColorList, ::searchTag)
        binding.tagsList.apply {
            adapter = customTagsAdapter
            layoutManager = lm
        }

        binding.prevButton.setOnClickListener {
            goPrevImage()
        }
        binding.nextButton.setOnClickListener {
            goNextImage()
        }

        binding.tagsLayout.setOnClickListener { hideTags() }
        binding.tagsView.setOnClickListener { hideTags() }
        binding.tagsViewToggler.setOnClickListener { showTags() }

        showImage(currentImage.full, currentImage.detail)
    }

    private fun getDetails() {
        // TODO parsing
        // FIXME
        currentImage.detail = currentImage.detail
        currentImage.full = currentImage.full

        currentImage.tags = listOf("dark_souls", "tits", "girl")
    }

    private fun goNextImage() {
        if (currentPos == items.lastIndex)
            return

        currentPos++
        getDetails()
        showImage(currentImage.full, currentImage.detail)
    }

    private fun goPrevImage() {
        if (currentPos == 0)
            return

        currentPos--
        getDetails()
        showImage(currentImage.full, currentImage.detail)
    }

    private fun showTags() {
        val tags = binding.tagsView
        val tagsToggler = binding.tagsViewToggler

        tags.visibility = View.VISIBLE
        tagsToggler.visibility = View.GONE
    }

    private fun hideTags() {
        val tags = binding.tagsView
        val tagsToggler = binding.tagsViewToggler

        tags.visibility = View.GONE
        tagsToggler.visibility = View.VISIBLE
    }

    private fun showImage(fullLink: String?, detailLink: String?) {
        hideTags()
        customTagsAdapter.notifyDataSetChanged()

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_default_image_item)

        Glide
            .with(applicationContext)
            .applyDefaultRequestOptions(requestOptions)
            .load(fullLink)
            .into(binding.fullImageView)
    }

    private fun searchTag(tagId: Int) {
        val tag = tagsList[tagId]

        communicator = this as Communicator
        // starting new activity and passing search request
        val i = Intent(this, ResultsActivity::class.java).apply {
            putExtra("search", tag)
        }
        startActivity(i)
    }

    override fun passSearchResults(results: List<ImageItem>) {}
}