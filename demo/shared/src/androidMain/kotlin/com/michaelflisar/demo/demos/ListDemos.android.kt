package com.michaelflisar.demo.demos

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.michaelflisar.composedialogs.core.ComposeDialogStyle
import com.michaelflisar.composedialogs.dialogs.list.DialogList
import com.michaelflisar.composedialogs.dialogs.list.DialogListDefaults
import com.michaelflisar.demo.classes.AppItem
import com.michaelflisar.democomposables.layout.DemoRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun DemoListAppSelector(
    style: ComposeDialogStyle,
    icon: (@Composable () -> Unit)?,
    showInfo: (info: String) -> Unit,
) {
    val context = LocalContext.current

    // Item Content Renderer - here you can provide composables for the content, icon and trailing area...
    // => I have defined some default composables which I will use here
    val customItemRenderer = DialogListDefaults.itemContent<AppItem>(
        text = { Text(it.label, maxLines = 2) },
        supportingText = { Text("ID: ${it.id}", maxLines = 2) },
        icon = {
            Image(
                painter = rememberDrawablePainter(it.icon(context)),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        }
    )
    val fullyCustomItemRenderer = @Composable { item: AppItem, context: DialogList.ItemContext ->
        // ATTENTION: you are responsible to handle the click behaviour yourself in this case - you can use the provided context for that!
        // and you are responsible to show the selected state if you want to - you can use the provided context for that as well!

        CompositionLocalProvider(
            LocalContentColor provides if (context.selected.value) MaterialTheme.colorScheme.onPrimary else LocalContentColor.current
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .clickable {
                    context.performItemAction()
                }
                .then(
                    if (context.selected.value)
                        Modifier.background(MaterialTheme.colorScheme.primary)
                    else Modifier
                )
                .padding(8.dp)
        ) {
            Text(item.label, maxLines = 2, style = MaterialTheme.typography.bodyLarge)
            Text("ID: ${item.id}", maxLines = 2, style = MaterialTheme.typography.bodyMedium)
        }
    }
    }
    val customItemIdProvider = { item: AppItem -> item.id }

    DemoRow {
        DemoList(
            style = style,
            icon = icon,
            showInfo = showInfo,
            content = customItemRenderer,
            key = customItemIdProvider,
            itemsLoader = { loadApps(context) },
            // optional => if no saver is provided, data will be reloaded and not retained between e.g. screen rotations
            // AppItem is parcelable so autoSaver can handle it!
            itemSaver = autoSaver(),
            selectionMode = DialogList.SelectionMode.MultiClick {
                showInfo("Selected in Multi Click Mode: ${it.id}")
            },
            infos = "Asynchronous loaded items with custom item content..."
        )
        DemoList(
            style = style,
            icon = icon,
            showInfo = showInfo,
            content = fullyCustomItemRenderer,
            key = customItemIdProvider,
            itemsLoader = { loadApps(context) },
            // optional => if no saver is provided, data will be reloaded and not retained between e.g. screen rotations
            // AppItem is parcelable so autoSaver can handle it!
            itemSaver = autoSaver(),
            selectionMode = DialogList.SelectionMode.MultiSelect(
                selected = rememberSaveable { mutableStateOf(emptyList()) }
            ),
            infos = "Asynchronous loaded items with FULLY custom item content + Multi Select...",
            divider = {
                Spacer(modifier = Modifier.height(4.dp))
            },
        )
    }
}


private suspend fun loadApps(context: Context): List<AppItem> {
    val context = context.applicationContext
    return withContext(Dispatchers.IO) {
        val items = ArrayList<AppItem>()
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        var id = 1
        for (info in resolveInfos) {
            val text = info.loadLabel(pm).toString()
            //val icon = info.loadIcon(context.packageManager)
            items.add(AppItem(id, info, text))
            id++
        }
        items.sortWith { o1, o2 -> o1.label.compareTo(o2.label, true) }
        items
    }
}