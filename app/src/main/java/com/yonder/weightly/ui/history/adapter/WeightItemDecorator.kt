package com.yonder.weightly.ui.history.adapter

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.yonder.weightly.R
import com.yonder.weightly.utils.extensions.dpToPx

private const val ITEM_VERTICAL_MARGIN = 16

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
        val viewHolder = parent.getChildViewHolder(view) as? WeightHistoryViewHolder
        val tvNote = viewHolder?.itemView?.findViewById<TextView>(R.id.tvNote)
        val isVisible = tvNote?.isVisible ?: false
        if (isVisible.not()) {
            outRect.top = marginVertical
            outRect.bottom = marginVertical
        }
    }
}