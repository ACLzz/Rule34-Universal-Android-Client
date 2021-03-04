package com.example.r34university

interface Parser {
    fun getTags(search: String): List<String>       // Returns list of available tags for search text
    fun search(search: String): List<ImageItem>     // Returns list of image items that was found by search text
    fun getDetails(imageItem: ImageItem)            // Updates image item fullLink, detailsLink and tagsList
}

val ContentParser = when (ConfigRepo) {
    else -> Rule34xxxParser()
} as Parser

fun buildUrl(path: String, args: Map<String, String>) : String {
    var url = path
    if (args.isNotEmpty()) {
        url += "?"
    }

    args.forEach { (id, value) ->
        url += "$id="
        val argArray = value.split(" ")

        argArray.forEach {
            url += it
            if (it != argArray.last())
                url += "+"
        }
        url += "&"
    }
    url = url.removeSuffix("&")
    return url
}