package parsers

import com.example.r34university.ImageItem

class rule34PahealNetParser() : Parser() {
    override var currentSearch: String = ""
    override fun getTags(search: String): List<String> {
        TODO("Not yet implemented")
    }

    override fun search(search: String, page: Int): List<ImageItem> {
        TODO("Not yet implemented")
    }

    override fun getPagesCount(search: String): Int {
        TODO("Not yet implemented")
    }

    override fun getDetails(imageItem: ImageItem) {
        TODO("Not yet implemented")
    }
}