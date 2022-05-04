package com.dicoding.storyapp

import com.dicoding.storyapp.model.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "photoUrl  $i",
                "createdAt $i",
                "name $i",
                "description $i",
                i + 0.0,
                i + 0.0
            )
            items.add(story)
        }
        return items
    }

    fun generateDummySomeStory(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..5) {
            val story = ListStoryItem(
                i.toString(),
                "photoUrl  $i",
                "createdAt $i",
                "name $i",
                "description $i",
                i + 0.0,
                i + 0.0
            )
            items.add(story)
        }
        return items
    }
}