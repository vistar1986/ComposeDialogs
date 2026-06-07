package com.michaelflisar.composedialogs.dialogs.frequency.composables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.composedialogs.dialogs.frequency.DialogFrequency
import com.michaelflisar.composedialogs.dialogs.time.DialogTime
import kotlinx.datetime.LocalTime

@Composable
internal fun Time(
    selectedTime: MutableState<LocalTime>,
    texts: DialogFrequency.Texts,
    modifier: Modifier = Modifier.Companion
) {
    val showDialogTime = rememberDialogState()
    InputButton(
        modifier = modifier,
        title = texts.time,
        value = selectedTime.value.toString(),
        onClick = {
            showDialogTime.show()
        }
    )
    if (showDialogTime.visible) {
        DialogTime(
            state = showDialogTime,
            time = selectedTime,
            title = { Text(texts.time) }
        )
    }
}