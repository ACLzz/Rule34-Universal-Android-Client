package com.example.r34university

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.r34university.databinding.SearchActivityBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: SearchActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SearchActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}