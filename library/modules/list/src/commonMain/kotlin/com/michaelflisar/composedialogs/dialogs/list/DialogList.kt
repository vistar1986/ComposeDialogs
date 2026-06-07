package com.michaelflisar.composedialogs.dialogs.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedialogs.core.BaseDialogState
import com.michaelflisar.composedialogs.core.ComposeDialogStyle
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogButtons
import com.michaelflisar.composedialogs.core.DialogContentScrollableLazyColumn
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.DialogEvent
import com.michaelflisar.composedialogs.core.DialogOptions
import com.michaelflisar.composedialogs.core.defaultDialogStyle
import com.michaelflisar.composedialogs.dialogs.list.composables.DialogListItem

// begin-snippet: DialogList::constructor1
/**
 * Displays a dialog containing a selectable or clickable list of items.
 *
 * This overload is intended for already available item lists.
 *
 * Each item is identified by [key]. The key is also used by [selectionMode]
 * to determine and update the selected item state.
 *
 * The [content] slot receives the item and an [DialogList.ItemContext].
 * Custom item implementations should use the context to read the current
 * selection state and call [DialogList.ItemContext.performItemAction] whenever
 * the item is activated by the user.
 *
 * @param state Controls the visibility and lifecycle of the dialog.
 * @param items The items displayed in the dialog.
 * @param key Returns a stable unique identifier for an item. The identifier is
 * also used internally for selection handling.
 * @param content Composable used to render an item. Receives both the item and
 * its associated [DialogList.ItemContext].
 * @param selectionMode Defines how item interactions and selection are handled.
 * @param divider Optional divider displayed between list items. By default, no dividers are shown.
 * @param description Optional text displayed above the list content.
 * @param filter Optional filter configuration used to filter visible items.
 * @param title Optional dialog title.
 * @param icon Optional dialog icon displayed next to the title.
 * @param style Defines the visual appearance of the dialog.
 * @param buttons Defines the dialog buttons.
 * @param options Additional dialog configuration options.
 * @param onEvent Callback invoked for dialog events.
 *
 * Example:
 *
 * ```
 * DialogList(
 *     state = state,
 *     items = users,
 *     key = { it.id },
 *     selectionMode = DialogList.SelectionMode.MultiSelect(
 *         selected = selectedUserIds
 *     ),
 *     content = DialogListDefaults.itemContent(
 *         text = { Text(it.name) },
 *         supportingText = { Text(it.email) }
 *     ),
 *     title = {
 *         Text("Select users")
 *     }
 * )
 * ```
 */
@Composable
fun <T> DialogList(
    state: BaseDialogState,
    items: List<T>,
    key: (item: T) -> Int,
    content: @Composable (item: T, context: DialogList.ItemContext) -> Unit,
    selectionMode: DialogList.SelectionMode<T>,
    divider: @Composable (() -> Unit)?  = null,
    description: String = "",
    filter: DialogList.Filter<T>? = null,
    title: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    style: ComposeDialogStyle = DialogDefaults.defaultDialogStyle(),
    buttons: DialogButtons = DialogDefaults.buttons(),
    options: DialogOptions = DialogDefaults.options(),
    onEvent: (event: DialogEvent) -> Unit = {}
)
// end-snippet
{
    DialogList(
        state = state,
        itemsProvider = DialogList.ItemProvider.List(items),
        key = key,
        content = content,
        selectionMode = selectionMode,
        divider = divider,
        description = description,
        filter = filter,
        title = title,
        icon = icon,
        style = style,
        buttons = buttons,
        options = options,
        onEvent = onEvent
    )
}

