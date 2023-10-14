package com.michaelflisar.composedialogs.dialogs.datetime

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogButtons
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.DialogEvent
import com.michaelflisar.composedialogs.core.DialogIcon
import com.michaelflisar.composedialogs.core.DialogState
import com.michaelflisar.composedialogs.core.DialogStyle
import com.michaelflisar.composedialogs.core.DialogTitleStyle
import com.michaelflisar.composedialogs.core.Options
import com.michaelflisar.composedialogs.dialogs.datetime.classes.CalendarPageData
import com.michaelflisar.composedialogs.dialogs.datetime.classes.DateViewState
import com.michaelflisar.composedialogs.dialogs.datetime.classes.SimpleDate
import com.michaelflisar.composedialogs.dialogs.datetime.composables.CalendarHeader
import com.michaelflisar.composedialogs.dialogs.datetime.composables.CalendarMonth
import com.michaelflisar.composedialogs.dialogs.datetime.composables.CalendarSelectionHeader
import com.michaelflisar.composedialogs.dialogs.datetime.composables.CalendarSelectListMonth
import com.michaelflisar.composedialogs.dialogs.datetime.composables.CalendarSelectListYear
import com.michaelflisar.composedialogs.dialogs.datetime.composables.CalendarTodayButton
import com.michaelflisar.composedialogs.dialogs.datetime.utils.DateUtil
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialogDate(
    state: DialogState,
    // Custom - Required
    date: DialogDateState,
    // Custom - Optional
    dateRange: DialogDateRange = DialogDateRange(),
    setup: DialogDateSetup = DialogDateSetup(),
    // Base Dialog - Optional
    title: String = "",
    titleStyle: DialogTitleStyle = DialogDefaults.titleStyle(),
    icon: DialogIcon? = null,
    style: DialogStyle = DialogDefaults.styleDialog(),
    buttons: DialogButtons = DialogDefaults.buttons(),
    options: Options = Options(),
    onEvent: (event: DialogEvent) -> Unit = {}
) {
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    Dialog(
        state,
        title.takeIf { !landscape } ?: "",
        titleStyle,
        icon.takeIf { !landscape },
        style,
        buttons,
        options,
        onEvent = onEvent
    ) {
        val pages = (dateRange.years.last - dateRange.years.first + 1) * 12
        val currentPage = remember(date.year.value, date.month.value) {
            derivedStateOf {
                val offsetInYears = date.year.value - dateRange.years.first
                val offsetMonths = date.month.value - 1
                offsetInYears * 12 + offsetMonths
            }
        }
        val today by remember {
            derivedStateOf { DateUtil.today() }
        }
        val todayPage by remember(pages, today) {
            derivedStateOf {
                val offsetInYears = today.year - dateRange.years.first
                val offsetMonths = today.month - 1
                val offset = offsetInYears * 12 + offsetMonths
                offset.takeIf { it in 0..<pages }
            }
        }
        val pagerState = rememberPagerState(
            initialPage = currentPage.value,
            initialPageOffsetFraction = 0f,
            pageCount = { pages }
        )

        val pageData = remember(pagerState.currentPage) {
            derivedStateOf {
                CalendarPageData(pagerState.currentPage, dateRange)
            }
        }

        val viewState = remember {
            mutableStateOf(DateViewState.Calendar)
        }

        BackHandler(enabled = viewState.value != DateViewState.Calendar, onBack = {
            viewState.value = DateViewState.Calendar
        })

        if (landscape) {
            Row {
                Column {
                    // custom icon and title placement in case of landscape mode!
                    if (icon != null) {
                        when (icon) {
                            is DialogIcon.Painter -> Image(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.CenterHorizontally),
                                painter = icon.painter(),
                                contentDescription = ""
                            )

                            is DialogIcon.Vector -> Icon(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                imageVector = icon.imageVector,
                                contentDescription = "",
                                tint = icon.tint ?: LocalContentColor.current
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (title.isNotEmpty()) {
                        Text(
                            text = title,
                            style = titleStyle.style ?: MaterialTheme.typography.headlineSmall,
                            fontWeight = titleStyle.fontWeight,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    CalendarSelectionHeader(
                        date,
                        setup,
                        pagerState,
                        currentPage
                    )
                    if (todayPage != null && setup.buttonToday != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CalendarTodayButton(setup.buttonToday, date, todayPage!!, today, pagerState)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    CalendarHeader(pagerState, dateRange, setup, pageData, viewState)
                    when (viewState.value) {
                        DateViewState.Calendar -> {
                            HorizontalPager(state = pagerState, verticalAlignment = Alignment.Top) {
                                val pageData = CalendarPageData(it, dateRange)
                                CalendarMonth(date, setup, pageData.year, pageData.month)
                            }
                        }

                        DateViewState.SelectYear -> {
                            CalendarSelectListYear(pageData, setup, dateRange, pagerState, viewState)
                        }

                        DateViewState.SelectMonth -> {
                            CalendarSelectListMonth(pageData, setup, pagerState, viewState)
                        }
                    }
                }
            }
        } else {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarSelectionHeader(
                        date,
                        setup,
                        pagerState,
                        currentPage
                    )
                    if (todayPage != null && setup.buttonToday != null) {
                        Spacer(modifier = Modifier.weight(1f))
                        CalendarTodayButton(setup.buttonToday, date, todayPage!!, today, pagerState)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                CalendarHeader(pagerState, dateRange, setup, pageData, viewState)
                when (viewState.value) {
                    DateViewState.Calendar -> {
                        HorizontalPager(state = pagerState, verticalAlignment = Alignment.Top) {
                            val pageData = CalendarPageData(it, dateRange)
                            CalendarMonth(date, setup, pageData.year, pageData.month)
                        }
                    }

                    DateViewState.SelectYear -> {
                        CalendarSelectListYear(pageData, setup, dateRange, pagerState, viewState)
                    }

                    DateViewState.SelectMonth -> {
                        CalendarSelectListMonth(pageData, setup, pagerState, viewState)
                    }
                }
            }
        }
    }
}

class DialogDateSetup(
    val buttonToday: String? = "Today",
    val firstDayOfWeek: Int = Calendar.MONDAY,
    val selectedDateFormat: String = "EE, dd MMM yyyy",
    val dateFormatMonthSelector: String = "MMM",
    val dateFormatYearSelector: String = "yyyy",
    val dateFormatMonthList: String = "MMMM",
    val dateFormatYearList: String = "yyyy",
    val dateCellHeight: Dp = 48.dp,
    val showNextPreviousMonthButtons: Boolean = true,
    val showNextPreviousYearButtons: Boolean = true
)

class DialogDateRange(
    val years: IntRange = 1900..2100
)

@Composable
fun rememberDialogDateState(
    initialYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    initialMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    initialDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
): DialogDateState {
    val year = rememberSaveable { mutableStateOf(initialYear) }
    val month = rememberSaveable { mutableStateOf(initialMonth) }
    val day = rememberSaveable { mutableStateOf(initialDay) }
    return DialogDateState(year, month, day)
}

class DialogDateState(
    val year: MutableState<Int>,
    val month: MutableState<Int>,
    val day: MutableState<Int>
) {
    internal fun update(date: SimpleDate) {
        year.value = date.year
        month.value = date.month
        day.value = date.day
    }

    internal fun isEqual(date: SimpleDate): Boolean {
        return year.value == date.year && month.value == date.month && day.value == date.day
    }

    override fun toString() =
        "${year.value}-${"%02d".format(month.value)}-${"%02d".format(day.value)}"
}