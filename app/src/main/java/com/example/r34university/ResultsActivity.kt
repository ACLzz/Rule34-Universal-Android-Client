package com.example.r34university

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.r34university.databinding.ResultsActivityBinding
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.search_fragment.*


class ImageItem (
    val thumb: String,
    val detail: String,
    val full: String) {

    var id: Int = 0
}

interface Communicator {
    fun passSearchResults(results: List<ImageItem>)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

class ResultsActivity : AppCompatActivity(), Communicator {
    private lateinit var binding: ResultsActivityBinding

    private val items = ArrayList<ImageItem>()
    private lateinit var customAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ResultsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lm = FlexboxLayoutManager(FlexDirection.ROW)
        lm.flexWrap = FlexWrap.WRAP
        lm.alignItems = AlignItems.STRETCH

        customAdapter = ImageAdapter(items)
        binding.resultsView.apply {
            adapter = customAdapter
            layoutManager = lm
        }

        val searchRequest = intent.getStringExtra("search")
        searchRequest?.let {
            val searchFragment = supportFragmentManager.findFragmentById(R.id.results_search_fragment)!! as SearchFragment
            searchFragment.search_field.setText(searchRequest)
            searchFragment.search()
        }
    }

    fun hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    override fun passSearchResults(results: List<ImageItem>) {
        items.clear()
        items.addAll(results)
    }
}