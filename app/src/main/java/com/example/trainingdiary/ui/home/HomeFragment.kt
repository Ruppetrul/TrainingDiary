package com.example.trainingdiary.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_home, false)
            .build()

        val itemId = arguments?.getInt("exerciseId")
        val position = arguments?.getInt("positionId", 0)

        val viewPager: ViewPager2 = binding.viewPager
        val adapter = MyFragmentStateAdapter(requireActivity())
        viewPager.adapter = adapter
        val todayPosition = adapter.getPositionForDate(LocalDate.now())

        val database = AppDatabase.getDatabase(requireContext())
        if (itemId != null && itemId != 0 && position != null && position != 0) {
            CoroutineScope(Dispatchers.IO).launch {
                val calendar = calculateShift(todayPosition, position)

                database.exerciseDao().insertHistory(
                    ExerciseHistory(0, itemId, calendar.time)
                ).let {
                    arguments?.putInt("exerciseId", 0)
                }
            }
        }

        if (position != 0 && position != null) {
            viewPager.setCurrentItem(position, false)
        } else {
            viewPager.setCurrentItem(todayPosition, false)
        }

        handleFloatButtonAndTitle(todayPosition, navOptions, viewPager)

        return root
    }

    private fun handleFloatButtonAndTitle(
        todayPosition: Int,
        navOptions: NavOptions,
        viewPager: ViewPager2
    ) {
        var currentPosition = todayPosition
        binding.floatingActionButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("positionId", currentPosition)
            findNavController().navigate(
                R.id.action_currentFragment_to_listFragment,
                bundle,
                navOptions
            )
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
                handleTitle(todayPosition, position)
            }
        })
        handleTitle(todayPosition, currentPosition)
    }

    private fun calculateShift(todayPosition: Int, position: Int): Calendar {
        val dayOffset = -(todayPosition - position)

        val currentDate = Date()

        val calendar = Calendar.getInstance()
        calendar.time = currentDate

        calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
        return calendar
    }

    private fun handleTitle(todayPosition: Int, position: Int) {
        val calendar = calculateShift(todayPosition, position)
        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

        val formattedDayOfWeek = dayOfWeek?.substring(0, 1)!!.uppercase() + dayOfWeek.substring(1)

        val title = "$formattedDayOfWeek, $dayOfMonth $monthName"

        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}