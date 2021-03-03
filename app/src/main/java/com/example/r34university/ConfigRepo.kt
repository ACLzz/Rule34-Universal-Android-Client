package com.example.r34university

enum class contentSource {rule34xxx}                        // website sources

object ConfigRepo {
    init {
        loadFromDisk()
    }

    lateinit var source: contentSource
    val useAutocompleteLoader: Boolean get() {              // search for tags on search field update
        return when (source) {
            contentSource.rule34xxx -> true
            else -> false
        }
    }

    private fun loadFromDisk() {
        // TODO load from disk
        source = contentSource.rule34xxx
    }
}