package com.yonder.weightly.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonder.weightly.data.repository.WeightRepository
import com.yonder.weightly.domain.uimodel.WeightUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private var weightRepository: WeightRepository) :
    ViewModel() {


    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        getWeightHistories()
    }

    private fun getWeightHistories() = viewModelScope.launch(Dispatchers.IO) {
        weightRepository.getAllWeights().collectLatest { weightHistories ->
            _uiState.update {
                it.copy(
                    histories = weightHistories.reversed()
                )
            }
        }
    }

    data class UiState(
        var histories: List<WeightUIModel?> = emptyList(),
    )
}