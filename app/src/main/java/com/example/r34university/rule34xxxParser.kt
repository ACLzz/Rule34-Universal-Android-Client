package com.example.r34university

import khttp.get
import org.json.JSONObject
import org.jsoup.helper.HttpConnection.connect
import org.jsoup.nodes.Document
import kotlin.concurrent.thread

class Rule34xxxParser : Parser {
    private val baseLink = "https://rule34.xxx/"

    override fun getTags(search: String): List<String> {
        // TODO print lable but insert value in search field
        val url = buildUrl(baseLink + "autocomplete.php", mapOf("q" to search))
        val resp = get(url).jsonArray

        val tags = ArrayList<String>()
        for (tagId in 0 until resp.length()) {
            val tag = resp[tagId] as JSONObject
            val tagText = tag.getString("value")
            tags.add(tagText)
        }
        return tags
    }

    override fun search(search: String): List<ImageItem> {
        // TODO
        // https://rule34.xxx/index.php?page=post&s=list&tags=my_first_tag+my_scend_tag+
        val url = buildUrl(baseLink + "index.php", mapOf(
            "page" to "post",
            "s" to "list",
            "tags" to search
        ))
        var resp: Document = Document("")
        thread {
            resp = connect(url).get()
        }.join()

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

    override fun getDetails(imageItem: ImageItem) {
        // TODO
    }
}