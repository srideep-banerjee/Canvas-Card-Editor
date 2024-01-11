package com.example.celassignment1.model.backstack

import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.celassignment1.model.TextItem

class BackStackManager(
    private val idToTextItemMap: SnapshotStateMap<Int, TextItem>
) {
    private val maxBackStackLength = 5
    private var backStackLength = 0
    private val emptyBackStackEntry = EmptyBackStackEntry()
    private var head: BackStackEntry = emptyBackStackEntry

    fun undo(id: Int?): Boolean {
        val result = head.undo(idToTextItemMap, id)
        head = head.behind?:emptyBackStackEntry
        return result
    }

    fun redo(id: Int?): Boolean {
        val result = head.ahead?.redo(idToTextItemMap, id) ?: false
        head = head.ahead?:head
        return result
    }

    fun add(entry: BackStackEntry) {
        head.ahead = entry
        entry.behind = head
        head = entry
        if (backStackLength == maxBackStackLength) {
            emptyBackStackEntry.ahead = emptyBackStackEntry.ahead?.ahead
        } else {
            backStackLength++
        }
    }
}