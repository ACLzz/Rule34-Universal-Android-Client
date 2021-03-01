package com.example.r34university

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.r34university.databinding.SearchFragmentBinding

class SearchFragment : Fragment() {
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var communicator: Communicator

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
        binding.searchField.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
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
        communicator = activity as Communicator
        val searchRequest = binding.searchField.text.toString()

        if (communicator !is ResultsActivity) {
            if (searchRequest.isEmpty())
                return

            val i = Intent(activity, ResultsActivity::class.java).apply {
                putExtra("search", searchRequest)
            }
            startActivity(i)
            return
        }

        // FIXME
        var items = listOf<ImageItem>(
            ImageItem("https://rule34.xxx/thumbnails/3913/thumbnail_3c2496972f2a8747910d325aa0d091b0.jpg?4432376", "https://rule34.xxx/index.php?page=post&s=view&id=4432376", "https://rule34.xxx//samples/3913/sample_3c2496972f2a8747910d325aa0d091b0.jpg?4432376"),
            ImageItem("https://rule34.xxx/thumbnails/3900/thumbnail_2f249cbddb0c1cd5c3519c0839be4185.jpg?4415977", "https://rule34.xxx/index.php?page=post&s=view&id=4415977", "https://rule34.xxx//samples/3900/sample_2f249cbddb0c1cd5c3519c0839be4185.jpg?4415977"),
            ImageItem("https://rule34.xxx/thumbnails/3919/thumbnail_f7e7cca88cbfde9151ed0308c36ae465.jpg?4441529", "https://rule34.xxx/index.php?page=post&s=view&id=4441529", "https://wimg.rule34.xxx//images/3919/f7e7cca88cbfde9151ed0308c36ae465.jpeg?4441529"),
            ImageItem("https://rule34.xxx/thumbnails/3857/thumbnail_9a8ba8de77b842c8d084458c9b32ab72.jpg?4366508", "https://rule34.xxx/index.php?page=post&s=view&id=4366508", "https://rule34.xxx//samples/3857/sample_9a8ba8de77b842c8d084458c9b32ab72.jpg?4366508")
        )

        if (searchRequest == "less") {
            items = items.subList(0, 1)
        }

        communicator.passSearchResults(items)
        hideKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}