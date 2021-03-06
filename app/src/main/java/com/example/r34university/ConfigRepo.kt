package com.example.r34university

enum class ContentSource {Rule34xxx}                        // website sources

object ConfigRepo {
    lateinit var source: ContentSource
    init {
        loadFromDisk()
    }

    val useAutocompleteLoader: Boolean get() {              // search for tags on search field update
        return when (source) {
            ContentSource.Rule34xxx -> true
            else -> false
        }
    }

    private fun loadFromDisk() {
        // TODO
        source = ContentSource.Rule34xxx
    }
}