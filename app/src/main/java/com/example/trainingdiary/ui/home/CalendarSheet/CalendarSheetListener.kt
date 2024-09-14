package com.example.trainingdiary.ui.home.CalendarSheet

import com.prolificinteractive.materialcalendarview.CalendarDay

interface CalendarSheetListener {
    fun onDateSelected(date: CalendarDay)
}