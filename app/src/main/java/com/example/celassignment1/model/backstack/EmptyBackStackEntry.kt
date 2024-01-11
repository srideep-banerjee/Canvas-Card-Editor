package com.example.celassignment1.model.backstack

import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.celassignment1.model.TextItem

internal class EmptyBackStackEntry: BackStackEntry(ahead = null, behind = null) {
    init {
        behind = this
    }
    override fun undo(idToTextItemMap: SnapshotStateMap<Int, TextItem>, id: Int?) = false
    override fun redo(idToTextItemMap: SnapshotStateMap<Int, TextItem>, id: Int?) = false
}