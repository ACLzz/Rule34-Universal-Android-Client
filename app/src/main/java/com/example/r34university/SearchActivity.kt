package com.example.r34university

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.r34university.databinding.SearchActivityBinding

class SearchActivity : AppCompatActivity(), Communicator {
    private lateinit var binding: SearchActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = SearchActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun passSearchResults(results: List<ImageItem>) {}
}