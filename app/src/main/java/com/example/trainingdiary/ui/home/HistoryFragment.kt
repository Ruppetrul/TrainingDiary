package com.example.trainingdiary.ui.home

import ExerciseHistoryAdapter
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainingdiary.AppDatabase
import com.example.trainingdiary.R
import com.example.trainingdiary.databinding.FragmentHomeHistoryBinding
import com.example.trainingdiary.models.Approach
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
        val exerciseHistoryAdapter = ExerciseHistoryAdapter(
            exerciseDeleteListener = { exerciseId ->
                CoroutineScope(Dispatchers.IO).launch {
                    database.exerciseDao().deleteHistoryById(exerciseId)
                }
            },
            approachAddListener = { exerciseId ->
                showDialog(requireContext(), exerciseId)
            }
        )

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

    private fun showDialog(context: Context, exerciseId: Int) {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.add_aproach, null)

        val weight = dialogView.findViewById<EditText>(R.id.weight)
        val decrementButton1 = dialogView.findViewById<Button>(R.id.weightDecrementButton)
        val incrementButton1 = dialogView.findViewById<Button>(R.id.weightIncrementButton)

        val repeat = dialogView.findViewById<EditText>(R.id.repeatNumberInput)
        val decrementButton2 = dialogView.findViewById<Button>(R.id.repeatDecrementButton)
        val incrementButton2 = dialogView.findViewById<Button>(R.id.repeatIncrementButton)

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Set")
        builder.setView(dialogView)

        builder.setPositiveButton("Save") { dialog, which ->
            val weight = weight.text.toString().toIntOrNull() ?: 0 //TODO to float
            val repeat = repeat.text.toString().toIntOrNull() ?: 0
            onSave(exerciseId, weight.toFloat(), repeat)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        setButtonClickListener(decrementButton1, weight, false)
        setButtonClickListener(incrementButton1, weight, true)
        setButtonClickListener(decrementButton2, repeat, false)
        setButtonClickListener(incrementButton2, repeat, true)
    }

    private fun setButtonClickListener(button: Button, inputField: EditText, increment: Boolean) {
        button.setOnClickListener {
            val value = inputField.text.toString().toIntOrNull() ?: 0
            inputField.setText((value + if (increment) 1 else -1).toString())
        }
    }

    private fun onSave(exerciseId: Int, weight: Float, repeat: Int) {
        val database = AppDatabase.getDatabase(requireContext())

        val approach = Approach(0, exerciseId, repeat, weight)
        CoroutineScope(Dispatchers.IO).launch {
            database.exerciseDao().insertApproach(approach)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}