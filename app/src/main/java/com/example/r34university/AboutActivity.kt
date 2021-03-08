package com.example.r34university

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.r34university.databinding.AboutActivityBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: AboutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        ConfigRepo.perfs = getSharedPreferences(cfgFileName, Context.MODE_PRIVATE)

        binding = AboutActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}