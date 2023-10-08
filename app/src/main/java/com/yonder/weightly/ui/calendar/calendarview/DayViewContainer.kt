package com.yonder.weightly.ui.calendar.calendarview

import android.view.View
import com.kizitonwose.calendar.view.ViewContainer
import com.yonder.weightly.databinding.CalendarDayLayoutBinding

class DayViewContainer(view: View) : ViewContainer(view) {

    val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
}