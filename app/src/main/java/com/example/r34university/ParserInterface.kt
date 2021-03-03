package com.example.r34university

interface Parser {
    fun getTags(search: String): List<String>       // Returns list of available tags for search text
    fun search(search: String): List<ImageItem>     // Returns list of image items that was found by search text
    fun getDetails(imageItem: ImageItem)            // Updates image item fullLink, detailsLink and tagsList
}

val ContentParser = when (ConfigRepo) {
    else -> Rule34xxxParser()
} as Parser