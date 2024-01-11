package com.example.celassignment1.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontFamily

@Immutable
data class TextFont (
    val fontFamily: FontFamily,
    val size: Int,
    val italic: Boolean,
    val underline: Boolean,
) {
    fun getUpdated(
        fontFamily: FontFamily = this.fontFamily,
        size: Int = this.size,
        italic: Boolean = this.italic,
        underline: Boolean = this.underline
    ): TextFont {
        return TextFont(fontFamily, size, italic, underline)
    }
}