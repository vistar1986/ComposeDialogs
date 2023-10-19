package com.michaelflisar.composedialogs.dialogs.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogButtons
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.DialogEvent
import com.michaelflisar.composedialogs.core.DialogState
import com.michaelflisar.composedialogs.core.DialogStyle
import com.michaelflisar.composedialogs.core.Options
import com.michaelflisar.composedialogs.dialogs.input.composables.DialogInputTextField

/**
 * Shows a dialog with an input field
 *
 * &nbsp;
 *
 * **Basic Parameters:** all params not described here are derived from [Dialog], check it out for more details
 *
 * @param input the selected text
 * @param inputLabel the optional label of the input field
 * @param inputPlaceholder the placeholder the for the input field
 * @param singleLine if true, the input field will only allow a single line
 * @param maxLines the max lines for the input field
 * @param minLines the min lines for the input field
 * @param keyboardOptions the [KeyboardOptions] for the input field
 * @param enabled if true, the input field is enabled
 * @param clearable if true, the input field can be cleared by a trailing clear icon
 * @param prefix the prefix for the input field
 * @param suffix the prefix for the input field
 * @param textStyle the [TextStyle] for the input field
 * @param validator the [DialogInputValidator] for the input field - use [rememberDialogInputValidator]
 * @param requestFocus if true, the input field will request the focus when the dialog si shown (and open the keyboard)
 * @param selectionState if initial selection state ([DialogInput.SelectionState]) of the input field
 * @param onTextStateChanged an optional callback that will be called whenever the value of the input field changes
 */
@Composable
fun DialogInput(
    // Base Dialog - State
    state: DialogState,
    // Custom - Required
    input: MutableState<String>,
    inputLabel: String = "",
    // Custom - Optional
    inputPlaceholder: String = "",
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    enabled: Boolean = true,
    clearable: Boolean = true,
    prefix: String = "",
    suffix: String = "",
    textStyle: TextStyle = LocalTextStyle.current,
    validator: DialogInputValidator = rememberDialogInputValidator(),
    requestFocus: Boolean = false,
    selectionState: DialogInput.SelectionState = DialogInput.SelectionState.Default,
    onTextStateChanged: (valid: Boolean, text: String) -> Unit = { _, _ -> },
    // Base Dialog - Optional
    title: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    style: DialogStyle = DialogDefaults.styleDialog(),
    buttons: DialogButtons = DialogDefaults.buttons(),
    options: Options = Options(),
    onEvent: (event: DialogEvent) -> Unit = {}
) {
    Dialog(state, title, icon, style, buttons, options, onEvent = onEvent) {
        val modifier = when (style) {
            is DialogStyle.BottomSheet -> Modifier.fillMaxWidth()
            is DialogStyle.Dialog -> Modifier
        }
        DialogInputTextField(
            modifier,
            input,
            inputLabel,
            inputPlaceholder,
            singleLine,
            maxLines,
            minLines,
            keyboardOptions,
            enabled,
            clearable,
            prefix,
            suffix,
            textStyle,
            validator,
            requestFocus,
            selectionState,
            onTextStateChanged
        )
    }
}

/**
 * convenient function for [DialogInput]
 *
 * @param input the initial text for the input field
 *
 * @return a state holding the current input value
 */
@Composable
fun rememberDialogInput(
    text: String
): MutableState<String> {
    return rememberSaveable { mutableStateOf(text) }
}

@Stable
object DialogInput {

    /**
     * Selection State of the input field (selection, cursor position)
     */
    sealed class SelectionState {
        /**
         * default input field behaviour
         */
        data object Default : SelectionState()

        /**
         * if the input field is initially focused, cursor will be placed at the end
         */
        data object CursorEnd : SelectionState()

        /**
         * if the input field is initially focused the whole text will be selected
         */
        data object SelectAll : SelectionState()

        /**
         * if the input field is initially focused, the defined range of the text will be selected
         */
        class Selection(val start: Int, val end: Int) : SelectionState()
    }
}