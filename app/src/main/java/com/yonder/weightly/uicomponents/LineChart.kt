package com.yonder.weightly.uicomponents

import android.view.LayoutInflater
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarEntry
import com.yonder.weightly.R
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.ui.home.chart.ChartFeeder
import com.yonder.weightly.ui.home.chart.ChartInitializer

@Composable
fun LineChart(
    modifier: Modifier,
    histories: List<WeightUIModel>,
    barEntries: List<BarEntry>
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val view = LayoutInflater.from(context).inflate(R.layout.content_line_chart, null, false)
            val lineChart = view.findViewById<LineChart>(R.id.lineChart)
            ChartInitializer.initLineChart(lineChart)
            view
        },
        update = { view ->
            val lineChart = view.findViewById<LineChart>(R.id.lineChart)
            ChartFeeder.setLineChartData(
                chart = lineChart,
                histories = histories,
                barEntries = barEntries,
                context = view.context
            )
        }
    )

}