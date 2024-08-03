package com.example.trainingdiary.ui.home

import ExerciseHistoryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainingdiary.AppDatabase
import com.example.trainingdiary.databinding.FragmentHomeHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    companion object {
        private const val ARG_DATE = "date"

        fun newInstance(dayPosition: Int): HistoryFragment {
            val fragment = HistoryFragment()
            val args = Bundle().apply {
                putInt(ARG_DATE, dayPosition)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private var timestamp : Int = 0

    private var _binding: FragmentHomeHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timestamp = requireArguments().getInt(ARG_DATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val database = AppDatabase.getDatabase(requireContext())
        val exerciseHistoryAdapter = ExerciseHistoryAdapter  { exerciseHistory ->
            CoroutineScope(Dispatchers.IO).launch {
                database.exerciseDao().deleteHistoryById(exerciseHistory)
            }
        }

        binding.exercisesHistory.apply {
            adapter = exerciseHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        homeViewModel.getByTimestamp(timestamp).observe(viewLifecycleOwner) { exerciseHistory ->
            if (exerciseHistory.isEmpty()) {
                binding.exercisesHistory.visibility = View.GONE
                binding.emptyDay.visibility = View.VISIBLE
            } else {
                binding.exercisesHistory.visibility = View.VISIBLE
                binding.emptyDay.visibility = View.GONE
                exerciseHistoryAdapter.submitList(exerciseHistory)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}