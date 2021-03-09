package com.example.r34university

import android.content.Intent
import android.os.Bundle
import android.text.*
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.MultiAutoCompleteTextView.Tokenizer
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import com.example.r34university.databinding.SearchFragmentBinding
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.tags_search_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import parsers.ContentParser
import kotlin.concurrent.thread


class SearchFragment : Fragment() {
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var communicator: Communicator
    private lateinit var searchField: MultiAutoCompleteTextView
    lateinit var tagsSearchFragment: TagsSearchFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchButton.setOnClickListener {
            search()
        }
        communicator = activity as Communicator
        tagsSearchFragment = childFragmentManager.findFragmentByTag("tags_search_fragment_fragment") as TagsSearchFragment

        searchField = tagsSearchFragment.tags_field
        searchField.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                search()
                return@OnKeyListener true
            }
            false
        })
    }

    fun hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun search() {
        val searchRequest = searchField.text.toString()

        if (communicator !is ResultsActivity) {
            if (searchRequest.isEmpty())
                return

            // starting new activity and passing search request
            val i = Intent(activity, ResultsActivity::class.java).apply {
                putExtra("search", searchRequest)
            }
            startActivity(i)
            return
        }

        val items = ContentParser.search(searchRequest)
        val pageCount = ContentParser.getPagesCount(searchRequest)

        communicator.passSearchResults(items)
        communicator.passPagesCount(pageCount)

        hideKeyboard()
        view?.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
