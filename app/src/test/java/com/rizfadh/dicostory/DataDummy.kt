package com.rizfadh.dicostory

import com.rizfadh.dicostory.data.api.response.StoryResult

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryResult> {
        val items: MutableList<StoryResult> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryResult(
                i.toString(),
                "name $i",
                "desc $i",
                "photo $i",
                "$i/$i/$i"
            )
            items.add(quote)
        }
        return items
    }
}