// begin-snippet: DialogList::constructor2
/**
 * Displays a dialog containing a selectable or clickable list of items loaded
 * asynchronously.
 *
 * This overload is useful when the list content has to be loaded when the
 * dialog is shown. Until loading is complete, [loadingIndicator] is displayed.
 *
 * If [itemSaver] is provided, the loaded items are stored using
 * [rememberSaveable]. This can be useful when the dialog should preserve loaded
 * data across configuration changes or process recreation.
 *
 * Each item is identified by [key]. The key is also used by [selectionMode]
 * to determine and update the selected item state.
 *
 * The [content] slot receives the item and an [DialogList.ItemContext].
 * Custom item implementations should use the context to read the current
 * selection state and call [DialogList.ItemContext.performItemAction] whenever
 * the item is activated by the user.
 *
 * @param state Controls the visibility and lifecycle of the dialog.
 * @param items Suspended function used to load the items displayed in the dialog.
 * @param key Returns a stable unique identifier for an item. The identifier is
 * also used internally for selection handling.
 * @param content Composable used to render an item. Receives both the item and
 * its associated [DialogList.ItemContext].
 * @param selectionMode Defines how item interactions and selection are handled.
 * @param itemSaver Optional saver used to persist loaded items across state
 * restoration.
 * @param loadingIndicator Composable displayed while items are being loaded.
 * @param divider Optional divider displayed between list items. By default, no dividers are shown.
 * @param description Optional text displayed above the list content.
 * @param filter Optional filter configuration used to filter visible items.
 * @param title Optional dialog title.
 * @param icon Optional dialog icon displayed next to the title.
 * @param style Defines the visual appearance of the dialog.
 * @param buttons Defines the dialog buttons.
 * @param options Additional dialog configuration options.
 * @param onEvent Callback invoked for dialog events.
 *
 * Example:
 *
 * ```
 * DialogList(
 *     state = state,
 *     items = {
 *         repository.loadUsers()
 *     },
 *     key = { it.id },
 *     selectionMode = DialogList.SelectionMode.SingleSelect(
 *         selected = selectedUserId
 *     ),
 *     content = DialogListDefaults.itemContent(
 *         text = { Text(it.name) },
 *         supportingText = { Text(it.email) }
 *     ),
 *     title = {
 *         Text("Select user")
 *     }
 * )
 * ```
 */
@Composable
fun <T> DialogList(
    state: BaseDialogState,
    items: suspend () -> List<T>,
    key: (item: T) -> Int,
    content: @Composable (item: T, context: DialogList.ItemContext) -> Unit,
    selectionMode: DialogList.SelectionMode<T>,
    itemSaver: Saver<MutableState<List<T>>, out Any>? = null,
    loadingIndicator: @Composable () -> Unit = {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    },
    divider: @Composable (() -> Unit)?  = null,
    description: String = "",
    filter: DialogList.Filter<T>? = null,
    title: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    style: ComposeDialogStyle = DialogDefaults.defaultDialogStyle(),
    buttons: DialogButtons = DialogDefaults.buttons(),
    options: DialogOptions = DialogDefaults.options(),
    onEvent: (event: DialogEvent) -> Unit = {}
)
// end-snippet
{
    DialogList(
        state = state,
        itemsProvider = DialogList.ItemProvider.Loader(loadingIndicator, items, itemSaver),
        key = key,
        content = content,
        selectionMode = selectionMode,
        divider = divider,
        description = description,
        filter = filter,
        title = title,
        icon = icon,
        style = style,
        buttons = buttons,
        options = options,
        onEvent = onEvent
    )
}

