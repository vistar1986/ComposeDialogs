package com.michaelflisar.composedialogs.dialogs.menu.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
internal fun MenuRow(
    title: String,
    description: String?,
    icon: @Composable (() -> Unit)?,
    endIcon: @Composable (() -> Unit)? = null,
    textStyleTitle: TextStyle = MaterialTheme.typography.bodyLarge,
    textStyleDescription: TextStyle = MaterialTheme.typography.bodyMedium,
    onMenuClicked: (() -> Unit?)?
) {
    val paddingVertical = 8.dp
    val paddingHorizontal = 16.dp

    val maxLinesText = 2
    val maxLinesSupportingText = 2

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .height(IntrinsicSize.Min)
            .clip(MaterialTheme.shapes.small)
            .then(
                if (onMenuClicked != null) {
                    Modifier.clickable { onMenuClicked() }
                } else Modifier.Companion
            )
            .padding(horizontal = paddingHorizontal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon
        icon?.invoke()

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = paddingVertical)
        ) {
            Text(
                text = title,
                style = textStyleTitle,
                maxLines = maxLinesText,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (description?.isNotEmpty() == true) {
                Text(
                    text = description,
                    style = textStyleDescription,
                    maxLines = maxLinesSupportingText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Icon end
        endIcon?.invoke()
    }
}