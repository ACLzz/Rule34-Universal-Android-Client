package parsers

import com.example.r34university.ImageItem
import khttp.get

class Rule34PahealNetParser() : Parser() {
    override var currentSearch: String = ""
    override val baseLink: String = "https://rule34.paheal.net/"

    override fun getTags(search: String): List<String> {
        val tags = ArrayList<String>()
        if (search.length <= 2)
            return tags

        val url = buildUrl("api/internal/autocomplete", mapOf("s" to search))
        val resp = get(url).jsonObject

        for (key in resp.keys()) {
            tags.add(key)
        }
        return tags
    }

    override fun search(search: String, page: Int): List<ImageItem> {
        val modifiedSearch = formatSearch(search)
        super.search(modifiedSearch, page)
        val resp = getHtml("post/list/${if (search.isNotEmpty()) "$modifiedSearch/" else ""}$page")

        val imageTags = resp.select("div.shm-thumb.thumb")
        val imageItems = arrayListOf<ImageItem>()
        imageTags?.let {
            imageTags.forEach {
                val details = it.select("a.shm-thumb-link").attr("href")
                val thumb = it.select("img").attr("src")
                val item = ImageItem(thumb, details, "")
                imageItems.add(item)
            }
        }

        return imageItems
    }

    override fun getAllPosts(): List<ImageItem> {
        return search("")
    }

    override fun getAllPostsPagesCount(): Int {
        return getPagesCount("")
    }

    override fun getPagesCount(search: String): Int {
        // FIXME
        val modifiedSearch = formatSearch(search)
        val resp = getHtml("post/list/${if (search.isNotEmpty()) "$modifiedSearch/" else ""}/1")
        val count = resp.selectFirst("#paginator > div:nth-child(1) > a:nth-child(3)").attr("href").split("/").last().toInt()
        return count.toInt()
    }

    override fun getDetails(imageItem: ImageItem) {
        val resp = getHtml(imageItem.detail.toString())

        val tags = arrayListOf<String>()
        resp.select(".tag_name").forEach {
            tags.add(it.text().replace(Regex(" "), "_"))
        }
        imageItem.tags = tags

        val full = resp.select(".shm-main-image")
        imageItem.full = if (full.first().tagName() == "video") {
            full.select("source").attr("src")
        } else {
            full.attr("src").toString()
        }
    }

    // paheal doesn't allow to search more than 3 tags by anonymous user
    private fun formatSearch(search: String): String {
        val tagsArray = search.split(" ")
        return if (tagsArray.size > 3) {
            tagsArray.subList(0, 3).joinToString(" ")
        } else {
            tagsArray.joinToString(" ")
        }
    }
}