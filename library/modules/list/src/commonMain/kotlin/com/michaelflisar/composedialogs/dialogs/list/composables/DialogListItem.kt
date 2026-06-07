package com.michaelflisar.composedialogs.dialogs.list.composables

import androidx.compose.runtime.Composable
import com.michaelflisar.composedialogs.core.BaseDialogState
import com.michaelflisar.composedialogs.core.DialogEvent
import com.michaelflisar.composedialogs.dialogs.list.DialogList
import com.michaelflisar.composedialogs.dialogs.list.DialogListUtil

@Composable
internal fun <T> DialogListItem(
    state: BaseDialogState,
    item: T,
    itemKey: Int,
    selectionMode: DialogList.SelectionMode<T>,
    content: @Composable (item: T, context: DialogList.ItemContext) -> Unit,
    onEvent: (event: DialogEvent) -> Unit
) {
    val context = DialogListUtil.createItemContext(
        item = item,
        itemKey = itemKey,
        selectionMode = selectionMode,
        state = state,
        onEvent = onEvent
    )
    content(item, context)
}
