package com.michaelflisar.composedialogs.core.style

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.composeunstyled.DragIndication
import com.composeunstyled.ModalBottomSheetProperties
import com.composeunstyled.Scrim
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.UnstyledModalBottomSheet
import com.composeunstyled.rememberModalBottomSheetState
import com.michaelflisar.composedialogs.core.BaseDialogState
import com.michaelflisar.composedialogs.core.ComposeDialogStyle
import com.michaelflisar.composedialogs.core.DialogButtons
import com.michaelflisar.composedialogs.core.DialogEvent
import com.michaelflisar.composedialogs.core.DialogOptions
import com.michaelflisar.composedialogs.core.StyleOptions
import com.michaelflisar.composedialogs.core.composables.ComposeDialogButtons
import com.michaelflisar.composedialogs.core.composables.ComposeDialogContent
import com.michaelflisar.composedialogs.core.composables.ComposeDialogTitle
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

object BottomSheetStyleDefaults {

    val peekHeight: (containerHeight: Dp, sheetHeight: Dp) -> Dp =
        { containerHeight, sheetHeight -> (containerHeight * .5f).coerceAtMost(sheetHeight) }

    val shape: Shape
        @Composable get() = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surface

    val iconColor
        @Composable get() = MaterialTheme.colorScheme.secondary

    val titleColor
        @Composable get() = MaterialTheme.colorScheme.onSurface

    val contentColor
        @Composable get() = MaterialTheme.colorScheme.onSurface
}

