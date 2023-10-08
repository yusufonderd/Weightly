package com.yonder.weightly.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.domain.usecase.GetAllWeights
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private var getAllWeights: GetAllWeights) :
    ViewModel() {

    var job: Job? = null

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun getWeightHistories() {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            getAllWeights().collectLatest { weightHistories ->
                _uiState.update {
                    it.copy(
                        histories = weightHistories.reversed(),
                        shouldShowEmptyView = weightHistories.isEmpty()
                    )
                }
            }
        }
    }

    fun cancelJobs() {
        job?.cancel()
    }

    data class UiState(
        var histories: List<WeightUIModel?> = emptyList(),
        var shouldShowEmptyView: Boolean = false,
    )
}