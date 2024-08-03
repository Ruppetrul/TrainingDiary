package com.example.trainingdiary.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MyFragmentStateAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    private val startDate = LocalDate.of(1970, 1, 1)
    private val maxDate = LocalDate.now().plusYears(50)

    fun getPositionForDate(date: LocalDate): Int {
        return ChronoUnit.DAYS.between(startDate, date).toInt()
    }

    override fun getItemCount(): Int {
        return ChronoUnit.DAYS.between(startDate, maxDate).toInt()
    }

    override fun createFragment(position: Int): Fragment {
        return HistoryFragment.newInstance(position)
    }
}
