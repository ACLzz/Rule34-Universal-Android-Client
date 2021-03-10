package com.example.r34university

import android.content.SharedPreferences

const val cfgFileName = "config"

object ConfigRepo {
    lateinit var perfs: SharedPreferences
    var parsersWithSuggestionsLoaderArray = arrayOf<String>()

    var source: String?
        get() {
            return perfs.getString("source", "rule34.xxx")
        }
        set(value) {
            with (perfs.edit()) {
                putString("source", value)
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
        return source in parsersWithSuggestionsLoaderArray
    }
}