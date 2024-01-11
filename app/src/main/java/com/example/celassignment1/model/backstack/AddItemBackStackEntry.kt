package com.example.celassignment1.model.backstack

import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.celassignment1.model.TextItem

class AddItemBackStackEntry(private val addedItem: TextItem): BackStackEntry() {
    override fun undo(idToTextItemMap: SnapshotStateMap<Int, TextItem>, id: Int?): Boolean {
        idToTextItemMap.remove(addedItem.id)
        return addedItem.id == id
    }

    override fun redo(idToTextItemMap: SnapshotStateMap<Int, TextItem>, id: Int?): Boolean {
        idToTextItemMap[addedItem.id] = addedItem
        return addedItem.id == id
    }

}