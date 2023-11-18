package com.yonder.weightly.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.uicomponents.ListDivider

@Composable
fun HistoryItemsContent(
    modifier: Modifier = Modifier,
    list: List<WeightUIModel>
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(items = list, key = { _, weight -> weight.uid }
        ) { index, weight ->
            HistoryItemRow(weight = weight)
            if (index < list.lastIndex) {
                ListDivider()
            }
        }
    }
}

@Composable
fun HistoryItemRow(weight: WeightUIModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {
            Text(text = weight.formattedDate)
            if (weight.shouldShowNote) {
                Text(text = weight.note)
            }
        }

        Row {
            Text(text = weight.emoji)
            Text(text = weight.valueWithUnit)
            Text(
                text = weight.difference,
                color = colorResource(id = weight.differenceColor)
            )
        }

    }
}