package com.example.trainingdiary.ui.home.CalendarSheet

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.trainingdiary.MainActivity
import com.example.trainingdiary.R
import com.example.trainingdiary.databinding.FragmentCalendarSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.util.Calendar

class CalendarSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCalendarSheetBinding? = null

    var viewModel : CalendarSheetViewModel? = null

    private var listener: CalendarSheetListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? CalendarSheetListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this).get(CalendarSheetViewModel::class.java)

        _binding = FragmentCalendarSheetBinding.inflate(inflater, container, false)


        return inflater.inflate(R.layout.fragment_calendar_sheet, container, false)
    }

    private fun timestampToCalendarDay(timestamp: Long): CalendarDay {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        val res = CalendarDay.from(calendar)
        Log.d("TAG", res.toString())
        return res
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendarView: MaterialCalendarView = view.findViewById(R.id.calendarView)

        viewModel?.getTrainingDates()
        viewModel!!.trainingDates.observe(viewLifecycleOwner) { dates ->
            calendarView.addDecorators(HistoryDaysDecorators(dates.map { timestampToCalendarDay(it) }))
        }

        calendarView.addDecorators(CurrentDayDecorator(getActivity() as MainActivity, CalendarDay.today()))

        calendarView.setOnDateChangedListener { widget, date, selected ->
            onDateSelected(date)
        }
    }

    class HistoryDaysDecorators(private val historyDays: List<CalendarDay>) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return historyDays.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(5f, Color.RED))
        }

    }

    class CurrentDayDecorator(context: Activity?, private val currentDay: CalendarDay) : DayViewDecorator {
        private val drawable: Drawable? = ContextCompat.getDrawable(context!!, R.drawable.ic_launcher_background)

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day == currentDay
        }

        override fun decorate(view: DayViewFacade) {
            view.setSelectionDrawable(drawable!!)
        }
    }

    private fun onDateSelected(date: CalendarDay) {
        listener!!.onDateSelected(date)
        dismissAllowingStateLoss()
    }
}
