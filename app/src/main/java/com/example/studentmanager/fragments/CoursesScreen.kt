package com.example.studentmanager.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentmanager.StudentAdapter
import com.example.studentmanager.data.Student
import com.example.studentmanager.databinding.FragmentCoursesScreenBinding
import com.example.studentmanager.student.StudentDatabase
import com.example.studentmanager.student.StudentRepository
import com.example.studentmanager.viewModel.StudentViewModel
import com.example.studentmanager.viewModel.StudentViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlin.getValue
import androidx.core.view.isVisible
import com.example.studentmanager.R

class CoursesScreen : Fragment() {

    private var _binding: FragmentCoursesScreenBinding? = null
    private val binding get() = _binding!!
    private val studentViewModel: StudentViewModel by activityViewModels {
        val studentDao = StudentDatabase.getDatabase(requireContext()).studentDao()
        val repository = StudentRepository(studentDao)
        StudentViewModelFactory(repository)
    }
    private lateinit var adapter: StudentAdapter
    private var allStudents: List<Student> = emptyList()
    private var selectedCourse: String? = null
    private var selectedSpeciality: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoursesScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeStudents()
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            requireContext(), studentViewModel,
            showDeleteIcon = false,
            showEditIcon = false
        )
        binding.recyclerViewStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStudents.adapter = adapter
    }

    private fun setupMotherChipGroups(students: List<Student>) {
        val uniqueCourses = students.map { it.course }.distinct().sorted()
        val uniqueSpecialities = students.map { it.speciality }.distinct().sorted()

        // Clear previous groups
        binding.motherChipContainer.removeAllViews()

        // Add Courses MotherChipGroup
        addMotherChipGroup("Courses", uniqueCourses) { selectedCourse = it; filterStudents() }

        // Add Specialities MotherChipGroup
        addMotherChipGroup("Speciality", uniqueSpecialities) {
            selectedSpeciality = it; filterStudents()
        }
    }

    private fun addMotherChipGroup(
        title: String,
        items: List<String>,
        onSelect: (String?) -> Unit
    ) {
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Inner ChipGroup
        val chipGroup = ChipGroup(requireContext()).apply {
            isSingleSelection = true
            isSingleLine = false
            visibility = View.GONE // initially collapsed
        }

        // Title with arrow
        val titleView = TextView(requireContext()).apply {
            text = "$title ▼" // ▼ for collapsed, ▲ for expanded
            textSize = 16f
            setTextColor(resources.getColor(R.color.gradient_top))
            setPadding(8, 8, 8, 8)
            setOnClickListener {
                // Toggle visibility of chips
                chipGroup.visibility =
                    if (chipGroup.isVisible) {
                        text = "$title ▼"
                        View.GONE
                    } else {
                        text = "$title ▲"
                        View.VISIBLE
                    }
            }
        }

        items.forEach { item ->
            val chip = Chip(requireContext()).apply {
                text = item
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    onSelect(if (isChecked) item else null)
                }
            }
            chipGroup.addView(chip)
        }

        container.addView(titleView)
        container.addView(chipGroup)
        binding.motherChipContainer.addView(container)
    }

    private fun filterStudents() {
        val filtered = allStudents.filter { s ->
            (selectedCourse == null || s.course == selectedCourse) &&
                    (selectedSpeciality == null || s.speciality == selectedSpeciality)
        }
        adapter.setStudents(filtered)
    }

    private fun observeStudents() {
        studentViewModel.allStudents.observe(viewLifecycleOwner) { list ->
            allStudents = list

            // Update RecyclerView
            adapter.setStudents(list)

            // Dynamically build chips when data changes
            setupMotherChipGroups(list)
        }
    }
}