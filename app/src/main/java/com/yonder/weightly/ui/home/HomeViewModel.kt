package com.yonder.weightly.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonder.weightly.data.local.AppDatabase
import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.data.repository.WeightRepository
import com.yonder.weightly.domain.mapper.WeightEntityMapper
import com.yonder.weightly.domain.uimodel.WeightUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private var weightRepository: WeightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        getAllWeightHistory()
    }

    private fun getAllWeightHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(histories = weightRepository())
            }
        }
    }

    data class UiState(
        var histories: List<WeightUIModel> = emptyList()
    )

}