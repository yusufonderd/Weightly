package com.yonder.weightly.uicomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.yonder.errorstatelayout.R as StateLayout

@Composable
fun EmptyView(text: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Image(
            painter = painterResource(id = StateLayout.drawable.ic_baseline_inbox_72),
            contentDescription = ""
        )

        Text(text = text)
    }
}