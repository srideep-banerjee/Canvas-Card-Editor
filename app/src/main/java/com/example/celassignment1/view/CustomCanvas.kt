package com.example.celassignment1.view

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.celassignment1.model.TextItem

var canvasHeight = 0
var canvasWidth = 0

@Composable
fun CustomCanvas(
    items: SnapshotStateMap<Int, TextItem>,
    textMeasurer: TextMeasurer,
    currentSelected: State<TextItem?>,
    currentDragged: State<TextItem?>,
    onClick: (Offset) -> Unit,
    onDragStart: (Offset) -> Unit,
    onDragEnd: (start: Offset, end: Offset) -> Unit
) {
    println("Recomposition in custom canvas............................")
    var dragOffset by remember {
        mutableStateOf(Offset(0f, 0f))
    }
    var initialDragOffset by remember {
        mutableStateOf(Offset(0f, 0f))
    }
    var temporaryTextStartOffset by remember {
        mutableStateOf(Offset(0f, 0f))
    }
    val infiniteTransition = rememberInfiniteTransition(label = "selection_indicator_transition")
    val selectionIndicatorOffset by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(750),
            repeatMode = RepeatMode.Reverse
        ),
        label = "selection_indicator_transition"
    )
    val colorScheme = MaterialTheme.colorScheme

    val selectedTextItemSize = remember {
        mutableStateOf<TextLayoutResult?>(null)
    }

    val currentStyle = remember {
        mutableStateOf<TextStyle?>(null)
    }
    currentSelected.value?.let {
        val newStyle = getTextStyleFromTextItem(it)
        if (newStyle != currentStyle.value) {
            currentStyle.value = newStyle
        }
        selectedTextItemSize.value = textMeasurer.measure(
            text = it.text,
            style = currentStyle.value!!,
            overflow = TextOverflow.Visible,
            maxLines = 1
        )
    }
    currentDragged.value?.let {
        currentStyle.value = getTextStyleFromTextItem(it)
        selectedTextItemSize.value = textMeasurer.measure(
            text = it.text,
            style = currentStyle.value!!
        )
    }
    if (currentDragged.value == null && currentSelected.value == null) {
        selectedTextItemSize.value = null
    }

    Canvas(modifier = Modifier
        .fillMaxWidth(0.5f)
        .fillMaxHeight()
        .background(MaterialTheme.colorScheme.surface)
        .onSizeChanged {
            canvasHeight = it.height
            canvasWidth = it.width
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    dragOffset = it
                    initialDragOffset = it
                    onDragStart(it)
                },
                onDragEnd = {
                    selectedTextItemSize.value?.let { size ->
                        onDragEnd(
                            temporaryTextStartOffset,
                            temporaryTextStartOffset + Offset(
                                size.size.width.toFloat(),
                                size.size.height.toFloat()
                            )
                        )
                    }
                }
            ) { change, _ ->
                dragOffset = change.position
            }
        }
        .pointerInput(Unit) {
            detectTapGestures {
                onClick(it)
            }
        }
    ) {
        for (item in items.values) {
            if (item.id == currentSelected.value?.id || item.id == currentDragged.value?.id) {
                continue
            }
            drawText(
                textLayoutResult = textMeasurer.measure(
                    text = item.text,
                    style = getTextStyleFromTextItem(item),
                    maxLines = 1,
                    overflow = TextOverflow.Visible
                ),
                topLeft = item.start,
                color = item.color,
                textDecoration = if (item.textFont.underline) TextDecoration.Underline else TextDecoration.None,
            )
        }
        currentSelected.value?.let {
            drawRoundRect(
                color = colorScheme.primary,
                topLeft = it.start - Offset(selectionIndicatorOffset + 6f,selectionIndicatorOffset + 6f),
                size = Size(
                    selectedTextItemSize.value!!.size.width + selectionIndicatorOffset * 2 + 12f,
                    selectedTextItemSize.value!!.size.height + selectionIndicatorOffset * 2 + 12f
                ),
                alpha = (12 - selectionIndicatorOffset) / 12,
                style = Stroke(2.dp.toPx()),
                cornerRadius = CornerRadius(200f, 200f)
            )
            drawText(
                textLayoutResult = selectedTextItemSize.value!!,
                color = it.color,
                topLeft = it.start,
                textDecoration = if (it.textFont.underline) TextDecoration.Underline else TextDecoration.None,
            )
        }

        currentDragged.value?.let {
            temporaryTextStartOffset = it.start + dragOffset - initialDragOffset
            val textStartXForCenter = (canvasWidth - selectedTextItemSize.value!!.size.width).toFloat() / 2f
            if (
                ((textStartXForCenter - 20f)..(textStartXForCenter + 20f))
                    .contains(temporaryTextStartOffset.x)
                ) {
                temporaryTextStartOffset = Offset(textStartXForCenter, temporaryTextStartOffset.y)
                drawLine(
                    color = colorScheme.onSurfaceVariant,
                    start = Offset(canvasWidth.toFloat() / 2f, 1f ),
                    end = Offset(canvasWidth.toFloat() / 2f, canvasHeight.toFloat() - 1f ),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f,10f))
                )
            }

            val textStartYForCenter = (canvasHeight - selectedTextItemSize.value!!.size.height).toFloat() / 2f
            if (
                ((textStartYForCenter - 20f)..(textStartYForCenter + 20f))
                    .contains(temporaryTextStartOffset.y)
                ) {
                temporaryTextStartOffset = Offset(temporaryTextStartOffset.x, textStartYForCenter)
                drawLine(
                    color = colorScheme.onSurfaceVariant,
                    start = Offset(1f, canvasHeight.toFloat() / 2f ),
                    end = Offset(canvasWidth.toFloat() - 1f, canvasHeight.toFloat() / 2f ),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f,10f))
                )
            }

            drawRoundRect(
                color = colorScheme.primary,
                topLeft = temporaryTextStartOffset - Offset(10f, 10f),
                size = Size(
                    selectedTextItemSize.value!!.size.width + 20f,
                    selectedTextItemSize.value!!.size.height + 20f
                ),
                alpha = (8 - selectionIndicatorOffset) / 12,
                cornerRadius = CornerRadius(200f, 200f)
            )
            drawText(
                textMeasurer = textMeasurer,
                text = it.text,
                topLeft = temporaryTextStartOffset,
                style = currentStyle.value!!
            )
        }
    }
}

fun getTextStyleFromTextItem(textItem: TextItem): TextStyle {
    return TextStyle(
        color = textItem.color,
        fontSize = textItem.textFont.size.sp,
        fontFamily = textItem.textFont.fontFamily,
        fontStyle =
        if (textItem.textFont.italic)
            FontStyle.Italic
        else
            FontStyle.Normal,
        textDecoration =
        if (textItem.textFont.underline)
            TextDecoration.Underline
        else
            TextDecoration.None
    )
}