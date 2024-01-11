package com.example.celassignment1.model.backstack

import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.celassignment1.model.TextItem

class RemoveItemBackStackEntry(private val removedItem: TextItem): BackStackEntry() {
    override fun undo(idToTextItemMap: SnapshotStateMap<Int, TextItem>, id: Int?): Boolean {
        idToTextItemMap[removedItem.id] = removedItem
        return removedItem.id == id
    }

    override fun redo(idToTextItemMap: SnapshotStateMap<Int, TextItem>, id: Int?): Boolean {
        idToTextItemMap.remove(removedItem.id)
        return removedItem.id == id
    }

}