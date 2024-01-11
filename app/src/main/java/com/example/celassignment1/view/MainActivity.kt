package com.example.celassignment1.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.celassignment1.viewmodel.MainActivityViewModel
import com.example.celassignment1.R
import com.example.celassignment1.model.TextFont
import com.example.celassignment1.model.TextItem
import com.example.celassignment1.ui.theme.CelAssignment1Theme

class MainActivity : ComponentActivity() {
    private val mainActivityViewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CelAssignment1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val fontState = remember {
                        mutableStateOf(mainActivityViewModel.defaultFont)
                    }
                    val color = remember {
                        mutableStateOf(mainActivityViewModel.defaultColor)
                    }
                    SharedComposable(mainActivityViewModel, fontState, color)
                    when (mainActivityViewModel.popupListState.value) {
                        PopupListType.FONTS -> {
                            FontDialog(font = fontState, mainActivityViewModel.popupListState)
                        }

                        PopupListType.COLOR -> {
                            ColorDialog(color = color, mainActivityViewModel.popupListState)
                        }

                        PopupListType.ITEMS -> {

                        }

                        PopupListType.NONE -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun SharedComposable(
    mainActivityViewModel: MainActivityViewModel,
    fontState: MutableState<TextFont>,
    colorState: MutableState<Color>
) {
    val textMeasurer = rememberTextMeasurer()
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        val currentlyDragged = remember {
            mutableStateOf<TextItem?>(null)
        }
        val textState = remember {
            mutableStateOf(mainActivityViewModel.defaultText)
        }
        mainActivityViewModel.currentlySelectedItem.value?.let {
            var different = false
            var color = it.color
            if (it.color.value != colorState.value.value) {
                color = colorState.value
                different = true
            }

            var text = it.text
            if (it.text != textState.value) {
                text = textState.value
                different = true
            }

            var font = it.textFont
            if (it.textFont != fontState.value) {
                font = fontState.value
                different = true
            }

            var end = it.end
            if (different) {
                val textStyle = TextStyle(
                    color = color,
                    fontSize = font.size.sp,
                    fontFamily = font.fontFamily,
                    fontStyle = if (font.italic) FontStyle.Italic else FontStyle.Normal,
                    textDecoration = if (font.underline) TextDecoration.Underline else TextDecoration.None
                )
                val size = textMeasurer.measure(
                    text = text,
                    style = textStyle,
                    overflow = TextOverflow.Visible,
                    maxLines = 1
                ).size
                end = it.start + Offset(size.width.toFloat(), size.height.toFloat())
            }

            val newTextItem = it.getUpdated(
                text = text,
                textFont = font,
                end = end,
                color = color
            )

            if (different) {
                mainActivityViewModel.updateText(newTextItem)
                mainActivityViewModel.setCurrentlySelectedItem(newTextItem)
            }
        }
        if (mainActivityViewModel.currentlySelectedItem.value == null && currentlyDragged.value == null) {
            if (mainActivityViewModel.defaultFont != fontState.value)
                mainActivityViewModel.defaultFont = fontState.value
            if (mainActivityViewModel.defaultText != textState.value)
                mainActivityViewModel.defaultText = textState.value
            if (mainActivityViewModel.defaultColor != colorState.value)
                mainActivityViewModel.defaultColor = colorState.value
        }


        Column {
            DoButton(iconId = R.drawable.undo_icon, text = "Undo") {
                val items = mainActivityViewModel.items
                val currentId = mainActivityViewModel.currentlySelectedItem.value?.id

                if (mainActivityViewModel.undo(currentId)) {
                    mainActivityViewModel.setCurrentlySelectedItem(items[currentId])
                    items[currentId]?.let {
                        fontState.value = it.textFont
                        textState.value = it.text
                        colorState.value = it.color
                    }
                    if (items[currentId] == null) {
                        textState.value = mainActivityViewModel.defaultText
                        fontState.value = mainActivityViewModel.defaultFont
                        colorState.value = mainActivityViewModel.defaultColor
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            DoButton(iconId = R.drawable.redo_icon, text = "Redo") {
                val items = mainActivityViewModel.items
                val currentId = mainActivityViewModel.currentlySelectedItem.value?.id

                if (mainActivityViewModel.redo(currentId)) {
                    mainActivityViewModel.setCurrentlySelectedItem(items[currentId])
                    items[currentId]?.let {
                        fontState.value = it.textFont
                        textState.value = it.text
                        colorState.value = it.color
                    }
                    if (items[currentId] == null) {
                        textState.value = mainActivityViewModel.defaultText
                        fontState.value = mainActivityViewModel.defaultFont
                        colorState.value = mainActivityViewModel.defaultColor
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f, true))
            if (mainActivityViewModel.currentlySelectedItem.value != null)
                FloatingActionButton(
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.error,
                    onClick = {
                        val item = mainActivityViewModel.currentlySelectedItem.value
                        mainActivityViewModel.setCurrentlySelectedItem(null)
                        item?.let { mainActivityViewModel.removeText(item) }
                        textState.value = mainActivityViewModel.defaultText
                        fontState.value = mainActivityViewModel.defaultFont
                        colorState.value = mainActivityViewModel.defaultColor
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
        }
        Spacer(Modifier.width(8.dp))

        CustomCanvas(
            textMeasurer = textMeasurer,
            items = mainActivityViewModel.items,
            currentSelected = mainActivityViewModel.currentlySelectedItem,
            currentDragged = currentlyDragged,
            onClick = { offset ->
                mainActivityViewModel.getItemAt(offset) {
                    it?.let {
                        fontState.value = it.textFont
                        colorState.value = it.color
                        textState.value = it.text
                    }
                    if (it == null) {
                        fontState.value = mainActivityViewModel.defaultFont
                        colorState.value = mainActivityViewModel.defaultColor
                        textState.value = mainActivityViewModel.defaultText
                    }
                    mainActivityViewModel.setCurrentlySelectedItem(it)
                }
            },
            onDragStart = { offset ->
                mainActivityViewModel.getItemAt(offset) {
                    it?.let {
                        fontState.value = it.textFont
                        colorState.value = it.color
                        textState.value = it.text
                    }
                    mainActivityViewModel.setCurrentlySelectedItem(null)
                    currentlyDragged.value = it
                }
            },
            onDragEnd = { start, end ->
                currentlyDragged.value?.let {
                    val textItem = TextItem(
                        id = it.id,
                        textFont = it.textFont,
                        text = it.text,
                        start = start,
                        end = end,
                        color = it.color
                    )
                    currentlyDragged.value = null
                    mainActivityViewModel.updateText(textItem)
                    mainActivityViewModel.setCurrentlySelectedItem(textItem)
                }
            }
        )

        Spacer(Modifier.width(8.dp))

        DetailsColumn(
            modifier = Modifier.weight(1f, true),
            colorState = colorState,
            fontState = fontState,
            textState = textState,
            onFontButtonClicked = {
                mainActivityViewModel.popupListState.value = PopupListType.FONTS
            },
            onColorButtonClicked = {
                mainActivityViewModel.popupListState.value = PopupListType.COLOR
            },
            onAdd = {
                val style = TextStyle(
                    color = mainActivityViewModel.defaultColor,
                    fontSize = mainActivityViewModel.defaultFont.size.sp,
                    fontStyle =
                    if (mainActivityViewModel.defaultFont.italic)
                        FontStyle.Italic
                    else
                        FontStyle.Normal,
                    fontFamily = mainActivityViewModel.defaultFont.fontFamily,
                    textDecoration =
                    if (mainActivityViewModel.defaultFont.underline)
                        TextDecoration.Underline
                    else
                        TextDecoration.None
                )
                val size = textMeasurer
                    .measure(
                        text = mainActivityViewModel.defaultText,
                        style = style
                    )
                    .size
                val start = Offset(
                    ((canvasWidth - size.width) / 2).toFloat(),
                    ((canvasHeight - size.height) / 2).toFloat()
                )
                val end = Offset(
                    start.x + size.width,
                    start.y + size.height
                )

                val textItem = TextItem(
                    textFont = mainActivityViewModel.defaultFont,
                    text = mainActivityViewModel.defaultText,
                    start = start,
                    end = end,
                    color = mainActivityViewModel.defaultColor
                )
                mainActivityViewModel.addText(textItem = textItem)
            }
        )
    }
}

@Composable
fun DoButton(iconId: Int, text: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconId),
            contentDescription = text,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(end = 8.dp)
        )

        Text(text = text, color = MaterialTheme.colorScheme.onPrimary)
    }
}