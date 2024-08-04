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

        val numberInput1 = dialogView.findViewById<EditText>(R.id.numberInput1)
        val decrementButton1 = dialogView.findViewById<Button>(R.id.decrementButton1)
        val incrementButton1 = dialogView.findViewById<Button>(R.id.incrementButton1)

        val numberInput2 = dialogView.findViewById<EditText>(R.id.numberInput2)
        val decrementButton2 = dialogView.findViewById<Button>(R.id.decrementButton2)
        val incrementButton2 = dialogView.findViewById<Button>(R.id.incrementButton2)

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Set")
        builder.setView(dialogView)

        builder.setPositiveButton("Save") { dialog, which ->
            val value1 = numberInput1.text.toString().toIntOrNull() ?: 0
            val value2 = numberInput2.text.toString().toIntOrNull() ?: 0
            onSave(exerciseId, value1, value2)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        setButtonClickListener(decrementButton1, numberInput1, false)
        setButtonClickListener(incrementButton1, numberInput1, true)
        setButtonClickListener(decrementButton2, numberInput2, false)
        setButtonClickListener(incrementButton2, numberInput2, true)
    }

    private fun setButtonClickListener(button: Button, inputField: EditText, increment: Boolean) {
        button.setOnClickListener {
            val value = inputField.text.toString().toIntOrNull() ?: 0
            inputField.setText((value + if (increment) 1 else -1).toString())
        }
    }

    private fun onSave(exerciseId: Int, value1: Int, value2: Int) {
        //TODO
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}