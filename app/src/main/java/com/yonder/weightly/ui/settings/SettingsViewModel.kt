package com.yonder.weightly.ui.settings

import androidx.lifecycle.ViewModel
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.uicomponents.MeasureUnit
import com.yonder.weightly.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        fetchPreferences()
    }

    private fun fetchPreferences() {
        val unit = Hawk.get<String>(Constants.Prefs.KEY_GOAL_WEIGHT_UNIT)
        val goalWeight = Hawk.get<Float>(Constants.Prefs.KEY_GOAL_WEIGHT)
        _uiState.update {
            it.copy(unit = unit, goalWeight = goalWeight)
        }
    }

    fun updateUnit(unit : String){
        Hawk.put(Constants.Prefs.KEY_GOAL_WEIGHT_UNIT,MeasureUnit.findValue(unit).value)
    }

    data class UiState(
        var unit: String? = null,
        var goalWeight: Float? = null
    )
}
