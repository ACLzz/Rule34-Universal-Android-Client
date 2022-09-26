package com.example.r34university

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.r34university.databinding.DetailActivityBinding
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import parsers.ContentParser
import kotlin.math.abs

const val SWIPE_THRESHOLD = 120
const val SWIPE_VELOCITY_THRESHOLD = 120

class DetailActivity: AppCompatActivity(), Communicator, GestureDetector.OnGestureListener{
    private lateinit var binding: DetailActivityBinding
    private lateinit var items: ArrayList<ImageItem>
    private lateinit var customTagsAdapter: TagsListAdapter
    private lateinit var communicator: Communicator
    private lateinit var mDetector: GestureDetectorCompat

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
        mDetector = GestureDetectorCompat(this, this)

        showImage()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (mDetector.onTouchEvent(ev)) {
            true
        } else {
            super.dispatchTouchEvent(ev)
        }
    }

    override fun onLongPress(event: MotionEvent) {}
    override fun onDown(event: MotionEvent): Boolean { return false }
    override fun onShowPress(event: MotionEvent) {}
    override fun onScroll(event1: MotionEvent, event2: MotionEvent, distanceX: Float, distanceY: Float): Boolean { return false }
    override fun onSingleTapUp(event: MotionEvent): Boolean { return false }
    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        var result = false
        try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        goPrevImage()
                    } else {
                        goNextImage()
                    }
                    result = true
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return result
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
            putExtra("prevSearch", ContentParser.currentSearch)
        }
        startActivity(i)
    }

    override fun passSearchResults(results: List<ImageItem>) {}
    override fun passPagesCount(count: Int) {}
}
