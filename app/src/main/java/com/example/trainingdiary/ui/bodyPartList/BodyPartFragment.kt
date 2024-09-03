package com.example.trainingdiary.ui.bodyPartList

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainingdiary.R
import com.example.trainingdiary.databinding.FragmentBodyTypeBinding
import com.example.trainingdiary.ui.exerciseList.ExerciseAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BodyPartFragment : Fragment() {

    private var _binding: FragmentBodyTypeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bodyPartViewModel: BodyPartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBodyTypeBinding.inflate(inflater, container, false)
        val root = binding.root

        val bodyPartAdapter = BodyPartAdapter(requireContext()) { bodyTypeId ->
            val bundle = Bundle()
            bundle.putInt("positionId", arguments?.getInt("positionId")!!)
            bundle.putInt("bodyTypeId", bodyTypeId)

            findNavController().navigate(R.id.action_body_part_to_exercises, bundle)
        }

        binding.bodyTypes.layoutManager = LinearLayoutManager(context)
        binding.bodyTypes.adapter = bodyPartAdapter

        bodyPartViewModel = ViewModelProvider(this)[BodyPartViewModel::class.java]

        var exerciseAdapter: ExerciseAdapter? = null;

        CoroutineScope(Dispatchers.Main).launch {
            val records = withContext(Dispatchers.IO) {
                bodyPartViewModel.getBodyTypes()
            }
            bodyPartAdapter.setRecords(records)

            exerciseAdapter = ExerciseAdapter({ exerciseId ->
                val bundle = Bundle()
                bundle.putInt("exerciseId", exerciseId)
                bundle.putInt("positionId", arguments?.getInt("positionId")!!)

                findNavController().navigate(R.id.action_exercises_to_home, bundle)
            }, records, requireContext())


        }

        var runnable: Runnable? = null
        val handler = Handler(Looper.getMainLooper())

        binding.exercisesSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                runnable?.let { handler.removeCallbacks(it) }
                runnable = Runnable {
                    CoroutineScope(Dispatchers.Main).launch {
                        val exercisesRecords = withContext(Dispatchers.IO) {
                            bodyPartViewModel.getExercisesByName(s.toString())
                        }
                        exerciseAdapter?.setRecords(exercisesRecords)
                    }

                    if (s != null && s.length >= 1) {
                        binding.bodyTypes.adapter = exerciseAdapter
                    } else {
                        binding.bodyTypes.adapter = bodyPartAdapter
                    }
                }
                handler.postDelayed(runnable!!, 300)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
