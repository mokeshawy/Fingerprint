package com.example.fingerprint.activitis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.fingerprint.R
import com.example.fingerprint.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding                : ActivityMainBinding
    lateinit var navHostFragment        : NavHostFragment
    lateinit var navController          : NavController
    lateinit var appBarConfiguration    : AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // operation work of fragment.
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController   = navHostFragment.navController

        // work of action bar.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.splashFragment,R.id.fingerPrintFragment,R.id.homeFragment))
        setupActionBarWithNavController(navController,appBarConfiguration)

        // work of show and hide action bar.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id){
                R.id.splashFragment         -> supportActionBar!!.hide()
                R.id.fingerPrintFragment    -> supportActionBar!!.hide()

                else -> supportActionBar!!.show()
            }
        }
    }
}