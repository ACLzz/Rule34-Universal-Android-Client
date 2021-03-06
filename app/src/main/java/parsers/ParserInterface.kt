package parsers

import com.example.r34university.ConfigRepo
import com.example.r34university.ImageItem

interface Parser {
    var currentSearch: String
    fun getTags(search: String): List<String>                                      // Returns list of available tags for search text
    fun search(search: String = currentSearch, page: Int = 0): List<ImageItem>     // Returns list of image items that was found by search text
    fun getPagesCount(search: String): Int                                         // Returns count of pages for search
    fun getDetails(imageItem: ImageItem)                                           // Updates image item fullLink, detailsLink and tagsList
}

val ContentParser = when (ConfigRepo) {
    else -> Rule34xxxParser()
} as Parser

fun buildUrl(path: String, args: Map<String, String> = mapOf()) : String {
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