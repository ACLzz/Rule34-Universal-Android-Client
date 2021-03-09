package com.example.r34university

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.r34university.databinding.DefaultTagsActivityBinding
import kotlinx.android.synthetic.main.tags_search_fragment.*

class DefaultTagsActivity : AppCompatActivity() {
    private lateinit var binding: DefaultTagsActivityBinding
    private lateinit var tagsFragment: TagsSearchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        ConfigRepo.perfs = getSharedPreferences(cfgFileName, Context.MODE_PRIVATE)

        binding = DefaultTagsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tagsFragment = supportFragmentManager.findFragmentByTag("default_tags_field_fragment") as TagsSearchFragment
        tagsFragment.tags_field.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                save()
                return@OnKeyListener true
            }
            false
        })
        tagsFragment.tags_field.setText(ConfigRepo.defaultTags)
    }

    private fun save() {
        val tags = tagsFragment.tags_field.text.toString()
        ConfigRepo.defaultTags = tags
        hideKeyboard(currentFocus ?: View(this))
        tagsFragment.tags_field.clearFocus()
    }
}