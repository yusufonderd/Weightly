package com.yonder.weightly.ui.home.chart

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.yonder.weightly.utils.extensions.EMPTY

object BarChartInitializer {

    fun initBarChart(barChart: BarChart) {
        barChart.legend.isEnabled = false
        barChart.axisLeft.axisMinimum = 0.0f
        barChart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        // Disable grid lines
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)
        //Disable zoom
        barChart.isDoubleTapToZoomEnabled = false
        barChart.setPinchZoom(false)
        barChart.description = Description().apply {
            text = String.EMPTY
        }
    }

}