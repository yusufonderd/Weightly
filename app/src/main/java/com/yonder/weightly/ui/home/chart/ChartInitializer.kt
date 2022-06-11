package com.yonder.weightly.ui.home.chart

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.yonder.weightly.utils.extensions.EMPTY

object ChartInitializer {

    fun initBarChart(barChart: LineChart) {
        barChart.legend.isEnabled = false
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