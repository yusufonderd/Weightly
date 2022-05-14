package com.yonder.weightly.ui.home.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yonder.weightly.databinding.ItemWeightHistoryBinding
import com.yonder.weightly.domain.uimodel.WeightUIModel


class WeightHistoryViewHolder(view: View,
                        private val onClickWeight: ((weight: WeightUIModel) -> Unit)?) :
    RecyclerView.ViewHolder(view) {
    private val binding = ItemWeightHistoryBinding.bind(view)

    fun bind(story: WeightUIModel) = with(binding) {
        itemView.setOnClickListener {
            onClickWeight?.invoke(story)
        }
    }
}


