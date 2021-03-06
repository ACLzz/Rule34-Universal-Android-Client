package com.example.r34university

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.r34university.databinding.SearchActivityBinding

class SearchActivity : AppCompatActivity(), Communicator {
    private lateinit var binding: SearchActivityBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = SearchActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun passSearchResults(results: List<ImageItem>) {}
    override fun passPagesCount(count: Int) {}
}