package com.example.trainingdiary.ui.bodyPartList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trainingdiary.R
import com.example.trainingdiary.databinding.FragmentBodyTypeBinding
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

        val adapter = BodyPartAdapter(requireContext()) { bodyTypeId ->
            val bundle = Bundle()
            bundle.putInt("positionId", arguments?.getInt("positionId")!!)
            bundle.putInt("bodyTypeId", bodyTypeId)

            findNavController().navigate(R.id.action_body_part_to_exercises, bundle)
        }
        binding.bodyTypes.layoutManager = LinearLayoutManager(context)
        binding.bodyTypes.adapter = adapter

        bodyPartViewModel = ViewModelProvider(this)[BodyPartViewModel::class.java]

        CoroutineScope(Dispatchers.Main).launch {
            val records = withContext(Dispatchers.IO) {
                bodyPartViewModel.getBodyTypes()
            }
            adapter.setRecords(records)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
