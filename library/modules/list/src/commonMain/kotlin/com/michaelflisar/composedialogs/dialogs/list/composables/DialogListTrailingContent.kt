package com.michaelflisar.composedialogs.dialogs.list.composables

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
internal fun DialogListTrailingContent(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
            content()
        }
    }
}