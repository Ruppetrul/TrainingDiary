package com.example.trainingdiary

import NotificationHelper
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.trainingdiary.databinding.ActivityMainBinding
import com.example.trainingdiary.ui.home.CalendarSheet.CalendarSheetFragment
import com.example.trainingdiary.ui.home.CalendarSheet.CalendarSheetListener
import com.example.trainingdiary.ui.home.HomeViewModel
import com.google.android.material.navigation.NavigationView
import com.prolificinteractive.materialcalendarview.CalendarDay

class MainActivity : AppCompatActivity(), CalendarSheetListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
    }

    private fun openDatePickerBottomSheet() {
        val bottomSheetFragment = CalendarSheetFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_calendar -> {
                openDatePickerBottomSheet()
                true
            }
            R.id.action_start -> {
                //TODO show dialog

                val dayOfEpoch = viewModel.getDayOfEpoch()
                Log.d("TAG", "dayOfEpoch: $dayOfEpoch")

                viewModel.getByPosition(dayOfEpoch.toInt()).observe(this, Observer {
                    val first = it.first()
                    NotificationHelper.createNotification(
                        baseContext,
                        first.exercise.title,
                        1,
                        first.approaches.first().weight,
                        first.approaches.first().repeatCount
                    )
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDateSelected(date: CalendarDay) {
        viewModel.setPosition(date)
    }
}