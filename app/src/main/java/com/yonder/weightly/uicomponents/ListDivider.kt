package com.yonder.weightly.uicomponents

import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.yonder.weightly.R

@Composable
fun ListDivider(){
    Divider(color = colorResource(id = R.color.gray_100))
}