package com.example.trainingdiary.ui.home

import ExerciseHistoryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainingdiary.AppDatabase
import com.example.trainingdiary.R
import com.example.trainingdiary.databinding.FragmentHomeBinding
import com.example.trainingdiary.models.ExerciseHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

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
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_home, false)
            .build()

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_currentFragment_to_listFragment, null, navOptions)
        }

        val itemId = arguments?.getInt("exerciseId")

        if (itemId != null && itemId != 0) {
            val database = AppDatabase.getDatabase(requireContext())
            CoroutineScope(Dispatchers.IO).launch {
                database.exerciseDao().insertHistory(ExerciseHistory(0, itemId, Date())).let {
                    arguments?.clear()
                }
            }
        }

        val exerciseHistoryAdapter = ExerciseHistoryAdapter()

        binding.exercisesHistory.apply {
            adapter = exerciseHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        homeViewModel.allExerciseHistory.observe(viewLifecycleOwner) { exerciseHistory ->
            exerciseHistoryAdapter.submitList(exerciseHistory)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}