@Composable
private fun <T> DialogList(
    state: BaseDialogState,
    itemsProvider: DialogList.ItemProvider<T>,
    key: (item: T) -> Int,
    content: @Composable (item: T, context: DialogList.ItemContext) -> Unit,
    selectionMode: DialogList.SelectionMode<T>,
    divider: @Composable (() -> Unit)?  = null,
    description: String,
    filter: DialogList.Filter<T>?,
    title: (@Composable () -> Unit)?,
    icon: (@Composable () -> Unit)?,
    style: ComposeDialogStyle,
    buttons: DialogButtons,
    options: DialogOptions,
    onEvent: (event: DialogEvent) -> Unit
) {
    Dialog(state, title, icon, style, buttons, options, onEvent = onEvent) {
        val itemsState = when (itemsProvider) {
            is DialogList.ItemProvider.List -> remember(itemsProvider) { mutableStateOf(itemsProvider.items) }
            is DialogList.ItemProvider.Loader -> {
                if (itemsProvider.itemSaver != null) {
                    rememberSaveable(saver = itemsProvider.itemSaver) { mutableStateOf(emptyList()) }
                } else {
                    remember { mutableStateOf(emptyList()) }
                }
            }
        }

        val filterText = rememberSaveable { mutableStateOf("") }
        val itemsLoaded = rememberSaveable { mutableStateOf(itemsProvider is DialogList.ItemProvider.List) }
        val filteredItems = remember(itemsState.value, filterText.value, filter) {
            derivedStateOf {
                if (filter == null) itemsState.value
                else itemsState.value.filter { filter.filter(filterText.value, it) }
            }
        }

        LaunchedEffect(filteredItems.value, itemsLoaded.value) {
            if (itemsLoaded.value && filter?.keepSelectionForInvisibleItems == false) {
                DialogListUtil.ensureOnlyVisibleItemsAreSelected(selectionMode, filteredItems.value, key)
            }
        }

        if (itemsProvider is DialogList.ItemProvider.Loader && !itemsLoaded.value) {
            LaunchedEffect(Unit) {
                itemsState.value = itemsProvider.loader()
                itemsLoaded.value = true
            }
        }

        Column {
            if (description.isNotEmpty()) {
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (filter?.infoText != null) {
                val info = filter.infoText.invoke(filteredItems.value.size, itemsState.value.size)
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    value = filterText.value,
                    onValueChange = { filterText.value = it },
                    supportingText = if (info.isNotEmpty()) {
                        { Text(modifier = Modifier.align(Alignment.End), text = info, style = MaterialTheme.typography.bodySmall) }
                    } else null,
                    trailingIcon = if (filter.showClearIcon && filterText.value.isNotEmpty()) {
                        {
                            IconButton(onClick = { filterText.value = "" }) {
                                Icon(imageVector = Icons.Outlined.Clear, contentDescription = "Clear")
                            }
                        }
                    } else null
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (selectionMode is DialogList.SelectionMode.MultiSelect && selectionMode.showSelectionCounter) {
                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = "${selectionMode.selected.value.size}/${itemsState.value.size}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            DialogContentScrollableLazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
                if (itemsState.value.isEmpty() && itemsProvider is DialogList.ItemProvider.Loader) {
                    item(key = "DialogList-PLACEHOLDER") { itemsProvider.loadingIndicator() }
                }

                items(filteredItems.value, key = { key(it) }) { item ->
                    DialogListItem(
                        state = state,
                        item = item,
                        itemKey = key(item),
                        selectionMode = selectionMode,
                        content = content,
                        onEvent = onEvent
                    )
                    if (divider != null && item != filteredItems.value.last()) {
                        divider()
                    }
                }
            }
        }
    }
}

@Stable
object DialogList {

    sealed class SelectionMode<T> {
        class SingleSelect<T>(
            val selected: MutableState<Int?>,
            val selectOnRadioButtonClickOnly: Boolean = true,
            val closeOnSelect: Boolean = false
        ) : SelectionMode<T>()

        class MultiSelect<T>(
            val selected: MutableState<List<Int>>,
            val selectOnCheckboxClickOnly: Boolean = true,
            val showSelectionCounter: Boolean = false
        ) : SelectionMode<T>()

        class SingleClickAndClose<T>(val onItemClicked: (item: T) -> Unit) : SelectionMode<T>()
        class MultiClick<T>(val onItemClicked: (item: T) -> Unit) : SelectionMode<T>()
    }

    class ItemContext internal constructor(
        val selected: State<Boolean>,
        val selectionMode: SelectionMode<*>,
        val performItemAction: () -> Unit
    )

    internal sealed class ItemProvider<T> {
        class List<T>(val items: kotlin.collections.List<T>) : ItemProvider<T>()
        class Loader<T>(
            val loadingIndicator: @Composable () -> Unit,
            val loader: suspend () -> kotlin.collections.List<T>,
            val itemSaver: Saver<MutableState<kotlin.collections.List<T>>, out Any>?
        ) : ItemProvider<T>()
    }

    class Filter<T>(
        val filter: (filter: String, item: T) -> Boolean,
        val infoText: @Composable ((count: Int, total: Int) -> String)? = { count, total -> "$count/$total" },
        val keepSelectionForInvisibleItems: Boolean = true,
        val showClearIcon: Boolean = true
    )
}
