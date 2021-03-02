package com.example.r34university

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.r34university.databinding.DetailActivityBinding

class DetailActivity: AppCompatActivity() {
    private lateinit var binding: DetailActivityBinding
    private lateinit var items: ArrayList<ImageItem>
    private var currentPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        items = intent.getParcelableArrayListExtra<ImageItem>("results")!!
        currentPos = intent.getIntExtra("imageId", 0)
        val image = items[currentPos]

        binding = DetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.prevButton.setOnClickListener {
            goPrevImage()
        }
        binding.nextButton.setOnClickListener {
            goNextImage()
        }

        showImage(image.full, image.detail)
    }

    private fun goNextImage() {
        if (currentPos == items.lastIndex)
            return

        currentPos++
        val image = items[currentPos]
        showImage(image.full, image.detail)
    }

    private fun goPrevImage() {
        if (currentPos == 0)
            return

        currentPos--
        val image = items[currentPos]
        showImage(image.full, image.detail)
    }

    private fun showImage(fullLink: String?, detailLink: String?) {
        // TODO get tags and author
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_default_image_item)

        Glide
            .with(applicationContext)
            .applyDefaultRequestOptions(requestOptions)
            .load(fullLink)
            .into(binding.fullImageView)
    }
}