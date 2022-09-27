package com.example.r34university

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GestureDetectorCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.example.r34university.databinding.DetailActivityBinding
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import parsers.ContentParser
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
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
        binding.downloadButton.setOnClickListener { downloadImage() }
        mDetector = GestureDetectorCompat(this, this)

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

        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(applicationContext, R.color.fullWhite))
        circularProgressDrawable.start()

        val requestOptions = RequestOptions()
            .placeholder(circularProgressDrawable)
            .error(R.drawable.ic_default_image_item)

        Glide
            .with(applicationContext)
            .applyDefaultRequestOptions(requestOptions)
            .load(currentImage.full)
            .transition(withCrossFade())
            .into(binding.fullImageView)
    }

    private fun downloadImage() {
        val image = binding.fullImageView.drawable.toBitmap()
        val filename = currentImage.detail?.split("=")?.last()
        val path = Environment.DIRECTORY_PICTURES + "/Rule34Universe"
        var fos: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, path)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        put(MediaStore.MediaColumns.ALBUM, "Rule34Universe")
                    }
                }

                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(path)
            val f = File(imagesDir, filename)
            fos = FileOutputStream(f)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            image.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_LONG).show()
        }
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
}
