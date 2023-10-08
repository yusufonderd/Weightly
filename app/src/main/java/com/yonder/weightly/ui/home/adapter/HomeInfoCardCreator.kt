package com.yonder.weightly.ui.home.adapter

import com.yonder.weightly.R
import com.yonder.weightly.ui.home.HomeViewModel
import com.yonder.weightly.uicomponents.InfoCardUIModel

object HomeInfoCardCreator {

    fun create(uiState: HomeViewModel.UiState): List<InfoCardUIModel> {
        return listOf(
            InfoCardUIModel(
                title = uiState.currentWeight,
                description = R.string.current,
                titleTextColor = R.color.purple_500
            ),
            InfoCardUIModel(
                title = uiState.goalWeight,
                description = R.string.goal
            ),
            InfoCardUIModel(
                title = uiState.startWeight,
                description = R.string.start
            ),
            InfoCardUIModel(
                title = uiState.averageWeight,
                description = R.string.title_average_weight,
                titleTextColor = R.color.orange
            ),
            InfoCardUIModel(
                title = uiState.maxWeight,
                description = R.string.title_max_weight,
                titleTextColor = R.color.red
            ),
            InfoCardUIModel(
                title = uiState.minWeight,
                description = R.string.title_min_weight,
                titleTextColor = R.color.green
            )
        )
    }
}