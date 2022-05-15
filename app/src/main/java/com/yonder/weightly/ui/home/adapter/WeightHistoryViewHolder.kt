package com.yonder.weightly.ui.home.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yonder.weightly.databinding.ItemWeightHistoryBinding
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.utils.extensions.toFormat

const val DATE_FORMAT = "dd MMM yyyy"

class WeightHistoryViewHolder(
    view: View,
    private val onClickWeight: ((weight: WeightUIModel) -> Unit)?
) :
    RecyclerView.ViewHolder(view) {
    private val binding = ItemWeightHistoryBinding.bind(view)

    fun bind(uiModel: WeightUIModel) = with(binding) {
        binding.tvDate.text = uiModel.date.toFormat(DATE_FORMAT)
        binding.tvWeight.text = String.format("%.2f", uiModel.value)
        itemView.setOnClickListener {
            onClickWeight?.invoke(uiModel)
        }
    }
}


