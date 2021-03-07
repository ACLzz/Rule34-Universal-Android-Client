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
import com.example.r34university.databinding.SearchFragmentBinding
import kotlinx.android.synthetic.main.search_fragment.*
import parsers.ContentParser
import kotlin.concurrent.thread


class SearchFragment : Fragment() {
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var communicator: Communicator
    private lateinit var searchField: MultiAutoCompleteTextView
    private lateinit var tags: List<String>
    private lateinit var tagsAdapter: ArrayAdapter<String>

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

        searchField = binding.searchField
        searchField.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                search()
                return@OnKeyListener true
            }
            false
        })

        if (!ConfigRepo.useAutocompleteLoader) {
            thread { getTags() }.join()
        } else
            tags = arrayListOf("")

        if (ConfigRepo.useAutocompleteLoader) {
            val mWatcher = MyWatcher(::getTags, ::updateTags)
            searchField.addTextChangedListener(mWatcher)
        }

        tagsAdapter = ArrayAdapter(
            activity?.applicationContext!!,
            android.R.layout.simple_list_item_1,
            tags
        )
        searchField.setAdapter(tagsAdapter)
        searchField.setTokenizer(SpaceTokenizer())
    }

    fun hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun search() {
        val searchRequest = binding.searchField.text.toString()

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

    private fun getTags() {
        // getting tags list from site
        val formattedTag = searchField.text.toString().split(" ").last().replace(Regex("-"), "")
        tags = ContentParser.getTags(formattedTag)
    }

    private fun updateTags() {
        val adapter = searchField.adapter as ArrayAdapter<String>
        adapter.clear()
        tags.forEach { adapter.add(it) }

        adapter.filter.filter(searchField.text, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class SpaceTokenizer : Tokenizer {
    private val i = 0

    // Returns the start of the token that ends at offset cursor within text.
    override fun findTokenStart(inputText: CharSequence, cursor: Int): Int {
        var idx = cursor
        val text = formatText(inputText)

        while (idx > 0 && text[idx - 1] != ' ') {
            idx--
        }
        while (idx < cursor && text[idx] == ' ') {
            idx++
        }
        return idx
    }

    // Returns the end of the token (minus trailing punctuation) that
    // begins at offset cursor within text.
    override fun findTokenEnd(inputText: CharSequence, cursor: Int): Int {
        var idx = cursor
        val text = formatText(inputText)

        val length = text.length
        while (idx < length) {
            if (text[i] == ' ') {
                return idx
            } else {
                idx++
            }
        }
        return length
    }

    // Returns text, modified, if necessary, to ensure that it ends with a token terminator
    // (for example a space or comma).
    override fun terminateToken(inputText: CharSequence): CharSequence {
        val text = formatText(inputText)
        var idx = text.length
        while (idx > 0 && text[idx - 1] == ' ') {
            idx--
        }
        return if (idx > 0 && text[idx - 1] == ' ') {
            text
        } else {
            if (text is Spanned) {
                val sp = SpannableString("$text")
                TextUtils.copySpansFrom(
                    text, 0, text.length,
                    Any::class.java, sp, 0
                )
                sp
            } else {
                "$text"
            }
        }
    }

    private fun formatText(text: CharSequence) = text.replace(Regex("-"), " ") as CharSequence
}

class MyWatcher(private val getTags: () -> Unit, private val updateTags: () -> Unit): TextWatcher {
    private var prevSeq: CharSequence = ""
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        return
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        s?.let {
            val fLength = prevSeq.length
            val sLength = s.length
            if (fLength < sLength) {
                newTags()
            }
            prevSeq = s.toString()
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    private fun newTags() {
        // TODO async
        thread {
            getTags()
        }.join()
        updateTags()
    }

}