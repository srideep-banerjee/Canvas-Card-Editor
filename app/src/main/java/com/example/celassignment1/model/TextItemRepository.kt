package com.example.celassignment1.model

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset

class TextItemRepository {
    private val idToTextItemMap: SnapshotStateMap<Int, TextItem> = mutableStateMapOf()
    private var maxId = 0

    fun addTextItem(item: TextItem) {
        item.id = maxId++
        idToTextItemMap[item.id] = item
    }

    fun removeTextItem(item: TextItem) {
        idToTextItemMap.remove(item.id)
    }

    fun getItemAt(offset: Offset): TextItem? {
        for (item in idToTextItemMap.values) {
            if (
                (item.start.x..item.end.x).contains(offset.x) &&
                (item.start.y..item.end.y).contains(offset.y)
                ) {
                return item
            }
        }

        return null
    }

    fun getItems(): SnapshotStateMap<Int, TextItem> = idToTextItemMap

    fun updateItem(item: TextItem) {
        idToTextItemMap[item.id] = item
    }
}