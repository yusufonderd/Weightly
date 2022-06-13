package com.yonder.weightly.ui.home.chart

import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.yonder.weightly.R
import com.yonder.weightly.utils.extensions.EMPTY

object ChartInitializer {

    fun initLineChart(chart: LineChart) {
        chart.legend.isEnabled = false
        chart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        // Disable grid lines
        chart.xAxis.setDrawGridLines(false)
        chart.axisLeft.textColor = ContextCompat.getColor(chart.context, R.color.black)
        chart.axisRight.textColor = ContextCompat.getColor(chart.context, R.color.black)
        chart.axisLeft.setDrawGridLines(false)
        chart.axisRight.setDrawGridLines(false)
        //Disable zoom
        chart.isDoubleTapToZoomEnabled = false
        chart.setPinchZoom(false)
        chart.description = Description().apply {
            text = String.EMPTY
        }
    }

    fun initBarChart(barChart: BarChart) {
        barChart.axisLeft.textColor = ContextCompat.getColor(barChart.context, R.color.black)
        barChart.axisRight.textColor = ContextCompat.getColor(barChart.context, R.color.black)
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