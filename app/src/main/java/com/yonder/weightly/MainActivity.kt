package com.yonder.weightly

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.codemybrainsout.ratingdialog.RatingDialog
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.databinding.ActivityMainBinding
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.enums.ThemeType
import dagger.hilt.android.AndroidEntryPoint

const val channelID = "channel1"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme()
        setupEdgeToEdgeInsets()
        setupBackNavigation()
        createNotificationChannel()
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_calendar,
                R.id.navigation_settings,
                R.id.navigation_history
            )
        )
        setSupportActionBar(binding.toolbar)
        val noToolbarNavigationIds =
            listOf(R.id.navigation_splash, R.id.navigation_on_boarding, R.id.navigation_open_lock)
        val noBottomNavigationIds =
            listOf(
                R.id.navigation_splash,
                R.id.navigation_on_boarding,
                R.id.navigation_add_weight,
                R.id.navigation_set_lock,
                R.id.navigation_open_lock
            )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val shouldShowBottomNavigation = noBottomNavigationIds.contains(destination.id).not()
            val shouldShowToolbar = noToolbarNavigationIds.contains(destination.id).not()
            binding.navView.isVisible = shouldShowBottomNavigation
            if (shouldShowToolbar) {
                supportActionBar?.show()
            } else {
                supportActionBar?.hide()
            }
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        showRateDialog()
    }

    private fun setupEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = systemBars.top)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.navView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom)
            insets
        }
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.navView.selectedItemId == R.id.navigation_home) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                } else {
                    binding.navView.selectedItemId = R.id.navigation_home
                }
            }
        })
    }

    private fun showRateDialog() {
        val ratingDialog: RatingDialog = RatingDialog.Builder(this)
            .threshold(4)
            .session(3)
            .title(text = R.string.rating_dialog_experience, textColor = R.color.black)
            .formSubmitText(R.string.rating_dialog_submit)
            .positiveButton(text = R.string.rating_dialog_maybe_later)
            .negativeButton(text = R.string.rating_dialog_never)
            .formSubmitText(R.string.rating_dialog_submit)
            .formHint(R.string.rating_dialog_suggestions)
            .formTitle(R.string.submit_feedback)
            .onRatingBarFormSubmit(viewModel::addFeedback)
            .build()
        ratingDialog.show()
    }

    private fun setTheme() {
        when (ThemeType.findValue(Hawk.get(Constants.Prefs.THEME_TYPE, "0"))) {
            ThemeType.DEFAULT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            ThemeType.DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val name = getString(R.string.app_notification_channel_title)
        val desc = getString(R.string.app_notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            channelID,
            name,
            importance
        )
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}