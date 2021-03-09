package com.example.r34university

import android.R
import android.os.Bundle
import android.text.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.fragment.app.Fragment
import com.example.r34university.databinding.TagsSearchFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import parsers.ContentParser
import kotlin.concurrent.thread

class TagsSearchFragment() : Fragment() {
    private var _binding: TagsSearchFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchField: MultiAutoCompleteTextView
    private lateinit var tags: List<String>
    private lateinit var tagsAdapter: ArrayAdapter<String>
    private val spaceTokenizer = SpaceTokenizer()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TagsSearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchField = binding.tagsField
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
            R.layout.simple_list_item_1,
            tags
        )
        searchField.setAdapter(tagsAdapter)
        searchField.setTokenizer(spaceTokenizer)
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
        val text = searchField.text.split(" ").last().replace(Regex("-"), "")

        adapter.filter.filter(text, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class SpaceTokenizer : MultiAutoCompleteTextView.Tokenizer {
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

    fun formatText(text: CharSequence) = text.replace(Regex("-"), " ") as CharSequence
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
        GlobalScope.launch {
            suspend {
                getTags()
                withContext(Dispatchers.Main) {
                    updateTags()
                }
            }.invoke()
        }
    }

}