internal class BottomSheetStyle(
    private val dragHandle: Boolean,
    private val peekHeight: ((containerHeight: Dp, sheetHeight: Dp) -> Dp)?,
    private val expandInitially: Boolean,
    private val velocityThreshold: () -> Dp,
    private val positionalThreshold: (totalDistance: Dp) -> Dp,
    private val animateShow: Boolean = false,
    // DialogProperties
    private val dismissOnBackPress: Boolean,
    private val dismissOnClickOutside: Boolean,
    private val scrim: Boolean,
    // Style
    private val options: StyleOptions = StyleOptions(),
    private val shape: Shape,
    private val containerColor: Color,
    private val iconColor: Color,
    private val titleColor: Color,
    private val contentColor: Color,
) : ComposeDialogStyle {

    override val type = ComposeDialogStyle.Type.BottomSheet

    @Composable
    override fun Show(
        title: (@Composable () -> Unit)?,
        icon: @Composable (() -> Unit)?,
        buttons: DialogButtons,
        options: DialogOptions,
        state: BaseDialogState,
        onEvent: (event: DialogEvent) -> Unit,
        content: @Composable () -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()

        val spacing = spacing()

        val peek: SheetDetent? = peekHeight?.let {
            SheetDetent("peek", it)
        }

        val shouldDismissOnBackPress by remember {
            derivedStateOf { dismissOnBackPress && state.interactionSource.dismissAllowed.value }
        }
        val shouldDismissOnClickOutside by remember {
            derivedStateOf { dismissOnClickOutside && state.interactionSource.dismissAllowed.value }
        }

        val initialAnimationDone = remember { mutableStateOf(false) }
        val initialDetent = if (expandInitially || peek == null) SheetDetent.FullyExpanded else peek

        val bottomSheetState = rememberModalBottomSheetState(
            initialDetent = if (animateShow && !initialAnimationDone.value) SheetDetent.Hidden else initialDetent,
            detents = listOfNotNull(SheetDetent.Hidden, peek, SheetDetent.FullyExpanded),
            confirmDetentChange = {
                val confirm = if (it == SheetDetent.Hidden) {
                    state.interactionSource.dismissAllowed.value
                } else true
                confirm
            },
            velocityThreshold = velocityThreshold,
            positionalThreshold = positionalThreshold
        )
        val bottomSheetProperties = ModalBottomSheetProperties(
            dismissOnBackPress = shouldDismissOnBackPress,
            dismissOnClickOutside = shouldDismissOnClickOutside,
            offsetForIme = true
        )

        var buttonPressed = false
        //var dismissed = false
        val onDismiss = {
            if (state.interactionSource.dismissAllowed.value) {
                coroutineScope.launch {
                    //println("dismissed - animateTo...")
                    //dismissed = true
                    bottomSheetState.animateTo(SheetDetent.Hidden)
                    if (buttonPressed)
                        state.dismiss()
                    else
                        state.dismiss(onEvent)
                    //println("dismissed - state.visible = ${state.visible}")
                }
                true
            } else {
                bottomSheetState.targetDetent = SheetDetent.FullyExpanded
                false
            }
        }

        val density = LocalDensity.current

        // Hack, to detect hidden state after animation because animateTo does work reliably
        LaunchedEffect(bottomSheetState.currentDetent) {
            if (initialAnimationDone.value &&
                bottomSheetState.currentDetent == SheetDetent.Hidden &&
                bottomSheetState.targetDetent == SheetDetent.Hidden
            ) {
                //println("Detent is hidden => calling onDismiss...")
                onDismiss()
            }
        }

        //LaunchedEffect(bottomSheetState.isIdle) {
        //    println("TEST - isIdle = ${bottomSheetState.isIdle} | target = ${bottomSheetState.currentDetent.identifier} | interaction allowed = ${state.interactionSource.dismissAllowed.value}")
        //    if (bottomSheetState.isIdle &&
        //        bottomSheetState.currentDetent == SheetDetent.Hidden &&
        //        !state.interactionSource.dismissAllowed.value
        //    ) {
        //        println("TEST - show again")
        //        bottomSheetState.currentDetent = SheetDetent.FullyExpanded
        //    }
        //}

        LaunchedEffect(initialAnimationDone.value) {
            if (!initialAnimationDone.value) {
                bottomSheetState.targetDetent = initialDetent
                initialAnimationDone.value = true
            }
        }

        val scrimSize = remember { mutableStateOf(DpSize.Zero) }

        UnstyledModalBottomSheet(
            state = bottomSheetState,
            properties = bottomSheetProperties,
            onDismiss = {
                onDismiss()
            },
            overlay = {
                if (scrim) {
                    Scrim(
                        enter = fadeIn(),
                        exit = fadeOut(),
                        scrimColor = MaterialTheme.colorScheme.scrim.copy(0.3f),
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged {
                                scrimSize.value =
                                    with(density) { DpSize(it.width.toDp(), it.height.toDp()) }
                            }
                    )
                }
            }
        ) {

            val footerSize = remember { mutableStateOf(DpSize.Zero) }
            val sheetSize = remember { mutableStateOf(DpSize.Zero) }

            val paddingTop = 12.dp
            val dragHandleHeight = 4.dp
            val dragHandleVerticalPadding = 22.dp

            val isCompact by remember {
                derivedStateOf {
                    scrimSize.value.width < 640.dp
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = paddingTop)
                    .then(
                        if (isCompact) {
                            Modifier
                        } else Modifier.padding(horizontal = 56.dp)
                    )
                    .statusBarsPadding()
                    .padding(
                        WindowInsets.navigationBars
                            .only(WindowInsetsSides.Horizontal)
                            .asPaddingValues()
                    ),
                contentAlignment = Alignment.TopCenter,
            ) {
                Sheet(
                    modifier = Modifier
                        .onSizeChanged {
                            sheetSize.value =
                                with(density) { DpSize(it.width.toDp(), it.height.toDp()) }
                        }
                        .shadow(4.dp, shape)
                        .clip(shape)
                        .background(containerColor)
                        .widthIn(max = 640.dp)
                        .fillMaxWidth()
                ) {

                    Column(
                        Modifier
                            .fillMaxWidth()
                    ) {

                        // 1) Drag Header On Top
                        if (dragHandle) {
                            DragIndication(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(vertical = dragHandleVerticalPadding)
                                    .background(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        shape = RoundedCornerShape(percent = 50)
                                    )
                                    .width(32.dp)
                                    .height(dragHandleHeight)
                            )
                        }

                        // 2) Icon + Title
                        ComposeDialogTitle(
                            modifier = Modifier
                                .padding(horizontal = 24.dp),
                            title = title,
                            icon = icon,
                            iconColor = iconColor,
                            titleColor = titleColor,
                            options = this@BottomSheetStyle.options,
                        )

                        // 3) Content
                        ComposeDialogContent(
                            content = content,
                            contentColor = contentColor,
                            modifier = Modifier.weight(weight = 1f, fill = false)
                                .padding(horizontal = 24.dp),
                            bottomPadding = spacing.contentPadding(buttons)
                        )

                        // 4) Footer
                        Column(
                            modifier = Modifier
                                .onSizeChanged {
                                    footerSize.value =
                                        with(density) { DpSize(it.width.toDp(), it.height.toDp()) }
                                }
                                .then(
                                    if (buttons.enabled) {
                                        Modifier.offset {
                                            val limit =
                                                paddingTop.toPx() + footerSize.value.height.toPx() +
                                                        (if (dragHandle) (dragHandleHeight.toPx() + dragHandleVerticalPadding.toPx() * 2) else 0f)


                                            var yOffset =
                                                (sheetSize.value.height.toPx() + paddingTop.toPx() - bottomSheetState.offset).roundToInt() * -1 + 1
                                            if (bottomSheetState.offset < limit) {
                                                // we only see the buttons and handle anymore
                                                //println("bottom reached")
                                                yOffset += (limit - bottomSheetState.offset).roundToInt()
                                            }

                                            println("yOffset = $yOffset | sheet height = ${sheetSize.value.height} | offset = ${bottomSheetState.offset} | limit = $limit")
                                            IntOffset(
                                                0,
                                                yOffset // +1 for rounding errors? otherwise it sometimes does show a pixel row below the bottom that is not overlayed by the buttons...
                                            )
                                        }
                                    } else Modifier
                                )
                        ) {
                            ComposeDialogButtons(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(containerColor)
                                    .padding(horizontal = 24.dp),
                                buttons = buttons,
                                state = state,
                                options = options,
                                dismissOnButtonPressed = {
                                    buttonPressed = true
                                    onDismiss()
                                },
                                onEvent = onEvent
                            )

                            // 5) Spacer behind nav bar
                            Spacer(
                                Modifier
                                    .fillMaxWidth()
                                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
                                    .background(containerColor)
                            )
                        }
                    }
                }
            }
        }
    }
}