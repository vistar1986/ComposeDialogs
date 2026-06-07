package com.michaelflisar.composedialogs.dialogs.menu

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedialogs.core.*
import com.michaelflisar.composedialogs.dialogs.menu.composables.MenuRow

// begin-snippet: DialogMenu::constructor
/**
 * Shows a dialog holdinh a list of menu items
 *
 * &nbsp;
 *
 * **Basic Parameters:** all params not described here are derived from [Dialog], check it out for more details
 *
 * @param items a list of menu items
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DialogMenu(
    state: BaseDialogState,
    // Custom - Required
    items: List<MenuItem>,
    // Base Dialog - Optional
    title: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    style: ComposeDialogStyle = DialogDefaults.defaultDialogStyle(),
    buttons: DialogButtons = DialogButtons.DISABLED,
    options: DialogOptions = DialogDefaults.options(),
    onEvent: (event: DialogEvent) -> Unit = {},
)
// end-snippet
{
    val selectedSubMenu = remember { mutableStateOf<List<MenuItem.SubMenu>>(emptyList()) }
    val visibleItems = remember(selectedSubMenu.value) {
        derivedStateOf {
            val list = selectedSubMenu.value.lastOrNull()?.items
            if (list != null) {
                listOf(MenuItem.BackMenu) + list
            } else items
        }
    }

    BackHandler(enabled = selectedSubMenu.value.isNotEmpty()) {
        selectedSubMenu.value = selectedSubMenu.value.dropLast(1)
    }

    Dialog(state, title, icon, style, buttons, options, onEvent = onEvent) {
        DialogContentScrollableColumn {
            //Spacer(modifier = Modifier.height(8.dp))
            visibleItems.value.forEach {
                when (it) {
                    MenuItem.BackMenu -> {
                        Row {
                            IconButton(
                                onClick = {
                                    selectedSubMenu.value = selectedSubMenu.value.dropLast(1)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    }

                    is MenuItem.Custom -> {
                        it.content()
                    }

                    MenuItem.Divider -> {
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                        )
                    }

                    is MenuItem.Item -> {
                        MenuRow(
                            title = it.title,
                            description = it.description,
                            icon = it.icon,
                            onMenuClicked = {
                                it.onClick()
                                if (it.dismissOnClick) {
                                    state.dismiss()
                                }
                                Unit
                            }
                        )
                    }

                    is MenuItem.Region -> {
                        MenuRow(
                            title = it.title,
                            description = it.description,
                            icon = null,
                            textStyleTitle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            textStyleDescription = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            onMenuClicked = null
                        )
                    }

                    is MenuItem.SubMenu -> {
                        MenuRow(
                            title = it.title,
                            description = it.description,
                            icon = it.icon,
                            endIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Open SubMenu"
                                )
                            },
                            onMenuClicked = {
                                selectedSubMenu.value += it
                                Unit
                            }
                        )
                    }
                }
            }
            //Spacer(modifier = Modifier.height(8.dp))
        }
    }
}