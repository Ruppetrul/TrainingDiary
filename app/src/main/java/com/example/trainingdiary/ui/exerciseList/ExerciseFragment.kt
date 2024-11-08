package com.example.trainingdiary.ui.exerciseList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainingdiary.R
import com.example.trainingdiary.databinding.FragmentExerciseBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExerciseFragment : Fragment() {

    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!
    private lateinit var exerciseViewModel: ExerciseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseBinding.inflate(inflater, container, false)
        val root = binding.root

        exerciseViewModel = ViewModelProvider(this)[ExerciseViewModel::class.java]

        CoroutineScope(Dispatchers.Main).launch {
            val adapter = ExerciseAdapter({ exerciseId ->
                val bundle = Bundle()
                bundle.putInt("exerciseId", exerciseId)
                bundle.putInt("positionId", arguments?.getInt("positionId")!!)

                findNavController().navigate(R.id.action_exercises_to_home, bundle)
            }, requireContext())

            binding.exercises.layoutManager = LinearLayoutManager(context)
            binding.exercises.adapter = adapter

            val exercisesBodyPartRecords = withContext(Dispatchers.IO) {
                exerciseViewModel.getExercisesByBodyPart(arguments?.getInt("bodyTypeId", 0)!!)
            }
            adapter.setRecords(exercisesBodyPartRecords)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
