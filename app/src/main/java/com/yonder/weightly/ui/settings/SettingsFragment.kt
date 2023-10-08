package com.yonder.weightly.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.BuildConfig
import com.yonder.weightly.R
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.NotificationReceiver
import com.yonder.weightly.utils.enums.MeasureUnit
import com.yonder.weightly.utils.extensions.EMPTY
import com.yonder.weightly.utils.notificationID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {


    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preference, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observe()
    }

    private fun observe() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect(::setUIState)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    SettingsViewModel.Event.NavigateToSplash -> {
                        findNavController().navigate(SettingsFragmentDirections.actionNavigateSplash())
                    }
                }
            }
        }
    }

    private fun setUIState(uiState: SettingsViewModel.UiState) {
        val setLockPreference = findPreference<Preference>("set_lock")
        val notificationTimePreference = findPreference<Preference>("notification_time")
        val premiumPreference = findPreference<Preference>("premium")
        val goalWeightPreference = findPreference<Preference>("goal_weight")
        val unitPreferences = findPreference<ListPreference>("unit")
        val themePreference = findPreference<ListPreference>("theme")
        val chartTypePreference = findPreference<ListPreference>("chart_type")
        val limitLinePreference = findPreference<CheckBoxPreference>("show_limit_lines")
        val notificationPreference = findPreference<CheckBoxPreference>("notification")
        notificationPreference?.isChecked = uiState.notificationEnabled
        limitLinePreference?.isChecked = uiState.shouldShowLimitLine
        unitPreferences?.value = MeasureUnit.findValue(uiState.unit).value
        goalWeightPreference?.summary = uiState.goalWeight
        chartTypePreference?.value = "${uiState.chartType.value}"
        notificationTimePreference?.summary = uiState.timePreference
        if (uiState.shouldShowPremiumPreference) {
            premiumPreference?.title = getString(R.string.be_premium)
            premiumPreference?.summary = getString(R.string.be_premium_description)
        } else {
            premiumPreference?.title = getString(R.string.premium_user)
            premiumPreference?.summary = String.EMPTY
        }
        themePreference?.value = uiState.theme.value
        if (uiState.isAppLocked){
            setLockPreference?.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_lock_open_24)
            setLockPreference?.title = getString(R.string.unlock)
        }else{
            setLockPreference?.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_lock_24)
            setLockPreference?.title = getString(R.string.lock)
        }
    }

    private fun initViews() {


      /*  findPreference<Preference>("notification_time")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val picker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(
                        Hawk.get(
                            Constants.Notification.KEY_HOUR,
                            Constants.Defaults.NOTIFICATION_HOUR
                        )
                    )
                    .setMinute(
                        Hawk.get(
                            Constants.Notification.KEY_MINUTE,
                            Constants.Defaults.NOTIFICATION_MINUTE
                        )
                    )
                    .setTitleText(R.string.select_notification_time)
                    .build()
                picker.addOnPositiveButtonClickListener {
                    viewModel.setNotificationTime(hour = picker.hour, minute = picker.minute)
                    scheduleNotification()
                }
                picker.show(parentFragmentManager, TAG_TIME_PICKER)
                true
            }*/

        findPreference<Preference>("set_lock")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                if (Hawk.get(Constants.Prefs.IS_APP_LOCKED,false)){

                    val alertBuilder = MaterialAlertDialogBuilder(requireContext())
                    alertBuilder.setTitle(R.string.remove_app_lock_dialog_title)
                    alertBuilder.setNegativeButton(R.string.no){ _,_->

                    }
                    alertBuilder.setPositiveButton(R.string.yes){_,_ ->
                        viewModel.deleteAppLock()
                    }
                    alertBuilder.show()

                }else{
                    findNavController().navigate(SettingsFragmentDirections.actionNavigateToSetLock())
                }
                true
            }

            findPreference<Preference>("goal_weight")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val alert = MaterialAlertDialogBuilder(requireContext())
                val editText = EditText(requireContext())
                val editTextParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                val textInputLayout = TextInputLayout(requireContext())
                val textInputLayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                textInputLayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE

                textInputLayout.layoutParams = textInputLayoutParams
                textInputLayout.addView(editText, editTextParams)
                textInputLayout.hint = getString(R.string.enter_goal_weight)

                alert.setMessage(R.string.goal_weight)
                alert.setView(textInputLayout)
                alert.setPositiveButton(getString(R.string.ok)) { _, _ ->
                    viewModel.updateGoalWeight(editText.text.toString())
                }
                alert.setNegativeButton(getString(R.string.cancel)) { _, _ ->
                }
                alert.show()
                true
            }


        findPreference<Preference>("reset_all_data")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val alertBuilder = MaterialAlertDialogBuilder(requireContext())
                alertBuilder.setTitle(R.string.reset_all_data_title)
                alertBuilder.setNegativeButton(R.string.no){ _,_->

                }
                alertBuilder.setPositiveButton(R.string.yes){_,_ ->
                    viewModel.deleteAllData()
                }
                alertBuilder.show()
                true
            }
        findPreference<Preference>("share_with_friends")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                ShareCompat.IntentBuilder.from(requireActivity())
                    .setType("text/plain")
                    .setChooserTitle(getString(R.string.share_with_friends))
                    .setText("http://play.google.com/store/apps/details?id=" + activity?.packageName)
                    .startChooser();
                true
            }

        findPreference<Preference>("developer")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                openUrl("https://twitter.com/iamyusufonder")
                true
            }
        findPreference<Preference>("premium")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                viewModel.startBilling(requireActivity())
                true
            }
        findPreference<Preference>("source_code")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                openUrl("https://github.com/yusufonderd/Weightly")
                true
            }
        findPreference<Preference>("send_feedback")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                openUrl("https://forms.gle/kNxxSE4SS1xy2qRt7")
                true
            }
        findPreference<Preference>("rate_us")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                    )
                )
                true
            }
        findPreference<ListPreference>("unit")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                viewModel.updateUnit(newValue)
            }
            true
        }
        findPreference<ListPreference>("chart_type")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                viewModel.updateChartType(newValue)
            }
            true
        }
        findPreference<CheckBoxPreference>("show_limit_lines")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Boolean) {
                viewModel.updateLimitLine(newValue)
            }
            true
        }
     /*   findPreference<CheckBoxPreference>("notification")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Boolean) {
                viewModel.updateNotification(
                    notificationEnabled = newValue
                )
                if (newValue) {
                    Toast.makeText(
                        requireContext(),
                        R.string.notification_activated,
                        Toast.LENGTH_SHORT
                    ).show()
                    scheduleNotification()
                } else {
                    cancelNotification()
                }
            }
            true
        }*/

        findPreference<ListPreference>("theme")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                viewModel.updateTheme(newValue)
            }
            true
        }
    }

    private fun openUrl(url: String) {
        val viewIntent = Intent(
            "android.intent.action.VIEW",
            Uri.parse(url)
        )
        startActivity(viewIntent)
    }

    private fun cancelNotification() {
        Toast.makeText(requireContext(), R.string.notification_closed, Toast.LENGTH_SHORT).show()
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun scheduleNotification() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val hour = Hawk.get(Constants.Notification.KEY_HOUR, 10)
        val minute = Hawk.get(Constants.Notification.KEY_MINUTE, 30)
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        if (now.after(calendar)) {
            calendar.add(Calendar.DATE, 1);
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Hawk.put(Constants.Prefs.KEY_IS_SCHEDULE_NOTIFICATION, true)
    }

}