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

        val exerciseHistoryAdapter = ExerciseHistoryAdapter(
            exerciseDeleteListener = { exerciseId ->
                showDeleteConfirmationDialog(requireContext(), exerciseId)
            },
            approachAddListener = { exerciseId, approachId, weight, repeat ->
                showDialog(requireContext(), exerciseId, approachId, weight, repeat)
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

    private fun showDialog(context: Context, exerciseId: Int, approachId: Int? = null, weight: Float? = null, repeat: Int? = null) {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.add_aproach, null)

        val weightView = dialogView.findViewById<EditText>(R.id.weight)

        if (weight != null) {
            weightView.setText(weight.toString())
        }

        val decrementButton1 = dialogView.findViewById<Button>(R.id.weightDecrementButton)
        val incrementButton1 = dialogView.findViewById<Button>(R.id.weightIncrementButton)

        val repeatView = dialogView.findViewById<EditText>(R.id.repeatNumberInput)
        if (weight != null) {
            repeatView.setText(repeat.toString())
        }
        val decrementButton2 = dialogView.findViewById<Button>(R.id.repeatDecrementButton)
        val incrementButton2 = dialogView.findViewById<Button>(R.id.repeatIncrementButton)

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Set")
        builder.setView(dialogView)

        builder.setPositiveButton(context.getString(R.string.save)) { dialog, which ->
            onSave(
                exerciseId,
                weightView.text.toString().toFloat(),
                repeatView.text.toString().toIntOrNull() ?: 0,
                approachId
            )
            dialog.dismiss()
        }

        builder.setNegativeButton(context.getString(R.string.сonfirm_deletion_negative)) { dialog, which ->
            dialog.dismiss()
        }

        if (approachId != null) {
            builder.setNeutralButton(context.getString(R.string.сonfirm_deletion_positive)) { dialog, which ->
                onDelete(approachId)
                dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.show()

        setButtonClickListener(decrementButton1, weightView, false, true)
        setButtonClickListener(incrementButton1, weightView, true, true)
        setButtonClickListener(decrementButton2, repeatView, false, false)
        setButtonClickListener(incrementButton2, repeatView, true, false)
    }

    private fun setButtonClickListener(button: Button, inputField: EditText, increment: Boolean, isFloat: Boolean) {
        button.setOnClickListener {
            val newValue = if (isFloat) {
                updateFloatValue(inputField, increment)
            } else {
                updateIntValue(inputField, increment)
            }
            inputField.setText(newValue)
        }
    }

    private fun updateFloatValue(inputField: EditText, increment: Boolean): String {
        val floatValue = inputField.text.toString().toFloatOrNull() ?: 0f
        val updatedValue = floatValue + if (increment) 1f else -1f
        return updatedValue.toString()
    }

    private fun updateIntValue(inputField: EditText, increment: Boolean): String {
        val intValue = inputField.text.toString().toIntOrNull() ?: 0
        val updatedValue = intValue + if (increment) 1 else -1
        return updatedValue.toString()
    }

    private fun onDelete(approachId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(requireContext())
            database.exerciseDao().deleteApproachById(approachId)
        }
    }

    private fun onSave(exerciseId: Int, weight: Float, repeat: Int, approachId: Int? = null) {
        val database = AppDatabase.getDatabase(requireContext())

        CoroutineScope(Dispatchers.IO).launch {
            val approach = Approach(
                id = approachId ?: 0,
                exerciseHistoryId = exerciseId,
                repeatCount = repeat,
                weight = weight
            )

            if (approachId != null && approachId != 0) {
                database.exerciseDao().updateApproach(approach)
            } else {
                database.exerciseDao().insertApproach(approach)
            }
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, exerciseId: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.confirm_deletion) )
        builder.setMessage(context.getString(R.string.confirm_deletion_text))

        builder.setPositiveButton(context.getString(R.string.сonfirm_deletion_positive)) { dialog, _ ->
            val database = AppDatabase.getDatabase(requireContext())

            CoroutineScope(Dispatchers.IO).launch {
                database.exerciseDao().deleteHistoryById(exerciseId)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton(context.getString(R.string.сonfirm_deletion_negative)) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}