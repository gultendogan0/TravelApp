package com.example.travelapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.travelapp.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private var isAddFragmentVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        setSupportActionBar(binding.appBarHome.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )


        val slideShowMenuItem = navView.menu.findItem(R.id.nav_slideshow)
        slideShowMenuItem.setOnMenuItemClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Log Out")
            alertDialogBuilder.setMessage("Do you want to sign out?")
            alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
                auth.signOut()
                startActivity(Intent(this,MainActivity::class.java))
                Toast.makeText(this, "Checked out", Toast.LENGTH_SHORT).show()
                finish()
                dialog.dismiss()
            }
            alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                Toast.makeText(this, "Exit canceled", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            true
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            isAddFragmentVisible = destination.id == R.id.addFragment
            updateFabVisibility()
        }

        binding.appBarHome.fab.setOnClickListener { view ->
            navController.navigate(R.id.action_nav_home_to_addFragment)
        }

        val headerView = navView.getHeaderView(0)
        val textView = headerView.findViewById<TextView>(R.id.nav_email)
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email
        textView.text = userEmail
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun updateFabVisibility() {
        if (isAddFragmentVisible) {
            binding.appBarHome.fab.hide()
        } else {
            binding.appBarHome.fab.show()
        }
    }
}