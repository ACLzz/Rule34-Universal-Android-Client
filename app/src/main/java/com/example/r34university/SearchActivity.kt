package com.example.r34university

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.r34university.databinding.SearchActivityBinding
import kotlinx.android.synthetic.main.search_fragment.*
import parsers.ContentParser
import java.util.ArrayList

class SearchActivity : AppCompatActivity(), Communicator {
    private lateinit var binding: SearchActivityBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        ConfigRepo.perfs = getSharedPreferences(cfgFileName, Context.MODE_PRIVATE)

        binding = SearchActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.postsLabel.setOnClickListener {
            val i = Intent(this, ResultsActivity::class.java).apply {
                putParcelableArrayListExtra("posts", ContentParser.getAllPosts() as ArrayList<ImageItem>)
                putExtra("pageCount", ContentParser.getAllPostsPagesCount())
            }
            startActivity(i)
        }

        binding.tagsLabel.setOnClickListener {
            // TODO
        }

        binding.aboutLabel.setOnClickListener {
            val i = Intent(this, AboutActivity::class.java)
            startActivity(i)
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.parsers_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.parserSpinner.adapter = adapter
            binding.parserSpinner.setSelection(adapter.getPosition(ConfigRepo.source.toSiteLink()))
        }

        binding.parserSpinner.onItemSelectedListener = ParserSelect()
    }

    override fun passSearchResults(results: List<ImageItem>) {}
    override fun passPagesCount(count: Int) {}
}


class ParserSelect : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        ConfigRepo.source = toSiteObj(parent?.getItemAtPosition(position).toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

}