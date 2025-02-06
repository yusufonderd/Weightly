package com.yonder.weightly.ui.settings

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
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.BuildConfig
import com.yonder.weightly.R
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.enums.MeasureUnit
import com.yonder.weightly.utils.extensions.EMPTY
import com.yonder.weightly.utils.extensions.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

const val TAG_TIME_PICKER = "TAG_TIME_PICKER_SETTINGS"

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        setPreferencesFromResource(R.xml.root_preference, rootKey)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
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
                    SettingsViewModel.Event.ShowAlarmSetMessage -> {
                        Toast.makeText(
                            requireContext(),
                            R.string.notification_activated,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    SettingsViewModel.Event.NavigateToSplash -> {
                        safeNavigate(SettingsFragmentDirections.actionNavigateSplash())
                    }

                    is SettingsViewModel.Event.ShowVersion -> {
                        Toast
                            .makeText(
                                requireContext(),
                                "Last version: ${event.latest.displayVersion}",
                                Toast.LENGTH_SHORT,
                            ).show()
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
        if (uiState.isAppLocked) {
            setLockPreference?.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_lock_open_24)
            setLockPreference?.title = getString(R.string.unlock)
        } else {
            setLockPreference?.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_lock_24)
            setLockPreference?.title = getString(R.string.lock)
        }
    }

    private fun initViews() {
        findPreference<Preference>("notification_time")?.onPreferenceClickListener =
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
                }
                picker.show(parentFragmentManager, TAG_TIME_PICKER)
                true
            }

        findPreference<Preference>("set_lock")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                if (Hawk.get(Constants.Prefs.IS_APP_LOCKED, false)) {
                    val alertBuilder = MaterialAlertDialogBuilder(requireContext())
                    alertBuilder.setTitle(R.string.remove_app_lock_dialog_title)
                    alertBuilder.setNegativeButton(R.string.no) { _, _ ->
                    }
                    alertBuilder.setPositiveButton(R.string.yes) { _, _ ->
                        viewModel.deleteAppLock()
                    }
                    alertBuilder.show()
                } else {
                    safeNavigate(SettingsFragmentDirections.actionNavigateToSetLock())
                }
                true
            }

        findPreference<Preference>("goal_weight")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val alert = MaterialAlertDialogBuilder(requireContext())
                val editText = EditText(requireContext())
                val editTextParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                    )
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                val textInputLayout = TextInputLayout(requireContext())
                val textInputLayoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
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
                alertBuilder.setNegativeButton(R.string.no) { _, _ ->
                }
                alertBuilder.setPositiveButton(R.string.yes) { _, _ ->
                    viewModel.deleteAllData()
                }
                alertBuilder.show()
                true
            }
        findPreference<Preference>("share_with_friends")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                ShareCompat.IntentBuilder
                    .from(requireActivity())
                    .setType("text/plain")
                    .setChooserTitle(getString(R.string.share_with_friends))
                    .setText("http://play.google.com/store/apps/details?id=" + activity?.packageName)
                    .startChooser()
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

        findPreference<Preference>("check_app_updates")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                viewModel.checkAppUpdates()
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
                        Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}"),
                    ),
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
        findPreference<CheckBoxPreference>("notification")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Boolean) {
                viewModel.updateNotification(notificationEnabled = newValue)
            }
            true
        }

        findPreference<ListPreference>("theme")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is String) {
                viewModel.updateTheme(newValue)
            }
            true
        }
    }

    private fun openUrl(url: String) {
        val viewIntent =
            Intent(
                "android.intent.action.VIEW",
                Uri.parse(url),
            )
        startActivity(viewIntent)
    }


}
