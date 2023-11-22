package com.yonder.weightly.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yonder.weightly.R
import com.yonder.weightly.uicomponents.LineChart

const val HEIGHT_RATIO_LINE_CHART = 0.3f

@SuppressLint("MissingInflatedId")
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier.fillMaxSize(),
    uiState: HomeViewModel.UiState,
    onClickAddWeightForToday: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val heightLineChart = configuration.screenHeightDp.dp * HEIGHT_RATIO_LINE_CHART

    Column(modifier = modifier) {
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightLineChart),
            histories = uiState.histories,
            barEntries = uiState.barEntries
        )

        if (uiState.userGoal != null) {
            Text(uiState.userGoal.orEmpty())
        }

        if (uiState.shouldShowAddWeightForTodayButton) {
            Button(onClick = onClickAddWeightForToday) {
                Text(stringResource(id = R.string.add_weight_for_today))
            }
        }
    }
}

