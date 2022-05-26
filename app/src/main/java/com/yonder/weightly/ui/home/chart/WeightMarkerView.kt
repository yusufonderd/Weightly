package com.yonder.weightly.ui.home.chart

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.yonder.weightly.R
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.utils.extensions.orZero

@SuppressLint("ViewConstructor")
class WeightMarkerView(context: Context, var histories: List<WeightUIModel?>) :
    MarkerView(context, R.layout.marker_view_weight) {

    private val tvMarkerTitle = findViewById<TextView>(R.id.tvMarkerTitle);

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        val history = histories[e?.x.orZero().toInt()]
        tvMarkerTitle.text = "${history?.emoji} ${history?.valueText}"
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }

}