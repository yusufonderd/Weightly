package com.yonder.weightly.ui.history.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.yonder.weightly.databinding.ItemWeightHistoryBinding
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.utils.extensions.toFormat
import com.yonder.weightly.utils.setSafeOnClickListener

const val DATE_FORMAT = "dd MMM yyyy"

class WeightHistoryViewHolder(
    view: View,
    private val onClickWeight: ((weight: WeightUIModel) -> Unit)?
) :
    RecyclerView.ViewHolder(view) {
    private val binding = ItemWeightHistoryBinding.bind(view)

    fun bind(uiModel: WeightUIModel) = with(binding) {
        tvNote.text = uiModel.note
        tvEmoji.text = uiModel.emoji
        tvNote.isGone = uiModel.note.isBlank()
        tvDate.text = uiModel.date.toFormat(DATE_FORMAT)
        tvWeight.text = uiModel.valueWithUnit
        tvDifference.text = uiModel.difference
        tvDifference.setTextColor(ContextCompat.getColor(binding.root.context,uiModel.differenceColor))
        itemView.setSafeOnClickListener {
            onClickWeight?.invoke(uiModel)
        }
    }
}
