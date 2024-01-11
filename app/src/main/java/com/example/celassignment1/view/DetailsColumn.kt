package com.example.celassignment1.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.GenericFontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.celassignment1.model.TextFont
import kotlinx.coroutines.delay

@Composable
fun RowScope.DetailsColumn(
    modifier: Modifier,
    fontState: MutableState<TextFont>,
    colorState: MutableState<Color>,
    textState: MutableState<String>,
    onFontButtonClicked: () -> Unit,
    onColorButtonClicked: () -> Unit,
    onAdd: ()->Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .weight(1f, true)
            .fillMaxHeight()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f, true)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Font:", modifier = Modifier.width(56.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                    modifier = Modifier
                        .weight(1f, true)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color = MaterialTheme.colorScheme.primary)
                        .clickable { onFontButtonClicked() }
                ) {
                    val name =
                        if(fontState.value.fontFamily is GenericFontFamily)
                            (fontState.value.fontFamily as GenericFontFamily).name
                        else "System Default"
                    Text(
                        text = name.capitalize(Locale.current),
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f, true)
                    )
                    Image(
                        imageVector = Icons.Filled.ArrowDropDown,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        contentDescription = "Font"
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Color:", modifier = Modifier.width(56.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .height(24.dp)
                        .width(60.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .background(colorState.value)
                        .clickable {
                            onColorButtonClicked()
                        }
                )
            }

            SizeSelector(fontState = fontState)

            TextSelector(textState = textState)
        }

        AddTextButton() {
            onAdd()
        }
    }
}

@Composable
fun SizeSelector(fontState: MutableState<TextFont>) {
    var tempSizeState by remember {
        mutableStateOf(
            TextFieldValue(
                text = fontState.value.size.toString(),
                selection = TextRange(Int.MAX_VALUE)
            )
        )
    }

    LaunchedEffect(key1 = tempSizeState) {
        delay(2000)
        if (fontState.value.size != tempSizeState.text.toInt()) {
            fontState.value = fontState.value.getUpdated(size = tempSizeState.text.toInt())
        }
    }

    LaunchedEffect(key1 = fontState.value.size) {
        if (fontState.value.size != tempSizeState.text.toInt()) {
            tempSizeState = tempSizeState.copy(
                text = fontState.value.size.toString(),
                selection = TextRange(Int.MAX_VALUE)
            )
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Size:", modifier = Modifier.width(56.dp))
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = tempSizeState,
            onValueChange = {
                if (it.text.isEmpty())
                    tempSizeState = tempSizeState.copy(
                        text = "0",
                        selection = TextRange(Int.MAX_VALUE)
                    )
                else if (it.text.isDigitsOnly() && !it.text.contains('.') && !it.text.contains('-'))
                    tempSizeState = tempSizeState.copy(
                        text = (it.text.toIntOrNull()?: Int.MAX_VALUE).toString(),
                        selection = TextRange(Int.MAX_VALUE)
                    )
            },
            modifier = Modifier
                .height(24.dp)
                .width(60.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(start = 4.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )
        Text(text = "sp")
    }
}

@Composable
fun TextSelector(textState: MutableState<String>) {
    var tempTextState by remember {
        mutableStateOf(
            TextFieldValue(
                text = textState.value,
                selection = TextRange(Int.MAX_VALUE)
            )
        )
    }

    var initial by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = tempTextState) {
        delay(2000)
        if (textState.value != tempTextState.text) {
            textState.value = tempTextState.text
        }
    }

    LaunchedEffect(key1 = textState.value) {
        if (textState.value != tempTextState.text) {
            tempTextState = tempTextState.copy(
                text = textState.value,
                selection = TextRange(Int.MAX_VALUE)
            )
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Text:", modifier = Modifier.width(56.dp))
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = tempTextState,
            onValueChange = {
                if(!initial) tempTextState = it
                else initial = false
            },
            modifier = Modifier
                .height(24.dp)
                .weight(1f, true)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(start = 4.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            singleLine = true
        )
    }
}

@Composable
fun AddTextButton(onAdd: () -> Unit) {
    Button(onClick = onAdd) {
        Text(text = "Add Text")
    }
}
