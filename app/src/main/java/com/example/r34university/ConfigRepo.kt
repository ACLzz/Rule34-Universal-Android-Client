package com.example.r34university

import android.content.SharedPreferences

enum class ContentSource {Rule34xxx, Rule34PahealNet}                        // website sources
fun ContentSource.toSiteLink() : String {
    return when(this) {
        ContentSource.Rule34xxx -> "rule34.xxx"
        ContentSource.Rule34PahealNet -> "rule34.paheal.net"
    }
}

fun toSiteObj(siteLink: String?) : ContentSource {
    return when(siteLink) {
        "rule34.xxx" -> ContentSource.Rule34xxx
        "rule34.paheal.net" -> ContentSource.Rule34PahealNet
        else -> ContentSource.Rule34xxx
    }
}

val cfgFileName = "config"

object ConfigRepo {
    lateinit var perfs: SharedPreferences

    var source: ContentSource
        get() {
            return toSiteObj(perfs.getString("source", "rule34.xxx"))
        }
        set(value) {
            with (perfs.edit()) {
                putString("source", value.toSiteLink())
                commit()
            }
        }

    var defaultTags: String?
    get() = perfs.getString("default_tags", "")
    set(value) {
        with (perfs.edit()) {
            putString("default_tags", value)
            commit()
        }
    }

    val useAutocompleteLoader: Boolean get() {              // search for tags on search field update
        return when (source) {
            ContentSource.Rule34xxx -> true
            else -> false
        }
    }
}