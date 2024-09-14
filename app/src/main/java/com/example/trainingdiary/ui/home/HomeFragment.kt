package com.example.trainingdiary.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.trainingdiary.AppDatabase
import com.example.trainingdiary.R
import com.example.trainingdiary.databinding.FragmentHomeBinding
import com.example.trainingdiary.models.ExerciseHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private var homeViewModel : HomeViewModel? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_home, false)
            .build()

        val exerciseId = arguments?.getInt("exerciseId") ?: 0
        var position = arguments?.getInt("positionId") ?: 0

        viewPager = binding.viewPager
        val adapter = MyFragmentStateAdapter(requireActivity())
        homeViewModel!!.adapter = adapter
        viewPager.adapter = adapter

        val todayPosition = adapter.getPositionForDate(LocalDate.now())

        val dayOffset = calculateShift(-(todayPosition - position))
        handleNewExercise(exerciseId, dayOffset.time)

        position = if (position != 0) position else todayPosition
        viewPager.setCurrentItem(position,false)

        handleFloatButtonAndTitle(todayPosition, position, navOptions, viewPager)

        return root
    }

    private fun handleNewExercise(
        exerciseId: Int,
        time: Date
    ) {
        if (exerciseId != 0) {
            val database = AppDatabase.getDatabase(requireContext())
            CoroutineScope(Dispatchers.IO).launch {
                database.exerciseDao().insertHistory(
                    ExerciseHistory(0, exerciseId, time)
                ).let {
                    arguments?.putInt("exerciseId", 0)
                }
            }
        }
    }

    private fun handleFloatButtonAndTitle(
        todayPosition: Int,
        currentPosition: Int,
        navOptions: NavOptions,
        viewPager: ViewPager2
    ) {
        var currentPosition2 = currentPosition
        binding.floatingActionButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("positionId", currentPosition2)
            findNavController().navigate(
                R.id.action_HomeFragment_to_BodyPartFragment,
                bundle,
                navOptions
            )
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition2 = position
                handleTitle(todayPosition, position)
            }
        })
        handleTitle(todayPosition, currentPosition2)
    }

    private fun calculateShift(dayOffset: Int): Calendar {
        val currentDate = Date()

        val calendar = Calendar.getInstance()
        calendar.time = currentDate

        calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
        return calendar
    }

    private fun handleTitle(todayPosition: Int, position: Int) {
        val calendar = calculateShift(-(todayPosition - position))
        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

        val formattedDayOfWeek = dayOfWeek?.substring(0, 1)!!.uppercase() + dayOfWeek.substring(1)

        val todayCalendar = Calendar.getInstance()
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0)
        todayCalendar.set(Calendar.MINUTE, 0)
        todayCalendar.set(Calendar.SECOND, 0)
        todayCalendar.set(Calendar.MILLISECOND, 0)

        val dateTitle = when {
            calendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR) -> {
                getString(R.string.today)
            }
            calendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR) - 1 -> {
                getString(R.string.yesterday)
            }
            calendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR) + 1 -> {
                getString(R.string.tomorrow)
            }
            else -> "$formattedDayOfWeek, $dayOfMonth $monthName"
        }

        val title = if (dateTitle == "$formattedDayOfWeek, $dayOfMonth $monthName") {
            dateTitle
        } else {
            "$formattedDayOfWeek, $dayOfMonth $monthName ($dateTitle)"
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        homeViewModel!!.position.observe(viewLifecycleOwner, Observer { new_position ->
            viewPager.setCurrentItem(new_position, false)

            //handleFloatButtonAndTitle(position2, position2, navOptions, viewPager)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("TAG", "Home onViewCreated: ")
        super.onViewCreated(view, savedInstanceState)
    }
}