package parsers

import com.example.r34university.ConfigRepo
import com.example.r34university.ImageItem
import org.jsoup.helper.HttpConnection
import kotlin.concurrent.thread
import org.jsoup.nodes.Document

open class Parser {
    open val baseLink: String = "https://example.com"
    open var currentSearch: String = ""

    open fun getTags(search: String): List<String> = listOf<String>()                        // Returns list of available tags for search text
    open fun search(search: String = currentSearch, page: Int = 0): List<ImageItem> {        // Returns list of image items that was found by search text
        currentSearch = search
        return listOf<ImageItem>()
    }
    open fun getPagesCount(search: String): Int = 1                                          // Returns count of pages for search
    open fun getDetails(imageItem: ImageItem) {}                                             // Updates image item fullLink, detailsLink and tagsList
    open fun getAllPosts(): List<ImageItem> {                                                // Returns all new posts
        return listOf()
    }
    open fun getAllPostsPagesCount(): Int = 1

    fun buildUrl(path: String, args: Map<String, String> = mapOf(), useBaseLink: Boolean = true) : String {
        var url = if (useBaseLink) baseLink + path else path

        if (args.isNotEmpty())
            url += "?"

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

    fun getHtml(path: String, args: Map<String, String> = mapOf(), baseLink: Boolean = true): Document {
        val url = buildUrl(path, args, baseLink)
        var resp: Document = Document("")
        thread {
            resp = HttpConnection.connect(url).get()
        }.join()
        return resp
    }
}

val ContentParser = when (ConfigRepo) {
    else -> Rule34xxxParser()
} as Parser