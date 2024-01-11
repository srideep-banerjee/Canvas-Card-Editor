package com.example.celassignment1.model.backstack

import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.celassignment1.model.TextItem

abstract class BackStackEntry(
    var ahead: BackStackEntry? = null,
    var behind: BackStackEntry? = null
) {
    abstract fun undo(idToTextItemMap: SnapshotStateMap<Int, TextItem>, id: Int?): Boolean
    abstract fun redo(idToTextItemMap: SnapshotStateMap<Int, TextItem>, id: Int?): Boolean
}