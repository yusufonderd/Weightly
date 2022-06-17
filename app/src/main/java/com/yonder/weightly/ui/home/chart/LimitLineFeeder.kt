package com.yonder.weightly.ui.home.chart

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.YAxis
import com.yonder.weightly.R
import com.yonder.weightly.utils.extensions.orZero

object LimitLineFeeder {

    private fun createLimitLine(
        context: Context,
        value: Float?,
        @ColorRes color: Int,
        @StringRes title: Int
    ): LimitLine {
        val limitLine = LimitLine(value.orZero(), context.getString(title))
        limitLine.lineWidth = 2f
        limitLine.textColor = ContextCompat.getColor(context, R.color.black)
        limitLine.lineColor = ContextCompat.getColor(context, color)
        limitLine.enableDashedLine(10f, 10f, 0f)
        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        limitLine.textSize = 10f
        return limitLine
    }

    fun removeLimitLines(lineChart: LineChart, barChart: BarChart) {
        barChart.axisLeft.removeAllLimitLines()
        lineChart.axisLeft.removeAllLimitLines()
    }

    fun addLimitLineToLineChart(
        context: Context,
        chart: LineChart,
        averageValue: Float?,
        goalValue: Float?
    ) {
        val leftAxis: YAxis = chart.axisLeft
        leftAxis.removeAllLimitLines()
        if (averageValue.orZero() > 0) {
            leftAxis.addLimitLine(
                createLimitLine(
                    context,
                    averageValue,
                    R.color.orange,
                    R.string.title_average_weight
                )
            )
        }
        if (goalValue.orZero() > 0) {
            leftAxis.addLimitLine(
                createLimitLine(
                    context,
                    goalValue,
                    R.color.black,
                    R.string.goal_weight
                )
            )
        }
    }

    fun addLimitLineToBarChart(
        context: Context,
        barChart: BarChart,
        averageValue: Float?,
        goalValue: Float?
    ) {
        val leftAxis: YAxis = barChart.axisLeft
        leftAxis.removeAllLimitLines()
        if (averageValue.orZero() > 0) {
            leftAxis.addLimitLine(
                createLimitLine(
                    context,
                    averageValue,
                    R.color.orange,
                    R.string.title_average_weight
                )
            )
        }
        if (goalValue.orZero() > 0) {
            leftAxis.addLimitLine(
                createLimitLine(
                    context,
                    goalValue,
                    R.color.black,
                    R.string.goal_weight
                )
            )
        }
    }
}