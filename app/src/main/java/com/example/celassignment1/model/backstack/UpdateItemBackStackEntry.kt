package com.example.celassignment1.model.backstack

import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.celassignment1.model.TextItem

class UpdateItemBackStackEntry(
    private val oldItem: TextItem,
    private val newItem: TextItem
): BackStackEntry() {
    override fun undo(idToTextItemMap: SnapshotStateMap<Int, TextItem>, id: Int?): Boolean {
        println("Undoing update: old = $oldItem, new = $newItem...............................")
        idToTextItemMap[newItem.id] = oldItem
        return newItem.id == id
    }

    override fun redo(idToTextItemMap: SnapshotStateMap<Int, TextItem>, id: Int?): Boolean {
        idToTextItemMap[newItem.id] = newItem
        return  newItem.id == id
    }

}