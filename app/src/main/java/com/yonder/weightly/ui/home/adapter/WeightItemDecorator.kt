package com.yonder.weightly.ui.home.adapter

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yonder.weightly.utils.extensions.dpToPx

private const val ITEM_VERTICAL_MARGIN = 4

class WeightItemDecorator(context: Context) : RecyclerView.ItemDecoration() {


    private val marginVertical by lazy {
        ITEM_VERTICAL_MARGIN.dpToPx(context)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = marginVertical
        outRect.bottom = marginVertical
    }
}
