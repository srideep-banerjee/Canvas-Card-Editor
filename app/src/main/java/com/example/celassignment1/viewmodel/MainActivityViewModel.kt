package com.example.celassignment1.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.celassignment1.model.TextFont
import com.example.celassignment1.model.TextItem
import com.example.celassignment1.model.TextItemRepository
import com.example.celassignment1.model.backstack.AddItemBackStackEntry
import com.example.celassignment1.model.backstack.BackStackManager
import com.example.celassignment1.model.backstack.RemoveItemBackStackEntry
import com.example.celassignment1.model.backstack.UpdateItemBackStackEntry
import com.example.celassignment1.view.PopupListType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.RuntimeException

class MainActivityViewModel: ViewModel() {
    val popupListState = mutableStateOf(PopupListType.NONE)
    private val _currentlySelectedItem = mutableStateOf<TextItem?>(null)
    val currentlySelectedItem: State<TextItem?> = _currentlySelectedItem

    private val textItemRepository = TextItemRepository()
    val items = textItemRepository.getItems()

    private val backStackManager = BackStackManager(items)

    var defaultFont = TextFont(
        fontFamily = FontFamily.Default,
        size = 12,
        italic = false,
        underline = false
    )

    var defaultColor = Color.Black

    var defaultText = "New Text"

    fun addText(textItem: TextItem) {
        textItemRepository.addTextItem(textItem)
        backStackManager.add(AddItemBackStackEntry(textItem))
    }

    fun removeText(textItem: TextItem) {
        textItemRepository.removeTextItem(textItem)
        backStackManager.add(RemoveItemBackStackEntry(textItem))
    }
    fun updateText(textItem: TextItem) {
        backStackManager.add(UpdateItemBackStackEntry(items[textItem.id]!!, textItem))
        textItemRepository.updateItem(textItem)
        if (!items.containsKey(textItem.id))
            throw RuntimeException("Can't update text, text not present in map")
    }

    fun getItemAt(offset: Offset, onRetrieved: (TextItem?)->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val textItem = textItemRepository.getItemAt(offset)
            withContext(Dispatchers.Main) {
                onRetrieved(textItem)
            }
        }
    }

    fun setCurrentlySelectedItem(textItem: TextItem?) {
        _currentlySelectedItem.value = textItem
    }

    fun undo(id: Int?): Boolean {
        return backStackManager.undo(id)
    }

    fun redo(id: Int?): Boolean {
        return backStackManager.redo(id)
    }
}