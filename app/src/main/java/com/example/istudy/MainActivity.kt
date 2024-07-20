package com.example.istudy

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.istudy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var binding: ActivityMainBinding
    private lateinit var studyFragment: StudyFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)  // Initialize DBHelper

        // FRAGMENT INITIAL SETUP
        if (savedInstanceState == null) {
            studyFragment = StudyFragment()
            replaceFragment(studyFragment)
        }

        // ====================TOOLBAR SET-UP ============================================
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.deleteIcon.setOnClickListener {
            showToastMessage()
        }

        binding.uploadButton.setOnClickListener {
            val dialog = UploadPdfDialogFragment()
            dialog.show(supportFragmentManager, "UploadPdfDialogFragment")
        }
        // ====================END OF TOOLBAR ============================================

        // ====================BOTTOM NAV BAR SET-UP ============================================
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    binding.uploadButton.visibility = View.VISIBLE
                    replaceFragment(StudyFragment())
                    true
                }
                R.id.navigation_profile -> {
                    binding.uploadButton.visibility = View.GONE
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
        // ========================END OF BOTTOM NAV BAR =====================================

    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()  // Close the DBHelper when the activity is destroyed
    }

    // FRAGMENT REPLACE FUNCTION
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    fun refreshStudyFragmentData() {
        studyFragment.refreshData()
    }

    private fun showToastMessage() {
        val message = "Long press the topic to delete."
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
