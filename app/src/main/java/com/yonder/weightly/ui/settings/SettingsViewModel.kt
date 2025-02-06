package com.yonder.weightly.ui.settings

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.domain.usecase.DeleteAllWeights
import com.yonder.weightly.domain.usecase.GetGoalWeight
import com.yonder.weightly.network.FirebaseApiService
import com.yonder.weightly.network.LatestVersionResponse
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.coroutines.CoroutineDispatchers
import com.yonder.weightly.utils.enums.ChartType
import com.yonder.weightly.utils.enums.MeasureUnit
import com.yonder.weightly.utils.enums.ThemeType
import com.yonder.weightly.utils.extensions.orZero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    private val getGoalWeight: GetGoalWeight,
    private val deleteAllWeights: DeleteAllWeights,
    private val apiFirebase: FirebaseApiService,
    private val dispatcher: CoroutineDispatchers,
    private val scheduleNotification: ScheduleNotification
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    init {
        fetchPreferences()
        checkIsPremiumUser()
    }

    private fun getNotificationTime(
        hour: Int = Hawk.get(Constants.Notification.KEY_HOUR, 10),
        minute: Int = Hawk.get(Constants.Notification.KEY_MINUTE, 30),
    ): String {
        var stringHour = "$hour"
        var stringMinute = "$minute"
        if (hour < 10) {
            stringHour = "0$hour"
        }
        if (minute < 10) {
            stringMinute = "0$minute"
        }
        return "$stringHour:$stringMinute"
    }

    private fun fetchPreferences() {
        val unit = Hawk.get<String>(Constants.Prefs.KEY_GOAL_WEIGHT_UNIT)
        val chartType = ChartType.findValue(Hawk.get(Constants.Prefs.KEY_CHART_TYPE, 0))
        val themeType = ThemeType.findValue(Hawk.get(Constants.Prefs.THEME_TYPE, "0"))
        val shouldShowLimitLine = Hawk.get(Constants.Prefs.KEY_CHART_LIMIT_LINE, false)
        val notificationTime = getNotificationTime()
        _uiState.update {
            it.copy(
                unit = unit,
                goalWeight = getGoalWeight.invoke(),
                theme = themeType,
                shouldShowLimitLine = shouldShowLimitLine,
                chartType = chartType,
                timePreference = notificationTime,
            )
        }
    }

    fun updateUnit(unit: String) {
        Hawk.put(Constants.Prefs.KEY_GOAL_WEIGHT_UNIT, MeasureUnit.findValue(unit).value)
        _uiState.update {
            it.copy(
                goalWeight = getGoalWeight.invoke(),
            )
        }
    }

    fun updateLimitLine(shouldShowLimitLine: Boolean) {
        Hawk.put(Constants.Prefs.KEY_CHART_LIMIT_LINE, shouldShowLimitLine)
    }

    fun updateGoalWeight(goalWeight: String) {
        if (goalWeight.isNotBlank()) {
            Hawk.put(Constants.Prefs.KEY_GOAL_WEIGHT, goalWeight.toFloatOrNull().orZero())
            _uiState.update {
                it.copy(
                    goalWeight = getGoalWeight.invoke(),
                )
            }
        }
    }

    fun updateNotification(notificationEnabled: Boolean) {
        Hawk.put(Constants.Prefs.KEY_IS_SCHEDULE_NOTIFICATION, notificationEnabled)
        val hour = Hawk.get(Constants.Notification.KEY_HOUR, 10)
        val minute = Hawk.get(Constants.Notification.KEY_MINUTE, 30)
        if (notificationEnabled) {
            triggerEvent(Event.ShowAlarmSetMessage)
            scheduleNotification.setAlarm(hour, minute)
        } else {
            scheduleNotification.removeAlarm()
        }
    }

    fun updateTheme(theme: String) {
        val themeType = ThemeType.findValue(theme)
        when (themeType) {
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
        Hawk.put(Constants.Prefs.THEME_TYPE, themeType.value)
        _uiState.update { it.copy(theme = themeType) }
    }

    fun updateChartType(chartType: String) {
        Hawk.put(Constants.Prefs.KEY_CHART_TYPE, ChartType.findValue(chartType.toInt()).value)
    }

    fun startBilling(activity: Activity) {
        if (uiState.value.shouldShowPremiumPreference) {
            //  billingHelper.launchBillingFlow(activity, Constants.PREMIUM_ACCOUNT)
        }
    }

    fun checkAppUpdates() {
        viewModelScope.launch(dispatcher.io) {
            val latestVersion =
                runCatching {
                    apiFirebase.getLatestRelease().body()
                }.getOrNull() ?: return@launch
            eventChannel.send(Event.ShowVersion(latestVersion))
        }
    }

    fun setNotificationTime(
        hour: Int,
        minute: Int,
    ) {
        Hawk.put(Constants.Notification.KEY_HOUR, hour)
        Hawk.put(Constants.Notification.KEY_MINUTE, minute)
        _uiState.update {
            it.copy(
                timePreference = getNotificationTime(),
            )
        }

        scheduleNotification.setAlarm(hour = hour, minute = minute)
    }

    fun deleteAllData() {
        Hawk.deleteAll()
        viewModelScope.launch(dispatcher.io) {
            deleteAllWeights()
            triggerEvent(Event.NavigateToSplash)
        }
    }


    fun triggerEvent(event: SettingsViewModel.Event) {
        viewModelScope.launch(dispatcher.main) {
            eventChannel.send(event)
        }
    }

    fun deleteAppLock() {
        Hawk.delete(Constants.Prefs.APP_LOCK_HINT)
        Hawk.delete(Constants.Prefs.IS_APP_LOCKED)
        Hawk.delete(Constants.Prefs.APP_LOCK_PASSWORD)
        _uiState.update {
            it.copy(
                isAppLocked = false,
            )
        }
    }

    private fun checkIsPremiumUser() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    shouldShowPremiumPreference = false,
                )
            }
        }
    }

    data class UiState(
        var unit: String? = null,
        var goalWeight: String = "",
        var chartType: ChartType = ChartType.LINE,
        var theme: ThemeType = ThemeType.DEFAULT,
        var shouldShowLimitLine: Boolean = false,
        var isAppLocked: Boolean =
            Hawk.get(
                Constants.Prefs.IS_APP_LOCKED,
                false,
            ),
        var shouldShowPremiumPreference: Boolean = false,
        var notificationEnabled: Boolean =
            Hawk.get(
                Constants.Prefs.KEY_IS_SCHEDULE_NOTIFICATION,
                false,
            ),
        var timePreference: String = "",
    )

    sealed class Event {
        data object NavigateToSplash : Event()
        data object ShowAlarmSetMessage : Event()
        data class ShowVersion(
            val latest: LatestVersionResponse,
        ) : Event()
    }
}
