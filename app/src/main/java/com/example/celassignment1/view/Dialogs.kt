package com.example.celassignment1.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.GenericFontFamily
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.celassignment1.model.TextFont

@Composable
fun CustomDialogList(
    header: @Composable ColumnScope.()->Unit,
    item: @Composable LazyListScope.(index: Int) -> Unit,
    size: Int,
    onDismissed: () -> Unit
) {
    Dialog(onDismissRequest = {
        onDismissed()
    }) {
        Column(modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)) {
            this@Column.header()
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)) {
                items(size) {
                    this@LazyColumn.item(it)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "OK",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onDismissed() }
                )
            }
        }
    }
}

@Composable
fun FontDialog(font: MutableState<TextFont>, dialogState: MutableState<PopupListType>) {
    var italicState by rememberSaveable {
        mutableStateOf(font.value.italic)
    }
    var underlineState by rememberSaveable {
        mutableStateOf(font.value.underline)
    }
    val fonts = listOf(
        FontFamily.Default,
        FontFamily.Cursive,
        FontFamily.Monospace,
        FontFamily.Serif,
        FontFamily.SansSerif
    )
    var currentFontFamily by rememberSaveable {
        mutableIntStateOf(fonts.indexOf(font.value.fontFamily))
    }

    CustomDialogList(
        header = {
            Text(text = "Select font", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = italicState, onCheckedChange = {italicState = it})
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Italic")
                Spacer(modifier = Modifier.width(16.dp))
                Checkbox(checked = underlineState, onCheckedChange = {underlineState = it})
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Underline")
            }
            Text(text = "Font family:")
        },
        item = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (it == currentFontFamily),
                    onClick = {
                        currentFontFamily = it
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                val fontFamilyName =
                    if (it == 0)
                        "System Default"
                    else
                        (fonts[it] as GenericFontFamily)
                        .name
                        .capitalize(Locale.current)
                Text(text = fontFamilyName)
            }
        },
        size = fonts.size
    ) {
        font.value = font.value.getUpdated(
            fontFamily = fonts[currentFontFamily],
            italic = italicState,
            underline = underlineState
        )
        dialogState.value = PopupListType.NONE
    }
}

@Composable
fun ColorDialog(color: MutableState<Color>, dialogState: MutableState<PopupListType>) {
    var tempColor by remember {
        mutableStateOf(color.value)
    }

    CustomDialogList(
        header = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(tempColor)
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Red:", modifier = Modifier.width(72.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f),
                    value = tempColor.red,
                    onValueChange = {
                        tempColor = Color(it, tempColor.green, tempColor.blue, tempColor.alpha)
                    }
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Green:", modifier = Modifier.width(72.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f),
                    value = tempColor.green,
                    onValueChange = {
                        tempColor = Color(tempColor.red, it, tempColor.blue, tempColor.alpha)
                    }
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Blue:", modifier = Modifier.width(72.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f),
                    value = tempColor.blue,
                    onValueChange = {
                        tempColor = Color(tempColor.red, tempColor.green, it, tempColor.alpha)
                    }
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Alpha:", modifier = Modifier.width(72.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f),
                    value = tempColor.alpha,
                    onValueChange = {
                        tempColor = Color(tempColor.red, tempColor.green, tempColor.blue, it)
                    }
                )
            }
        },
        item = {},
        size = 0
    ) {
        println("ColorDialog: Setting color to ${tempColor}........................")
        color.value = tempColor
        dialogState.value = PopupListType.NONE
    }
}