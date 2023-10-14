package com.michaelflisar.composedialogs.demo.demos

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedialogs.core.*
import com.michaelflisar.composedialogs.demo.DemoDialogButton
import com.michaelflisar.composedialogs.demo.DemoDialogRegion
import com.michaelflisar.composedialogs.demo.DemoDialogRow
import com.michaelflisar.composedialogs.demo.showToast
import com.michaelflisar.composedialogs.dialogs.datetime.*

@Composable
fun DateTimeDemos(style: DialogStyle, icon: DialogIcon?) {
    DemoDialogRegion("DateTime Dialogs")
    DemoDialogRow {
        DemoDialogDate1(style, icon)
    }
    DemoDialogRow {
        DemoDialogTime1(style, icon, false)
        DemoDialogTime1(style, icon, true)
    }
}

@Composable
private fun RowScope.DemoDialogDate1(style: DialogStyle, icon: DialogIcon?) {
    val context = LocalContext.current
    val state = rememberDialogState()
    if (state.showing) {

        // special state for date dialog
        val date = rememberDialogDateState()
        // optional settings
        val setup = DialogDateSetup(
            dateCellHeight = 32.dp
        )
        val dateRange = DialogDateRange()

        DialogDate(
            state = state,
            date = date,
            setup = setup,
            dateRange = dateRange,
            icon = icon,
            title = "Select Date",
            style = style,
            onEvent = {
                if (it is DialogEvent.Button && it.button == DialogButtonType.Positive) {
                    context.showToast("Selected Date: $date")
                } else {
                    context.showToast("Event $it")
                }
            }
        )
    }
    DemoDialogButton(
        state,
        Icons.Default.CalendarMonth,
        "Date Dialog",
        "Shows a date picker dialog"
    )
}

@Composable
private fun RowScope.DemoDialogTime1(style: DialogStyle, icon: DialogIcon?, is24Hours: Boolean) {
    val context = LocalContext.current
    val state = rememberDialogState()
    if (state.showing) {

        // special state for time dialog
        val time = rememberDialogTimeState()
        // optional settings
        val setup = DialogTimeSetup(is24Hours = is24Hours)

        DialogTime(
            state = state,
            time = time,
            setup = setup,
            icon = icon,
            title = "Select Time",
            style = style,
            onEvent = {
                if (it is DialogEvent.Button && it.button == DialogButtonType.Positive) {
                    context.showToast("Selected Time: $time")
                } else {
                    context.showToast("Event $it")
                }
            }
        )
    }
    DemoDialogButton(
        state,
        Icons.Default.Schedule,
        "Time Dialog",
        "Shows a time picker dialog (24h = $is24Hours)"
    )
}
