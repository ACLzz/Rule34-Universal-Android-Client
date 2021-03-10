package parsers

import com.example.r34university.ImageItem
import khttp.get
import org.json.JSONObject

class Rule34xxxParser() : Parser() {
    override var currentSearch: String = ""
    override val baseLink = "https://rule34.xxx/"
    private val maximumId = 200000      // rule34.xxx has dumb pid argument bound
    private val maximumPage = 4762
    private val idsPerPage = 42

    override fun getTags(search: String): List<String> {
        val url = buildUrl("autocomplete.php", mapOf("q" to search))
        val resp = get(url).jsonArray

        val tags = ArrayList<String>()
        for (tagId in 0 until resp.length()) {
            val tag = resp[tagId] as JSONObject
            val tagText = tag.getString("value")
            tags.add(tagText)
        }
        return tags
    }

    override fun search(search: String, page: Int): List<ImageItem> {
        super.search(search, page)
        currentSearch = search

        val urlParams = mutableMapOf(
            "page" to "post",
            "s" to "list",
            "tags" to search
        )
        if (page > 1)
            urlParams["pid"] = ((page-1) * idsPerPage).toString()

        val resp = getHtml("index.php", urlParams)

        val imageTags = resp.select("span.thumb")
        val imageItems = arrayListOf<ImageItem>()
        imageTags?.let {
            imageTags.forEach {
                val details = it.select("a").attr("href")
                val thumb = it.select("img").attr("src")
                val item = ImageItem(thumb, details, "")
                imageItems.add(item)
            }
        }

        return imageItems
    }

    override fun getAllPosts(): List<ImageItem> = search("all")

    override fun getAllPostsPagesCount(): Int = getPagesCount("all")

    override fun getPagesCount(search: String): Int {
        val resp = getHtml("index.php", mapOf(
            "page" to "post",
            "s" to "list",
            "tags" to search,
            "pid" to "200000",
        ))

        val pageBar = resp.select("#paginator")
        val lastPage = pageBar.select("a").last()
        lastPage?.let {
            if (lastPage.text() == ">>" || lastPage.text() == ">") {
                return maximumPage
            }
            if (lastPage.text().toInt() <= maximumPage) {
                return lastPage.text().toInt()
            }
        }
        return 1
    }

    override fun getDetails(imageItem: ImageItem) {
        val resp = getHtml(imageItem.detail.toString())

        val tags = arrayListOf<String>()
        resp.select("ul#tag-sidebar a").forEach {
            tags.add(it.text().replace(Regex(" "), "_"))
        }
        imageItem.tags = tags

        val video = resp.select("#video")
        imageItem.full = if (video.size != 0) {
            video.select("source").attr("src")
        } else {
             resp.select("#image").attr("src").toString()
        }
    }
}