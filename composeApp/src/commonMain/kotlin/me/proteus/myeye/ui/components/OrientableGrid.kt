package me.proteus.myeye.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun OrientableGrid(
    qualifier: Boolean,
    columnModifier: Modifier,
    rowModifier: Modifier,
    arrangement: Arrangement.HorizontalOrVertical,
    horizontalAlignment: Alignment.Horizontal?,
    verticalAlignment: Alignment.Vertical?,
    content: @Composable (Boolean) -> Unit
) {
    if (qualifier) {
        Row(
            modifier = rowModifier,
            verticalAlignment = verticalAlignment ?: Alignment.Top,
            horizontalArrangement = arrangement
        ) {
            content(qualifier)
        }
    } else {
        Column(
            modifier = columnModifier,
            verticalArrangement = arrangement,
            horizontalAlignment = horizontalAlignment ?: Alignment.Start
        ) {
            content(qualifier)
        }
    }
}

@Composable
fun OrientableGrid(
    qualifier: Boolean,
    modifier: Modifier,
    arrangement: Arrangement.HorizontalOrVertical,
    horizontalAlignment: Alignment.Horizontal?,
    verticalAlignment: Alignment.Vertical?,
    content: @Composable (Boolean) -> Unit
) {
    OrientableGrid(
        qualifier,
        modifier,
        modifier,
        arrangement,
        horizontalAlignment,
        verticalAlignment,
        content
    )
}