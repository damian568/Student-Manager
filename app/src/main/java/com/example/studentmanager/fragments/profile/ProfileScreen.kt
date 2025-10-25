package com.example.studentmanager.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.studentmanager.data.User
import com.example.studentmanager.databinding.FragmentProfileScreenBinding
import com.example.studentmanager.dialogs.EditUserDialog
import com.example.studentmanager.user.UserDatabase
import com.example.studentmanager.user.UserRepository
import com.example.studentmanager.viewModel.UserViewModel
import com.example.studentmanager.viewModel.UserViewModelFactory
import kotlin.getValue

class ProfileScreen : Fragment() {

    private var _binding: FragmentProfileScreenBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels {
        val userDao = UserDatabase.getDatabase(requireContext()).userDao()
        val repository = UserRepository(userDao)
        UserViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUser()
        editProfileUI()
    }

    private fun observeUser(){
        // Observe the currently logged-in user
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                // Observe live updates from database for this user
                userViewModel.observeUser(it.uid).observe(viewLifecycleOwner) { updatedUser ->
                    updatedUser?.let { displayProfileUI(it) }
                }
            }
        }
    }

    private fun displayProfileUI(user: User){
        binding.apply {
            profileName.text = user.username
            profileEmail.text = user.email
            profileGender.text = user.gender.name
        }
    }

    private fun editProfileUI(){
        binding.profileEditIcon.setOnClickListener {
            userViewModel.currentUser.value?.let {
                EditUserDialog(it).show(childFragmentManager, "EditUserDialog")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}