package com.example.r34university

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.r34university.databinding.DetailActivityBinding
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import parsers.ContentParser

class DetailActivity: AppCompatActivity(), Communicator {
    private lateinit var binding: DetailActivityBinding
    private lateinit var items: ArrayList<ImageItem>
    private lateinit var customTagsAdapter: TagsListAdapter
    private lateinit var communicator: Communicator

    private var currentPos = 0
    private val currentImage: ImageItem get() = items[currentPos]
    private var tagsList = listOf<String>()
    private lateinit var tagsColorList: List<Int>

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        ConfigRepo.perfs = getSharedPreferences(cfgFileName, Context.MODE_PRIVATE)

        items = intent.getParcelableArrayListExtra<ImageItem>("results")!!
        currentPos = intent.getIntExtra("imageId", 0)
        ContentParser.getDetails(currentImage)
        tagsList = currentImage.tags

        binding = DetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tagsColorList = listOf(
            ContextCompat.getColor(applicationContext, R.color.redTagColor),
            ContextCompat.getColor(applicationContext, R.color.purpleTagColor),
            ContextCompat.getColor(applicationContext, R.color.navyTagColor),
            ContextCompat.getColor(applicationContext, R.color.blueTagColor),
            ContextCompat.getColor(applicationContext, R.color.greenTagColor),
            ContextCompat.getColor(applicationContext, R.color.yellowTagColor),
            ContextCompat.getColor(applicationContext, R.color.orangeTagColor),
        )

        // FIXME recyclerview not resizing and scrolling when it mustn't
        val lm = FlexboxLayoutManager(FlexDirection.ROW)
        lm.flexWrap = FlexWrap.WRAP
        lm.alignItems = AlignItems.STRETCH

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
        binding.tagsPlaceholder.setOnClickListener { if (binding.tagsLayout.visibility == ViewGroup.VISIBLE) hideTags() else showTags() }
        binding.tagsViewToggler.setOnClickListener { showTags() }

        showImage()
    }

    private fun goNextImage() {
        if (currentPos == items.lastIndex)
            return

        currentPos++
        showImage()
    }

    private fun goPrevImage() {
        if (currentPos == 0)
            return

        currentPos--
        showImage()
    }

    private fun showTags() {
        val tags = binding.tagsLayout
        val tagsToggler = binding.tagsViewToggler

        tags.visibility = View.VISIBLE
        tagsToggler.visibility = View.GONE
    }

    private fun hideTags() {
        val tags = binding.tagsLayout
        val tagsToggler = binding.tagsViewToggler

        tags.visibility = View.GONE
        tagsToggler.visibility = View.VISIBLE
    }

    private fun showImage() {
        ContentParser.getDetails(currentImage)
        tagsList = currentImage.tags
        customTagsAdapter = TagsListAdapter(tagsList, tagsColorList, ::searchTag)
        binding.tagsList.swapAdapter(customTagsAdapter, false)

        hideTags()

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_default_image_item)

        Glide
            .with(applicationContext)
            .applyDefaultRequestOptions(requestOptions)
            .load(currentImage.full)
            .into(binding.fullImageView)
    }

    private fun searchTag(tagId: Int) {
        val tag = tagsList[tagId]

        communicator = this
        // starting new activity and passing search request
        val i = Intent(this, ResultsActivity::class.java).apply {
            putExtra("search", tag)
        }
        startActivity(i)
    }

    override fun passSearchResults(results: List<ImageItem>) {}
    override fun passPagesCount(count: Int) {}
}