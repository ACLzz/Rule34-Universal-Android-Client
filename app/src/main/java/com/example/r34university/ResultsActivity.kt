package com.example.r34university

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.MultiAutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.example.r34university.databinding.ResultsActivityBinding
import com.google.android.flexbox.*
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.tags_search_fragment.*
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList


class ImageItem(
    var thumb: String? = "",
    var detail: String? = "",
    var full: String? = "") : Parcelable {
    var tags = listOf<String>()

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(thumb)
        parcel.writeString(detail)
        parcel.writeString(full)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageItem> {
        override fun createFromParcel(parcel: Parcel): ImageItem {
            return ImageItem(parcel)
        }

        override fun newArray(size: Int): Array<ImageItem?> {
            return arrayOfNulls(size)
        }
    }
}

interface Communicator {
    fun passSearchResults(results: List<ImageItem>)
    fun passPagesCount(count: Int)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

class ResultsActivity : AppCompatActivity(), Communicator {
    private lateinit var binding: ResultsActivityBinding

    private val items = ArrayList<ImageItem>()
    private var searchStack = Stack<String>()
    private val forceSearchLock = ReentrantLock()
    private lateinit var searchFragmentObj: SearchFragment
    private lateinit var tagsSearchField: MultiAutoCompleteTextView
    private lateinit var customAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        ConfigRepo.perfs = getSharedPreferences(cfgFileName, Context.MODE_PRIVATE)

        binding = ResultsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        searchFragmentObj = supportFragmentManager.findFragmentById(R.id.results_search_fragment)!! as SearchFragment
        tagsSearchField = searchFragmentObj.tagsSearchFragment.tags_field

        val lm = FlexboxLayoutManager(FlexDirection.ROW)
        lm.flexWrap = FlexWrap.WRAP
        lm.alignItems = AlignItems.CENTER
        lm.justifyContent = JustifyContent.SPACE_BETWEEN

        customAdapter = ImageAdapter(items, ::showFull)
        binding.resultsView.apply {
            adapter = customAdapter
            layoutManager = lm
        }

        val searchRequest = intent.getStringExtra("search")
        searchRequest?.let {
            forceSearch(searchRequest)
            searchStack.add(searchRequest)
        }

        val searchResults = intent.getParcelableArrayListExtra<ImageItem>("posts")
        searchResults?.let {
            passSearchResults(searchResults)
        }

        val pagesCount = intent.getIntExtra("pageCount", 1)
        if (pagesCount > 1)
            passPagesCount(pagesCount)
    }

    private fun forceSearch(searchRequest: String) {
        tagsSearchField.setText(searchRequest)
        try {
            forceSearchLock.lock()
            searchFragmentObj.search()
        } finally {
            forceSearchLock.unlock()
        }
    }

    private fun showFull(imageId: Int): Unit {
        val i = Intent(this, DetailActivity::class.java).apply {
            putExtra("imageId", imageId)
            putExtra("results", items)
        }
        startActivity(i)
    }

    fun hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    override fun passSearchResults(results: List<ImageItem>) {
        if (!forceSearchLock.isLocked) {
            searchStack.add(tagsSearchField.text.toString())
        }

        items.clear()
        items.addAll(results)
        customAdapter.notifyDataSetChanged()
        binding.resultsView.scrollToPosition(0)
    }

    override fun passPagesCount(count: Int) {
        val pageBar = supportFragmentManager.findFragmentByTag("page_bar_results_fragment_tag") as PageBarFragment
        pageBar.pageCount = count
        pageBar.currentPage = 1
        pageBar.updatePageList()
    }

    override fun onBackPressed() {
        if (!searchStack.empty()) {
            val prevSearch = searchStack.pop()
            val currentSearch = tagsSearchField.text.toString()
            if (prevSearch != currentSearch) {
                forceSearch(prevSearch)
                return
            }
        }

        if (searchStack.empty())
            super.onBackPressed()
        else
            onBackPressed()
    }
}