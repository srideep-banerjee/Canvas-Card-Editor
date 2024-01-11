package com.example.celassignment1.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Immutable
class TextItem(
    var id: Int = -1,
    val textFont: TextFont,
    val text: String,
    val start: Offset,
    val end: Offset,
    val color: Color
) {
    fun getUpdated(
        textFont: TextFont = this.textFont,
        text: String = this.text,
        start: Offset = this.start,
        end: Offset = this.end,
        color: Color = this.color
    ): TextItem {
        return TextItem(id, textFont, text, start, end, color)
    }
}