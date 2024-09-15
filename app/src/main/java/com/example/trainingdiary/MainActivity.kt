package com.example.trainingdiary

import NotificationHelper
import NotificationHelper.createPlayer
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.trainingdiary.databinding.ActivityMainBinding
import com.example.trainingdiary.ui.home.CalendarSheet.CalendarSheetFragment
import com.example.trainingdiary.ui.home.CalendarSheet.CalendarSheetListener
import com.example.trainingdiary.ui.home.HistoryHelper
import com.example.trainingdiary.ui.home.HomeViewModel
import com.google.android.material.navigation.NavigationView
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
                showConfirmationDialog(this, "Запустить тренировочный плеер?",
                    "В уведомлениях повится панель управления подходами." +
                            " Вы сможете записывать прогресс не отвлекаясь от ютубчика :)" +
                            "\n \n" +
                            "Просто составте себе план тренировки и запустите плеер.",
                    "Запустить",
                    "Отмена", {

                    createPlayer(this)
                }, {
                    //Nothing
                })

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDateSelected(date: CalendarDay) {
        viewModel.setPosition(date)
    }

    fun showConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
        positiveButtonAction: () -> Unit,
        negativeButtonAction: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton(positiveButtonText) { dialog, _ ->
            positiveButtonAction()
            dialog.dismiss()
        }

        builder.setNegativeButton(negativeButtonText) { dialog, _ ->
            negativeButtonAction()
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}