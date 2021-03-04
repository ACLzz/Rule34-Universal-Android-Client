package com.example.r34university

import khttp.get
import org.json.JSONObject

class Rule34xxxParser : Parser {
    private val baseLink = "https://rule34.xxx/"

    override fun getTags(search: String): List<String> {
        // TODO print lable but insert value in search field
        //	https://rule34.xxx/autocomplete.php?q=mySearch
        val url = buildUrl(baseLink + "autocomplete.php", mapOf("q" to search))
        val _resp = get(url)
        val resp = _resp.jsonArray

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
        return listOf()
    }

    override fun getDetails(imageItem: ImageItem) {
        // TODO
    }
}