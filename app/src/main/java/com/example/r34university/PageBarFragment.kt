package com.example.r34university

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.r34university.databinding.PageBarFragmentBinding
import kotlinx.android.synthetic.main.tag_item.view.*
import parsers.ContentParser

class PageBarFragment : Fragment() {
    var pageCount: Int = 0
    private var currentPage = 1
    private var _binding: PageBarFragmentBinding? = null
    private val binding get() = _binding!!
    private val pageTextView: TextView get() = buildPageTextView()
    private lateinit var communicator: Communicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PageBarFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        communicator = activity as Communicator
        buildPageTextView()
        updatePageList()

        binding.pageNumberField.setOnKeyListener(View.OnKeyListener{ v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                currentPage = binding.pageNumberField.text.toString().toInt()
                changePage(currentPage)

                binding.pageNumberField.setText("")
                (activity as ResultsActivity).hideKeyboard()
                return@OnKeyListener true
            }
            false
        })

        binding.prevPageButton.setOnClickListener {
            if (currentPage == 1) {
                return@setOnClickListener
            }
            currentPage--
            changePage(currentPage)
        }

        binding.nextPageButton.setOnClickListener {
            if (currentPage == pageCount) {
                return@setOnClickListener
            }
            currentPage++
            changePage(currentPage)
        }
    }

    private fun buildPageTextView(): TextView {
        val pageTextView = TextView(context)
        pageTextView.setOnClickListener {
            currentPage = pageTextView.text.toString().toInt()
            changePage(currentPage)
        }

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        params.rightMargin = 8
        params.weight = 1.toFloat()
        pageTextView.layoutParams = params
        pageTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        pageTextView.textSize = 20.toFloat()

        val textColor = ContextCompat.getColor(requireContext(), R.color.colorText)
        pageTextView.setTextColor(textColor)
        return pageTextView
    }

    fun updatePageList() {
        binding.pageList.removeAllViews()
        if (pageCount <= 1) {
            binding.root.visibility = View.GONE
            return
        }
        binding.root.visibility = View.VISIBLE

        fun addPagesRange(start: Int, end: Int) {
            for (page in start..end) {
                val pageView = pageTextView
                pageView.text = page.toString()
                if (page == currentPage) {
                    val accentColor = ContextCompat.getColor(requireContext(), R.color.colorLinkText)
                    pageView.setTextColor(accentColor)
                }
                binding.pageList.addView(pageView)
            }
        }

        if (((pageCount - currentPage) >= 5 && currentPage >= 2) || (pageCount > 7 && currentPage <= 2)) {
            var startPage = currentPage - 1
            var thirdPage = currentPage + 1

            if (currentPage == 1) {
                startPage = currentPage
                thirdPage = currentPage + 2
            }

            addPagesRange(startPage, thirdPage)

            val dotPageView = pageTextView
            dotPageView.text = "..."
            binding.pageList.addView(dotPageView)

            addPagesRange(pageCount-2, pageCount)
        } else {
            addPagesRange(currentPage-1, pageCount)
        }
    }

    private fun changePage(page: Int) {
        val items = ContentParser.search(page = page)
        communicator.passSearchResults(items)
        updatePageList()
    }
}