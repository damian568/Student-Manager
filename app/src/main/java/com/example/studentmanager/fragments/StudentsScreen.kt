package com.example.studentmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentmanager.StudentAdapter
import com.example.studentmanager.databinding.FragmentStudentsScreenBinding
import com.example.studentmanager.dialogs.AddStudentDialog
import com.example.studentmanager.student.StudentDatabase
import com.example.studentmanager.student.StudentRepository
import com.example.studentmanager.viewModel.StudentViewModel
import com.example.studentmanager.viewModel.StudentViewModelFactory

class StudentsScreen : Fragment() {

    private var _binding: FragmentStudentsScreenBinding? = null
    private val binding get() = _binding!!
    private val studentViewModel: StudentViewModel by activityViewModels {
        val studentDao = StudentDatabase.getDatabase(requireContext()).studentDao()
        val repository = StudentRepository(studentDao)
        StudentViewModelFactory(repository)
    }
    private lateinit var adapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentsScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(requireContext(), studentViewModel)
        binding.recyclerViewStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStudents.adapter = adapter
    }

    private fun setupFab() {
        binding.idFABAdd.setOnClickListener {
            AddStudentDialog().show(parentFragmentManager, "AddStudentDialog")
        }
    }

    private fun observeData() {
        studentViewModel.allStudents.observe(viewLifecycleOwner) { list ->
            adapter.setStudents(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}