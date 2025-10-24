package com.example.studentmanager

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.studentmanager.databinding.MainActivityBinding
import com.example.studentmanager.fragments.GradesScreen
import com.example.studentmanager.fragments.CoursesScreen
import com.example.studentmanager.fragments.StudentsScreen
import com.example.studentmanager.fragments.profile.LoginScreen
import com.example.studentmanager.fragments.profile.ProfileScreen
import com.example.studentmanager.fragments.profile.RegistrationScreen
import com.example.studentmanager.user.UserDatabase
import com.example.studentmanager.user.UserRepository
import com.example.studentmanager.viewModel.UserViewModel
import com.example.studentmanager.viewModel.UserViewModelFactory
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class MainActivity : AppCompatActivity() {

    private lateinit var db: UserDatabase

    lateinit var binding: MainActivityBinding
    private lateinit var navController: NavController
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        // ✅ initialize database safely inside onCreate
        db = UserDatabase.getDatabase(applicationContext)
        val userDao = db.userDao()
        val repository = UserRepository(userDao)
        val factory = UserViewModelFactory(repository)
        userViewModel = viewModels<UserViewModel> { factory }.value

        // ✅ navigation setup
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        setupActionBarWithNavController(navController)

        observeLoginState()
    }

    private fun observeLoginState() {
        userViewModel.currentUser.observe(this) { user ->
            if (user != null && userViewModel.getIsLoginFlow()) {
                navController.navigate(R.id.studentsScreen)
                userViewModel.clearLoginFlow()
            } else if (user == null) {
                navController.navigate(R.id.loginScreen)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun bottomNavigation() {
        binding.bottomNavigation.add(
            CurvedBottomNavigation.Model(1, "Courses", R.drawable.ic_courses)
        )
        binding.bottomNavigation.add(
            CurvedBottomNavigation.Model(2, "Students", R.drawable.ic_article_person)
        )
        binding.bottomNavigation.add(
            CurvedBottomNavigation.Model(3, "Grades", R.drawable.ic_grades)
        )
        binding.bottomNavigation.add(
            CurvedBottomNavigation.Model(4, "Profile", R.drawable.ic_account)
        )
        binding.bottomNavigation.add(
            CurvedBottomNavigation.Model(5, "Logout", R.drawable.ic_logout)
        )

        binding.bottomNavigation.setOnClickMenuListener {
            when (it.id) {
                1 -> replaceFragment(CoursesScreen())
                2 -> replaceFragment(StudentsScreen())
                3 -> replaceFragment(GradesScreen())
                4 -> replaceFragment(ProfileScreen())
                5 -> replaceFragment(LoginScreen())
            }
        }

        //default fragment
        replaceFragment(StudentsScreen())
        binding.bottomNavigation.show(2)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navHostFragment, fragment)
            .commit()
    }
}