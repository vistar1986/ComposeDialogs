package com.michaelflisar.composedialogs.dialogs.list.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.ProvideTextStyle

@Composable
internal fun DialogListContent(
    text: @Composable () -> Unit,
    supportingText: (@Composable () -> Unit)? = null
) {
    Column {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface
        ) {
            ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                text()
            }
        }

        supportingText?.let {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                    it()
                }
            }
        }
    }
}