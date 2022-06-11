package com.yonder.weightly.ui.home.chart

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.yonder.weightly.R
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.utils.extensions.EMPTY


object ChartFeeder {

    fun setChartData(
        chart: LineChart,
        histories: List<WeightUIModel?>,
        barEntries: List<BarEntry>,
        context: Context
    ) {
        val set1 = LineDataSet(barEntries, String.EMPTY)
        set1.valueFormatter = WeightValueFormatter(histories)
        set1.valueTextSize = 9f
        set1.setDrawFilled(true)
        set1.fillColor = ContextCompat.getColor(context, R.color.purple_200)
        set1.fillAlpha = 150
        set1.setCircleColor(ContextCompat.getColor(context, R.color.purple_200))
        val xAxis = chart.xAxis
        xAxis.valueFormatter = XAxisValueDateFormatter(histories)
        set1.color = ContextCompat.getColor(context, R.color.purple_200)
        val dataSets: java.util.ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(set1)
        val data = LineData(dataSets)
        chart.data = data
        //Set marker view
        val markerView = WeightMarkerView(context, histories)
        markerView.chartView = chart
        chart.marker = markerView

        chart.invalidate()
    }
}