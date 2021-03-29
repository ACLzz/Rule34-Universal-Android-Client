package parsers

import com.example.r34university.ConfigRepo
import com.example.r34university.ImageItem
import org.jsoup.helper.HttpConnection
import kotlin.concurrent.thread
import org.jsoup.nodes.Document
import org.jsoup.HttpStatusException

open class Parser {
    open val baseLink: String = "https://example.com"                                        // YOU NEED TO OVERWRITE THIS
    open var currentSearch: String = ""                                                      // YOU NEED TO OVERWRITE THIS

    open fun getTags(search: String): List<String> = listOf<String>()                        // Returns list of available tags for search text
    open fun search(search: String = currentSearch, page: Int = 1): List<ImageItem> {        // Returns list of image items that was found by search text
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
            try {
                resp = HttpConnection.connect(url).get()        // FIXME 404 error
            } catch (e: HttpStatusException) {
                println("Http exception status code: " + e.statusCode.toString())
            }
        }.join()
        return resp
    }
}

val ContentParser get() = when (ConfigRepo.source) {
    "rule34.xxx" -> rule34xxxParser
    "rule34.paheal.net" -> rule34PahealNetParser
    else -> Rule34xxxParser()
}

private val rule34xxxParser = Rule34xxxParser()
private val rule34PahealNetParser = Rule34PahealNetParser()